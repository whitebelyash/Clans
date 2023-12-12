package ru.whbex.develop.bukkit.misc;

import ru.whbex.develop.common.Clans;
import ru.whbex.develop.common.player.CommandPerformer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BukkitUtils {
    public static CommandPerformer asPerformer(CommandSender sender){
        if(!(sender instanceof Player))
            return Clans.instance().getPlayerManager().getConsole();
        else
            return Clans.instance().getPlayerManager().getPlayer(((Player) sender).getUniqueId());
    }
}
