package ru.whbex.develop.bukkit.misc;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.whbex.develop.core.Clans;
import ru.whbex.develop.core.player.CommandPerformer;

public class BukkitUtils {
    public static CommandPerformer asPerformer(CommandSender sender){
        if(!(sender instanceof Player))
            return Clans.instance().getPlayerManager().getConsole();
        else
            return Clans.instance().getPlayerManager().getPlayer(((Player) sender).getUniqueId());
    }
}
