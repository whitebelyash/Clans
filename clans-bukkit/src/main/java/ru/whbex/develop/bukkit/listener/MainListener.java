package ru.whbex.develop.bukkit.listener;

import ru.whbex.develop.common.Clans;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class MainListener implements Listener {

    @EventHandler(priority =  EventPriority.HIGHEST)
    public void on(PlayerLoginEvent e){
        Clans.instance().getPlayerManager().createPlayer(e.getPlayer().getUniqueId(), e.getPlayer().getName());
    }
}
