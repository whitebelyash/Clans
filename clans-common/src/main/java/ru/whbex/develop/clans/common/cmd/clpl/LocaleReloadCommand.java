package ru.whbex.develop.clans.common.cmd.clpl;

import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.exec.Command;
import ru.whbex.lib.log.LogContext;

public class LocaleReloadCommand implements Command {
    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {
        /* Yes, this is a simple Ctrl+C/Ctrl+V from ReloadCommand. (Why not Command+C? Because I'm writing this on Linux) */
        Runnable task = () -> {
            actor.sendMessageT("command.clpl.reload.start");
            try {
                ClansPlugin.Context.INSTANCE.plugin.reloadLangFiles();
            } catch (Exception e) {
                actor.sendMessageT("command.clpl.reload.fail");
                LogContext.log(Level.ERROR, "Failed to reload language. See below stacktrace for more info");
                e.printStackTrace();
                return;
            }
            actor.sendMessageT("command.clpl.reload.complete");
        };
        // We'll ignore task here, no need to overengineer this
        ClansPlugin.Context.INSTANCE.plugin.getTaskScheduler().runAsync(task);
    }

    @Override
    public String name() {
        return "locale-reload";
    }
}
