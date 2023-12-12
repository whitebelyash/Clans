package ru.whbex.develop.common.player;

import ru.whbex.develop.common.Clans;
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
        pw.sendMessageColorized(playerId, string);
    }

    @Override
    public void sendMessage(String s, String... args) {

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
