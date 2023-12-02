package ru.whbex.develop.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;
import ru.whbex.develop.Constants;
import ru.whbex.develop.player.PlayerWrapper;

import java.util.UUID;

public class PlayerWrapperBukkit implements PlayerWrapper {

    @Override
    public void sendMessage(UUID receiver, String string) {
        if(isOnline(receiver))
            Bukkit.getPlayer(receiver).sendMessage(string);
    }
    public void sendMessageColorized(UUID receiver, String string){
        sendMessage(receiver, ChatColor.translateAlternateColorCodes(Constants.PLAYER_COLOR_SYMBOL, string));

    }
    public boolean isOnline(UUID target){
        return Bukkit.getPlayer(target) != null;
    }
    public void teleport(UUID target, int x, int y, int z, String world){
        if(Bukkit.getWorld(world) == null)
            throw new IllegalArgumentException("Unknown world " + world);
        if(isOnline(target))
            Bukkit.getPlayer(target).teleport(new Location(Bukkit.getWorld(world), x, y, z), PlayerTeleportEvent.TeleportCause.PLUGIN);

    }
}
