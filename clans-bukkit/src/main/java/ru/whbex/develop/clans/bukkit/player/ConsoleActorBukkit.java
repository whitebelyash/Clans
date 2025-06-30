package ru.whbex.develop.clans.bukkit.player;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.misc.text.FormattedText;
import ru.whbex.develop.clans.common.misc.text.Text;
import ru.whbex.develop.clans.common.player.ConsoleActor;
import ru.whbex.lib.string.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConsoleActorBukkit implements ConsoleActor, CommandActor {
    private final CommandSender sender = Bukkit.getConsoleSender();
    private final UUID uuid = new UUID(0, 0);
    private final Map<String, Object> data = new HashMap<>();

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
    public void sendText(Text text) {

    }

    @Override
    public void sendFormattedText(FormattedText text) {

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
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public void setData(String key, Object data) {
        this.data.put(key, data);
    }

    @Override
    public Object getData(String key) {
        return data.get(key);
    }

    @Override
    public boolean hasData(String key) {
        return data.containsKey(key);
    }

    @Override
    public void removeData(String key) {
        data.remove(key);

    }

    @Override
    public void removeDataAll() {
        data.clear();
    }


    @Override
    public String toString() {
        return "ConsoleActorBukkit{}";
    }
}
