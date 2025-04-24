package ru.whbex.develop.clans.bukkit.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.clpl.ClansPluginCommand;

import java.util.List;
// TODO: Discover better way to extend commands in implementations
public class ClansPluginCommandBukkit extends ClansPluginCommand<CommandSender> implements CommandExecutor, TabCompleter {
    @Override
    public CommandActor asActor(CommandSender performer) {
        return StaticActorProxy.asActor(performer);
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
