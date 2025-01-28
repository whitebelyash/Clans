package ru.whbex.develop.clans.common.cmd.exec;

import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.cmd.CommandActor;

public interface Command {

    void execute(CommandActor actor, Command command, String label, String... args);
    String name();
    default String permission() {
        return ClansPlugin.Context.NAME + '.' + name();
    }

    default boolean isRoot(){
        return false;
    }
    default boolean isAsync(){
        return false;
    }
}
