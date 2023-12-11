package ru.whbex.develop.bukkit.wrap;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import ru.whbex.develop.core.player.CommandPerformer;

public class ConsoleWrapperBukkit implements CommandPerformer {
    private final CommandSender sender = Bukkit.getConsoleSender();


    @Override
    public void sendMessage(String s) {
        sender.sendMessage(s);

    }

    @Override
    public void sendMessage(String s, String... args) {
        sender.sendMessage(s);

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
