package ru.whbex.develop.clans.bukkit.player;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.player.ConsoleActor;
import ru.whbex.lib.string.StringUtils;

public class ConsoleActorBukkit implements ConsoleActor, CommandActor {
    private final CommandSender sender = Bukkit.getConsoleSender();

    @Override
    public void sendMessage(String s) {
        sender.sendMessage(s);
    }

    @Override
    public void sendMessage(String s, Object... args) {

        sender.sendMessage(StringUtils.simpleformat(s, args));
    }

    @Override
    public void sendMessageT(String s) {
        sender.sendMessage(ClansPlugin.Context.INSTANCE.plugin.getLanguage().getPhrase(s));
    }

    @Override
    public void sendMessageT(String s, Object... args) {
        sender.sendMessage(StringUtils.simpleformat(ClansPlugin.Context.INSTANCE.plugin.getLanguage().getPhrase(s), args));
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public String getName() {
        return sender.getName();
    }

    @Override
    public String toString() {
        return "ConsoleActorBukkit{}";
    }
}
