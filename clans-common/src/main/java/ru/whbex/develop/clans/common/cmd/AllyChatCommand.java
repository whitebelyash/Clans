package ru.whbex.develop.clans.common.cmd;

import ru.whbex.develop.clans.common.cmd.exec.Command;

public class AllyChatCommand implements Command {
    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {

    }

    @Override
    public String name() {
        return "ally-chat";
    }
}
