package ru.whbex.develop.clans.common.cmd;

public interface CommandInterface {

    void execute(CommandActor actor, CommandInterface command, String label, String... args);
}
