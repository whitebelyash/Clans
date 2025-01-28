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
        throw new RuntimeException("Remove");
    }

    @Override
    public String name() {
        return "flush";
    }
}
