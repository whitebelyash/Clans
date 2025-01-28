package ru.whbex.develop.clans.common.cmd.clpl;

import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.cmd.ActorProxy;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.exec.RootCommand;

public abstract class ClansPluginCommand<T> extends RootCommand<T>{

    @Override
    protected void registerAll() {
        register(new ReloadCommand());
        register(new pageviewtest());
        register(new LocaleInfoCommand());
        register(new LocaleTestCommand());
        register(new ActorListCommand());
    }

    @Override
    protected void root(CommandActor a) {
        a.sendMessage("/clansplugin " + String.join("|", cmdList));
    }
    @Override
    public String name() {
        return "clansplugin";
    }
}
