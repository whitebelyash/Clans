package ru.whbex.develop.clans.bukkit.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import ru.whbex.develop.clans.bukkit.player.PlayerManagerBukkit;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.clan.ClanCommand;

import java.util.List;

public class ClanCommandBukkit extends ClanCommand<CommandSender> implements CommandExecutor, TabCompleter {
    @Override
    public CommandActor asActor(CommandSender performer) {
        return ((PlayerManagerBukkit) ClansPlugin.Context.INSTANCE.plugin.getPlayerManager()).asCommandActor(performer);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        this.execute(asActor(commandSender), this, s, strings);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        // TODO: Implement further
        return cmdList;
    }
}
