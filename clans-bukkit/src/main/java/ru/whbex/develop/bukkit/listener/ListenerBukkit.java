package ru.whbex.develop.bukkit.listener;

import ru.whbex.develop.bukkit.MainBukkit;
import ru.whbex.develop.common.ClansPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class ListenerBukkit implements Listener {

    @EventHandler(priority =  EventPriority.HIGHEST)
    public void on(PlayerLoginEvent e){
        if(ClansPlugin.Context.INSTANCE.plugin.getPlayerActor(e.getPlayer().getUniqueId()) == null)
            ((MainBukkit) ClansPlugin.Context.INSTANCE.plugin).registerActor(e.getPlayer().getUniqueId());
    }
}
