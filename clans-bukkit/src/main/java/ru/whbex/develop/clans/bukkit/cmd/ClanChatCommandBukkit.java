package ru.whbex.develop.clans.bukkit.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import ru.whbex.develop.clans.common.cmd.ClanChatCommand;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.exec.SimpleCommand;

import java.util.List;

public class ClanChatCommandBukkit extends SimpleCommand<CommandSender> implements CommandExecutor, TabCompleter {
    public ClanChatCommandBukkit() {
        super(new ClanChatCommand());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return List.of();
    }

    @Override
    public CommandActor asActor(CommandSender performer) {
        return StaticActorProxy.asActor(performer);
    }

    @Override
    public String name() {
        return "";
    }
}
