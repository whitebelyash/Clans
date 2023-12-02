package ru.whbex.develop.player;

import org.bukkit.Bukkit;
import ru.whbex.develop.Clans;
import ru.whbex.develop.lang.LocaleString;
import ru.whbex.develop.misc.StringUtils;

import java.util.Arrays;
import java.util.UUID;

public class CPlayer implements CommandPerformer {
    private final UUID playerId;
    private final String nickname;
    private static final PlayerWrapper pw = Clans.instance().getPlayerWrapper();

    public CPlayer(UUID playerId, String nickname){
        this.playerId = playerId;
        this.nickname = nickname;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    @Override
    public void sendMessage(LocaleString s) {
        this.sendMessage(s.path);

    }

    public void sendMessage(LocaleString string, String... args){
        // args ignored for now
        this.sendMessage(StringUtils.simpleformat(string.path, args));
    }

    @Override
    public void sendMessage(LocaleString s, LocaleString... args) {
        String[] argsl = (String[]) Arrays.stream(args).map(e -> e.path).toArray();
        this.sendMessage(s.path, argsl);

    }

    public void sendMessage(String string){
        pw.sendMessageColorized(playerId, string);
    }

    @Override
    public void sendMessage(String s, String... args) {
        pw.sendMessageColorized(playerId, StringUtils.simpleformat(s, args));
    }

    public boolean isOnline(){
        return pw.isOnline(playerId);
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public String getName() {
        return nickname;
    }
}
