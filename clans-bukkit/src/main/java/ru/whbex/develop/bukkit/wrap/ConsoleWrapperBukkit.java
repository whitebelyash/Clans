package ru.whbex.develop.bukkit.wrap;

import ru.whbex.develop.common.Clans;
import ru.whbex.develop.common.misc.StringUtils;
import ru.whbex.develop.common.player.CommandPerformer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ConsoleWrapperBukkit implements CommandPerformer {
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
    public void sendMessageT(String path) {
        sender.sendMessage(Clans.instance().getLanguage().getString(path));
    }

    @Override
    public void sendMessageT(String path, Object... args) {
        String t = Clans.instance().getLanguage().getString(path);
        sender.sendMessage(StringUtils.simpleformat(t, args));

    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public String getName() {
        return Bukkit.getConsoleSender().getName();
    }
}
