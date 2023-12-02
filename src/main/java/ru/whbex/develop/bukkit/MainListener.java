package ru.whbex.develop.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.whbex.develop.Clans;

import java.util.NoSuchElementException;

public class MainListener implements Listener {

    @EventHandler(priority =  EventPriority.HIGHEST)
    public void on(PlayerLoginEvent e){
        Clans.instance().getPlayerManager().createPlayer(e.getPlayer().getUniqueId(), e.getPlayer().getName());
    }
}
