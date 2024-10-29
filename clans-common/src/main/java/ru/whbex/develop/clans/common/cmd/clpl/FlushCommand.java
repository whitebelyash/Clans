package ru.whbex.develop.clans.common.cmd.clpl;

import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.Constants;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.exec.Command;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

public class FlushCommand implements Command {
    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {
        actor.sendMessageT("command.clpl.flush.start");
        Future<Boolean> flush = ClansPlugin.Context.INSTANCE.plugin.getClanManager().exportAll();
        // Await for task result
        ClansPlugin.Context.INSTANCE.plugin.getTaskScheduler().runAsync(() -> {
            try {
                boolean res = flush.get(Constants.TASK_WAIT_TIMEOUT, Constants.TASK_WAIT_TIMEOUT_UNIT);
                actor.sendMessageT(res ? "command.clpl.flush.success" : "command.clpl.flush.fail");
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                actor.sendMessageT("command.clpl.flush.fail");
            }
        });
    }

    @Override
    public String name() {
        return "flush";
    }
}
