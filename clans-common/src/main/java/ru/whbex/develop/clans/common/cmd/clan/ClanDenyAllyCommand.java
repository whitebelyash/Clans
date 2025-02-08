package ru.whbex.develop.clans.common.cmd.clan;

import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.exec.Command;

public class ClanDenyAllyCommand implements Command {
    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {

    }

    @Override
    public String name() {
        return "deny-ally";
    }
}
