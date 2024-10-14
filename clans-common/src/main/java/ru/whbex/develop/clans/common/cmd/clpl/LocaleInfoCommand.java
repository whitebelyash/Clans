package ru.whbex.develop.clans.common.cmd.clpl;

import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.cmd.exec.Command;
import ru.whbex.develop.clans.common.lang.Language;

public class LocaleInfoCommand implements Command {
    @Override
    public void execute(CommandActor actor, Command command, String label, String... args) {
        actor.sendMessage(" === Locale info === ");
        Language lang = ClansPlugin.Context.INSTANCE.plugin.getLanguage();
        String name = " - Name: " + lang.getName();
        String nameLocalized = " - Name (l): " + lang.getNameLocalized();
        String localeTag = "- Tag: " + lang.getLocale().getLanguage();
        String content = " - Strings amount: " + lang.getPhrases().keySet().size();
        actor.sendMessage(name);
        actor.sendMessage(nameLocalized);
        actor.sendMessage(localeTag);
        actor.sendMessage(content);
        actor.sendMessage(" === ");

    }

    @Override
    public String name() {
        return "localeinfo";
    }
}
