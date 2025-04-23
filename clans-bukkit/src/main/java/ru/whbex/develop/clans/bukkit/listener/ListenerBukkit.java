package ru.whbex.develop.clans.bukkit.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.slf4j.event.Level;
import ru.whbex.develop.clans.bukkit.player.PlayerActorBukkit;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.event.EventSystem;
import ru.whbex.lib.log.LogContext;

public class ListenerBukkit implements Listener {
    @EventHandler(priority =  EventPriority.HIGHEST)
    public void on(PlayerLoginEvent e){

    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerJoinEvent e){
        PlayerActorBukkit actor = (PlayerActorBukkit) ClansPlugin.playerManager().createActorObject(e.getPlayer().getUniqueId());
        actor.setBukkitPlayer(e.getPlayer());
        ClansPlugin.playerManager().loadPlayerActor(actor, true);
        EventSystem.PLAYER_JOIN.call(e.getPlayer().getUniqueId());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerQuitEvent e){
        // Player object won't null itself, and it will still point to the exited one, so we need to remove it here ourselves
        PlayerActorBukkit actor = (PlayerActorBukkit) ClansPlugin.playerManager().playerActor(e.getPlayer().getUniqueId());
        if(actor == null){
            LogContext.log(Level.WARN, "Player left with no PlayerActor object, this is not ok!");
            return; // do not call player left event if actor obj doesn't exist
        }
        EventSystem.PLAYER_LEFT.call(actor.getUniqueId());
    }
}
