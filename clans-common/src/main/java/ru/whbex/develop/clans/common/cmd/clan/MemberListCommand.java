package ru.whbex.develop.clans.common.cmd.clan;

import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.exec.Command;
import ru.whbex.develop.clans.common.cmd.exec.CommandError;
import ru.whbex.develop.clans.common.player.PlayerActor;

public class MemberListCommand implements Command {
    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {
        throw new UnsupportedOperationException("WIP");


    }
    private void executeSelf(PlayerActor a){
        if(!a.hasClan())
            throw new CommandError("meta.command.clan-needed");

    }

    @Override
    public String name() {
        return "list-members";
    }
}
