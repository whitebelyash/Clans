package ru.whbex.develop.clans.bukkit.player;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.clan.bridge.Bridge;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.event.EventHandler;
import ru.whbex.develop.clans.common.event.EventSystem;
import ru.whbex.develop.clans.common.event.def.PlayerEvent;
import ru.whbex.develop.clans.common.player.PlayerActor;
import ru.whbex.develop.clans.common.player.PlayerManager;
import ru.whbex.develop.clans.common.player.ConsoleActor;
import ru.whbex.develop.clans.common.player.PlayerProfile;
import ru.whbex.develop.clans.common.task.DatabaseService;
import ru.whbex.lib.log.Debug;
import ru.whbex.lib.log.LogContext;
import ru.whbex.lib.sql.SQLAdapter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// TODO: Huge refactor
public class PlayerManagerBukkit implements PlayerManager {
    private final Map<UUID, PlayerActor> actors = new HashMap<>();
    private final Map<String, PlayerActor> actorsN = new HashMap<>();
    private final Map<UUID, PlayerActor> onlineActors = new HashMap<>();

    private final ConsoleActorBukkit consoleActor = new ConsoleActorBukkit();



    public PlayerManagerBukkit() {
        this.createTables();
        Debug.print("Registering event callbacks...");
        EventSystem.PLAYER_JOIN.register(onJoin);
        EventSystem.PLAYER_LEFT.register(onQuit);
    }


    @Override
    public PlayerActor getPlayerActor(UUID id) {
        return actors.get(id);
    }

    @Override
    public PlayerActor getPlayerActor(String nickname) {
        return actorsN.get(nickname);
    }

    @Override
    public PlayerActor getOnlinePlayerActor(UUID id) {
        return onlineActors.get(id);
    }

    @Override
    public void registerPlayerActor(PlayerActor actor) {
        actors.put(actor.getUniqueId(), actor);
        if(actor.getProfile() == null){
            Debug.print("Player profile not set, fetching...");
            // Set stub profile before real is fetched
            actor.setProfile(new PlayerProfile(actor.getUniqueId(), null, -1, 0));
            // Set name if online
            if(actor.isOnline()) {
                actor.getProfile().setName(((PlayerActorBukkit) actor).getPlayer().getName());
                Debug.print("name set!");
            }
            // We'll ignore fetch progress for now
            DatabaseService.getAsyncExecutor(SQLAdapter::preparedQuery)
                    .sql(FETCH_PROFILE_STMT)
                    .setPrepared(ps -> ps.setString(1, actor.getUniqueId().toString()))
                    .queryCallback(resp -> {
                        if(resp.resultSet().next())
                            do {
                                String old = actor.getProfile() != null ? actor.getProfile().getName() : null;
                                actor.setProfile(PlayerProfile.fromResultSet(resp.resultSet()));
                                if(old != null)
                                    actor.getProfile().setName(old);
                                actorsN.put(actor.getProfile().getName(), actor);
                                Debug.print("Profile successfully fetched");
                            } while(resp.resultSet().next());
                        else {
                            Debug.print("PlayerProfile not found for {0}", actor);
                        }
                        return null;
                    })
                    .executeAsync();
        }
        Debug.print("Registered/updated actor {0}", actor);
    }

    @Override
    public void registerPlayerActor(UUID id) {
        if(actors.containsKey(id)) {
            // makeOnline(id);
            return;
        }
        PlayerActor p = new PlayerActorBukkit(id);
        registerPlayerActor(p);
    }
    public void unregisterPlayerActor(UUID id) throws SQLException {
        if(!actors.containsKey(id))
            return;
    }
    private static void profileToPrepStatement(PlayerProfile pp, PreparedStatement ps) throws SQLException{
        ps.setString(1, pp.getOwner().toString());
        ps.setString(2, pp.getName());
        ps.setLong(3, pp.getRegDate());
        ps.setLong(4, pp.getLastSeen());
    }
    // TODO: Move to PlayerManager interface
    public void savePlayerActor(UUID id){
        if(!actors.containsKey(id))
            return;
        PlayerActor actor = actors.get(id);
        Debug.print("Saving PlayerProfile for {0}", actor);
        SQLAdapter<Void>.Executor<Void> ex = DatabaseService.getAsyncExecutor(SQLAdapter::preparedUpdate)
                .setVerbose(true)
                .setPrepared(ps -> {
                    profileToPrepStatement(actor.getProfile(), ps);
                })
                .updateCallback(resp -> {
                    Debug.print("Updated {0} rows", resp.updateResult());
                    return null;
                });
        switch(DatabaseService.getDatabaseBackend()){
            case H2 -> ex.sql(INSERT_PROFILE_STMT_H2);
            case SQLITE -> ex.sql(INSERT_PROFILE_STMT_SQLITE);
            default -> throw new IllegalArgumentException("Unsupported database backend " + DatabaseService.getDatabaseBackend());
        }
        ex.executeAsync();
    }

    @Override
    public PlayerActor getOrRegisterPlayerActor(PlayerActor actor) {
        if(actors.containsKey(actor.getUniqueId()))
            return actor;
        registerPlayerActor(actor);
        return actor;
    }

    @Override
    public PlayerActor getOrRegisterPlayerActor(UUID id) {
        registerPlayerActor(id);
        return actors.get(id);
    }

    @Override
    public Collection<PlayerActor> getOnlinePlayerActors() {
        return onlineActors.values();
    }

    private final PlayerEvent.PlayerEventHandler onJoin = (id) -> {
        Debug.print("onJoin() " + id);
        if (!actors.containsKey(id))
            return;
        PlayerActor actor = actors.get(id);
        Debug.print("Updating player name...");
        String bukkitName = ((PlayerActorBukkit) actor).getPlayer().getName();
        if (actor.getProfile().getName() == null) {
            actor.getProfile().setName(bukkitName);
        }
        if (actor.getProfile().getName().equals(bukkitName)) {
            actorsN.remove(actor.getProfile().getName());
            actor.getProfile().setName(bukkitName);
        }
        actorsN.put(bukkitName, actor);
        onlineActors.put(id, actor);
    };
    private final PlayerEvent.PlayerEventHandler onQuit = ((id) -> {
        Debug.print("onQuit() " + id);
        onlineActors.remove(id);
        savePlayerActor(id);
    });

    @Override
    public ConsoleActor getConsoleActor() {
        return consoleActor;
    }

    public CommandActor asCommandActor(CommandSender sender){
        return sender instanceof Player ? (CommandActor)this.getOrRegisterPlayerActor(((Player) sender).getUniqueId()) : consoleActor;
    }
}
