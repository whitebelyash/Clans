package ru.whbex.develop.clans.common.cmd.clpl;

import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.exec.Command;

public class ForceSyncCommand implements Command {
    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {
        actor.sendMessageT("command.clpl.forcesync.start");
        ClansPlugin.clanManager().triggerSync();
    }

    @Override
    public String name() {
        return "forcesync";
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
