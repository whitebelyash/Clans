package ru.whbex.develop.clans.common.cmd;

public class CommandManager implements CommandInterface {

    @Override
    public void execute(CommandActor actor, CommandInterface command, String label, String... args) {
        actor.sendMessage("Message");
    }
}
