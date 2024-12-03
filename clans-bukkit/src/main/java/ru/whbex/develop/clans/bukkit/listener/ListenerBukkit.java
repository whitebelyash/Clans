package ru.whbex.develop.clans.bukkit.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.whbex.develop.clans.bukkit.player.PlayerManagerBukkit;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.event.EventSystem;
import ru.whbex.lib.log.Debug;

public class ListenerBukkit implements Listener {
    @EventHandler(priority =  EventPriority.HIGHEST)
    public void on(PlayerLoginEvent e){
        ClansPlugin.Context.INSTANCE.plugin.getPlayerManager().registerPlayerActor(e.getPlayer().getUniqueId());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerJoinEvent e){
        EventSystem.PLAYER_JOIN.call(e.getPlayer().getUniqueId());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerQuitEvent e){
        EventSystem.PLAYER_LEFT.call(e.getPlayer().getUniqueId());
    }
}
