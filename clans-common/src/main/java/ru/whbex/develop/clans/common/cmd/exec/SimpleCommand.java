package ru.whbex.develop.clans.common.cmd.exec;

import ru.whbex.develop.clans.common.cmd.ActorProxy;
import ru.whbex.develop.clans.common.cmd.CommandActor;

public abstract class SimpleCommand<T> implements ActorProxy<T>, Command {
    private final Command cmd;

    public SimpleCommand(Command command){
        this.cmd = command;
    }
    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {
        try {
            cmd.execute(actor, command, label, args);
        } catch (CommandError e) {
            actor.sendMessageT(e.getMessage() == null ? "meta.command.unknown-error" : e.getMessage(), e.getArgs());
        } catch (CommandUsageError e){
            // TODO: Implement args localization
            actor.sendMessageT(cmd.description());
        }
    }


}
