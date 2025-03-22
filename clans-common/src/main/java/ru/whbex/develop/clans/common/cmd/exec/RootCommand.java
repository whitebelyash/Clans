package ru.whbex.develop.clans.common.cmd.exec;

import ru.whbex.develop.clans.common.cmd.ActorProxy;
import ru.whbex.develop.clans.common.cmd.CommandActor;

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
        try {
            cmds.get(c).execute(actor, command, label, args);
        } catch (CommandError e) {
            actor.sendMessageT(e.getMessage() == null ? "meta.command.unknown-error" : e.getMessage(), e.getArgs());
        } catch (CommandUsageError e){
            // TODO: Implement args localization
            actor.sendMessageT("command." + cmds.get(c).name() + ".usage");
        }
        // TODO: Minor fix: catch command exceptions too
    }


    @Override
    public boolean isRoot() {
        return true;
    }
}
