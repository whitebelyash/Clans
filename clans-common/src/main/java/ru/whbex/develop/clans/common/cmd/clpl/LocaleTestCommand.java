package ru.whbex.develop.clans.common.cmd.clpl;

import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.exec.Command;
import ru.whbex.develop.clans.common.cmd.exec.CommandError;

public class LocaleTestCommand implements Command {
    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {
        if(args.length < 2)
            throw new CommandError("meta.command.usage");
        String key = args[1];
        String value = ClansPlugin.Context.INSTANCE.plugin.getLanguage().getPhrase(key);
        if(key.equals(value))
            throw new CommandError("No string " + key + " found in language file");
        actor.sendMessage(key + ": " +value);
    }

    @Override
    public String name() {
        return "localetest";
    }
}
