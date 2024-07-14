package ru.whbex.develop.common.cmd;

public interface CommandInterface {

    void execute(CommandActor actor, CommandInterface command, String label, String... args);
}
