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
import ru.whbex.lib.log.Debug;
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
    public PlayerManagerBukkit() {
        // Debug.print("Creating players table...");
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
        Debug.print("Registered actor {0}", actor);
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
        Debug.print("Registered actor " + p);
    }
    public void unregisterPlayerActor(UUID id) throws SQLException {
        if(!actors.containsKey(id))
            return;
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
            Debug.print("bukkit online > onlineActors, updating");
            Bukkit.getOnlinePlayers().forEach(p -> registerPlayerActor(p.getUniqueId()));
        }

    }

    @Override
    public void makeOnline(UUID id) {
        if(actors.containsKey(id) && !onlineActors.containsKey(id) && actors.get(id).isOnline()) {
            onlineActors.put(id, actors.get(id));
            Debug.print("makeOnline() uuid: " + id);
        }
    }

    @Override
    public void makeOffline(UUID id) {
        if(actors.containsKey(id) && onlineActors.containsKey(id) && !actors.get(id).isOnline()) {
            onlineActors.put(id, actors.get(id));
            Debug.print("makeOffline() uuid: " + id);
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
