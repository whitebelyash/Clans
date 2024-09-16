package ru.whbex.develop.clans.bukkit.listener;

import org.bukkit.event.player.PlayerQuitEvent;
import ru.whbex.develop.clans.bukkit.MainBukkit;
import ru.whbex.develop.clans.common.ClansPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class ListenerBukkit implements Listener {

    @EventHandler(priority =  EventPriority.HIGHEST)
    public void on(PlayerLoginEvent e){
        ClansPlugin.Context.INSTANCE.plugin.getPlayerManager().registerPlayerActor(e.getPlayer().getUniqueId());
    }
    public void on(PlayerQuitEvent e){
        ClansPlugin.Context.INSTANCE.plugin.getPlayerManager().makeOffline(e.getPlayer().getUniqueId());
    }
}
