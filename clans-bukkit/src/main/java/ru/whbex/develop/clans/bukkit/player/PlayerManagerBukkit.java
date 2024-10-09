package ru.whbex.develop.clans.bukkit.player;

import org.bukkit.Bukkit;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.clan.loader.Bridge;
import ru.whbex.develop.clans.common.player.PlayerActor;
import ru.whbex.develop.clans.common.player.PlayerManager;
import ru.whbex.develop.clans.common.wrap.ConsoleActor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManagerBukkit implements PlayerManager {
    private final Map<UUID, PlayerActor> actors = new HashMap<>();
    private final Map<String, PlayerActor> actorsN = new HashMap<>();
    private final Map<UUID, PlayerActor> onlineActors = new HashMap<>();

    private final ConsoleActorBukkit consoleActor = new ConsoleActorBukkit();

    private final Bridge bridge;
    public PlayerManagerBukkit(Bridge bridge){
        this.bridge = bridge;
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
        ClansPlugin.dbg("Registered actor " + actor);
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
        ClansPlugin.dbg("Registered actor " + p);
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
            ClansPlugin.dbg("bukkit online > onlineActors, updating");
            Bukkit.getOnlinePlayers().forEach(p -> {
                registerPlayerActor(p.getUniqueId());
            });
        }

    }

    @Override
    public void makeOnline(UUID id) {
        if(actors.containsKey(id) && !onlineActors.containsKey(id) && actors.get(id).isOnline()) {
            onlineActors.put(id, actors.get(id));
            ClansPlugin.dbg("makeOnline() uuid: " + id);
        }
    }

    @Override
    public void makeOffline(UUID id) {
        if(actors.containsKey(id) && onlineActors.containsKey(id) && !actors.get(id).isOnline()) {
            onlineActors.put(id, actors.get(id));
            ClansPlugin.dbg("makeOffline() uuid: " + id);
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
}
