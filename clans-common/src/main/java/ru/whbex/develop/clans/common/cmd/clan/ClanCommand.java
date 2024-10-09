package ru.whbex.develop.clans.common.cmd.clan;

import ru.whbex.develop.clans.common.cmd.ActorProxy;
import ru.whbex.develop.clans.common.cmd.Command;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.CommandError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ClanCommand<T> implements ActorProxy<T>, Command {

    protected final Map<String, Command> cmds = new HashMap<>();
    protected final List<String> cmdList;

    public ClanCommand(){
        cmds.put("clan", this);
        cmds.put("list", new ClanListCommand());
        cmds.put("create", new ClanCreateCommand());

        cmds.put("help", new ClanHelpCommand(cmds.values()));
        this.cmdList = new ArrayList<>(cmds.keySet());


    }

    private void root(CommandActor a){
        cmds.get("help").execute(a, cmds.get("help"), null);
    }

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
        } catch (CommandError e){
            actor.sendMessageT(e.getMessage(), e.getArgs());
        }


    }

    @Override
    public String name() {
        return "clan";
    }

    @Override
    public boolean isRoot() {
        return true;
    }
}
