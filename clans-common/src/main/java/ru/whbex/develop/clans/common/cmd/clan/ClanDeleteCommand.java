package ru.whbex.develop.clans.common.cmd.clan;

import ru.whbex.develop.clans.common.cmd.exec.Command;
import ru.whbex.develop.clans.common.cmd.CommandActor;

public class ClanDeleteCommand implements Command {
    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {

    }

    @Override
    public String name() {
        return "delete";
    }
}
