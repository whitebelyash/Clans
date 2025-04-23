package ru.whbex.develop.clans.bukkit.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.whbex.develop.clans.bukkit.player.PlayerActorBukkit;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.event.EventSystem;

public class ListenerBukkit implements Listener {
    @EventHandler(priority =  EventPriority.HIGHEST)
    public void on(PlayerLoginEvent e){

    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerJoinEvent e){
        PlayerActorBukkit actor = (PlayerActorBukkit) ClansPlugin.playerManager().loadPlayerActor(e.getPlayer().getUniqueId());
        actor.setBukkitPlayer(e.getPlayer());
        EventSystem.PLAYER_JOIN.call(e.getPlayer().getUniqueId());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerQuitEvent e){
        // Remove playerobj
        // TODO: Check for NPE
        ((PlayerActorBukkit) ClansPlugin.playerManager().loadPlayerActor(e.getPlayer().getUniqueId())).setBukkitPlayer(null);
        EventSystem.PLAYER_LEFT.call(e.getPlayer().getUniqueId());
    }
}
