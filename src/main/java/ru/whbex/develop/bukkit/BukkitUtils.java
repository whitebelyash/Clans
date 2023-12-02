package ru.whbex.develop.bukkit;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.whbex.develop.Clans;
import ru.whbex.develop.player.CommandPerformer;
import ru.whbex.develop.player.PlayerManager;

public class BukkitUtils {
    public static CommandPerformer asPerformer(CommandSender sender){
        if(!(sender instanceof Player))
            return Clans.instance().getPlayerManager().getConsole();
        else
            return Clans.instance().getPlayerManager().getPlayer(((Player) sender).getUniqueId());
    }
}
