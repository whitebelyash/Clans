package ru.whbex.develop.clans.bukkit.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.clan.loader.Bridge;
import ru.whbex.develop.clans.common.player.PlayerActor;
import ru.whbex.develop.clans.common.player.PlayerManager;
import ru.whbex.develop.clans.common.wrap.ConsoleActor;

import java.util.*;

public class PlayerManagerBukkit implements PlayerManager {
    private Map<UUID, PlayerActor> actors = new HashMap<>();
    private Map<String, PlayerActor> actorsN = new HashMap<>();
    private Map<UUID, PlayerActor> onlineActors = new HashMap<>();

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
    public void registerPlayerActor(PlayerActor actor) {
        if(actors.containsKey(actor.getUniqueId()))
            return;
        actors.put(actor.getUniqueId(), actor);
        ClansPlugin.dbg("Registered actor " + actor);
    }

    @Override
    public void registerPlayerActor(UUID id) {
        if(actors.containsKey(id))
            return;
        PlayerActor p = new PlayerActorBukkit(id);
        actors.put(id, p);
        ClansPlugin.dbg("Registered actor " + p);
    }

    @Override
    public PlayerActor getOrRegisterPlayerActor(PlayerActor actor) {
        if(actors.containsKey(actor.getUniqueId()))
            return actor;
        actors.put(actor.getUniqueId(), actor);
        return actor;
    }

    @Override
    public PlayerActor getOrRegisterPlayerActor(UUID id) {
        registerPlayerActor(id);
        return actors.get(id);
    }

    @Override
    public void updateActors() {

    }

    @Override
    public void makeOnline(UUID id) {
        if(actors.containsKey(id) && !onlineActors.containsKey(id) && actors.get(id).isOnline())
            onlineActors.put(id, actors.get(id));
    }

    @Override
    public void makeOffline(UUID id) {
        if(actors.containsKey(id) && onlineActors.containsKey(id) && !actors.get(id).isOnline())
            onlineActors.put(id, actors.get(id));
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
