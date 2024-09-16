package ru.whbex.develop.clans.common.cmd;

import ru.whbex.develop.clans.common.ClansPlugin;

public interface Command {

    void execute(CommandActor actor, Command command, String label, String... args);
    String name();
    default String permission() {
        return ClansPlugin.Context.NAME + '.' + name();
    }
}
