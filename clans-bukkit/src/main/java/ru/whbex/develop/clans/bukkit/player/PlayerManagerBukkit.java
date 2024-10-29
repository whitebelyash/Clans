package ru.whbex.develop.clans.bukkit.player;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.clan.bridge.Bridge;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.player.PlayerActor;
import ru.whbex.develop.clans.common.player.PlayerManager;
import ru.whbex.develop.clans.common.player.ConsoleActor;
import ru.whbex.lib.log.LogDebug;
import ru.whbex.lib.sql.SQLAdapter;

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
    private final SQLAdapter adapter;
    public PlayerManagerBukkit(SQLAdapter adapter) throws SQLException {
        this.adapter = adapter;
        LogDebug.print("Creating players table...");
        int aff = adapter.update("CREATE TABLE IF NOT EXISTS players(" +
                "id varchar(36), " +
                "name varchar(16)" +
                ");");
        LogDebug.print("Affected {0} rows", aff);
    }

    SQLAdapter getAdapter(){
        return adapter;
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
        if(actors.containsKey(actor.getUniqueId()))
            return;
        actors.put(actor.getUniqueId(), actor);
        if(actor.getName() != null)
            actorsN.put(actor.getName(), actor);
        if(actor.isOnline())
            onlineActors.put(actor.getUniqueId(), actor);
        LogDebug.print("Registered actor {0}", actor);
    }

    @Override
    public void registerPlayerActor(UUID id) {
        if(actors.containsKey(id)) {
            makeOnline(id);
            return;
        }
        PlayerActor p = new PlayerActorBukkit(id);
        actors.put(id, p);
        if(p.getName() != null)
            actorsN.put(p.getName(), p);
        if(p.isOnline())
            onlineActors.put(id, p);
        LogDebug.print("Registered actor " + p);
    }
    public void unregisterPlayerActor(UUID id) throws SQLException {
        if(!actors.containsKey(id))
            return;
        adapter.updatePrepared("INSERT INTO player VALUES (?, ?);", ps -> {
            ps.setString(0, id.toString());
            ps.setString(1, actors.get(id).getName());
            return true;
        });
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
    public void updateActors() {
        // Register all previously unregistered online actors
        if(Bukkit.getOnlinePlayers().size() > onlineActors.values().size()){
            LogDebug.print("bukkit online > onlineActors, updating");
            Bukkit.getOnlinePlayers().forEach(p -> {
                registerPlayerActor(p.getUniqueId());
            });
        }

    }

    @Override
    public void makeOnline(UUID id) {
        if(actors.containsKey(id) && !onlineActors.containsKey(id) && actors.get(id).isOnline()) {
            onlineActors.put(id, actors.get(id));
            LogDebug.print("makeOnline() uuid: " + id);
        }
    }

    @Override
    public void makeOffline(UUID id) {
        if(actors.containsKey(id) && onlineActors.containsKey(id) && !actors.get(id).isOnline()) {
            onlineActors.put(id, actors.get(id));
            LogDebug.print("makeOffline() uuid: " + id);
        }
    }

    @Override
    public boolean isOnline(UUID id) {
        return actors.containsKey(id) && actors.get(id).isOnline();
    }

    @Override
    public Collection<PlayerActor> getOnlinePlayerActors() {
        return onlineActors.values();
    }

    @Override
    public ConsoleActor getConsoleActor() {
        return consoleActor;
    }

    public CommandActor asCommandActor(CommandSender sender){
        return sender instanceof Player ? (CommandActor)this.getOrRegisterPlayerActor(((Player) sender).getUniqueId()) : consoleActor;
    }
}
