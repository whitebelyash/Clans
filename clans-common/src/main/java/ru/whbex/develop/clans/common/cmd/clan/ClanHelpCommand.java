package ru.whbex.develop.clans.common.cmd.clan;

import ru.whbex.develop.clans.common.cmd.exec.Command;
import ru.whbex.develop.clans.common.cmd.CommandActor;

import java.util.Collection;

public class ClanHelpCommand implements Command {
    private final Collection<String> commands;
    ClanHelpCommand(Collection<Command> commands){
        this.commands = commands.stream().map(Command::name).toList();
    }
    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {
        actor.sendMessage("/clan " + String.join("|", commands));
    }

    @Override
    public String name() {
        return "help";
    }
}
