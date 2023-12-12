package ru.whbex.develop.common.player;

import ru.whbex.develop.common.Clans;
import ru.whbex.develop.common.misc.StringUtils;
import ru.whbex.develop.common.wrap.PlayerWrapper;

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


    public void sendMessage(String string){
        pw.sendMessage(playerId, string);
    }

    @Override
    public void sendMessage(String s, Object... args) {
        pw.sendMessage(playerId, StringUtils.simpleformat(s, args));

    }

    @Override
    public void sendMessageT(String path) {
        pw.sendMessage(playerId, Clans.instance().getLanguage().getString(path));
    }

    @Override
    public void sendMessageT(String path, Object... args) {
        String t = Clans.instance().getLanguage().getString(path);
        pw.sendMessage(playerId, StringUtils.simpleformat(t, args));

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
