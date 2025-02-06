package ru.whbex.develop.clans.common.cmd.exec;

import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.cmd.ActorProxy;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.task.Task;
import ru.whbex.lib.log.Debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RootCommand<T> implements ActorProxy<T>, Command {

    public RootCommand(){
        registerAll();
    }

    protected final Map<String, Command> cmds = new HashMap<>();
    protected final List<String> cmdList = new ArrayList<>();
    protected abstract void registerAll();
    protected void register(Command command){
        cmds.put(command.name(), command);
        cmdList.add(command.name());
    }
    protected abstract void root(CommandActor a);
    protected String rootName = name();

    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {
        if(args.length < 1){
            root(actor);
            return;
        }
        String c = args[0];
        if(!cmds.containsKey(c)){
            root(actor);
            return;
        }
        Runnable cr = () -> {
            try {
                cmds.get(c).execute(actor, command, label, args);
            } catch (CommandError e) {
                actor.sendMessageT(e.getMessage() == null ? "meta.command.unknown-error" : e.getMessage(), e.getArgs());
            } catch (CommandUsageError e){
                // TODO: Implement args localization
                actor.sendMessageT("command." + cmds.get(c).name() + ".usage");
            }
            Debug.print("Command {0} complete", c);
        };
        Task task = command.isAsync() ? ClansPlugin.TaskScheduler().runAsync(cr) : ClansPlugin.TaskScheduler().run(cr);
        Debug.print("Command {0} started with taskid {1} (async: {2})", c, task.id(), !task.sync());
    }


    @Override
    public boolean isRoot() {
        return true;
    }
}
