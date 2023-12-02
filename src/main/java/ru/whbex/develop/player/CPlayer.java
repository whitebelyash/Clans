package ru.whbex.develop.player;

import org.bukkit.Bukkit;
import ru.whbex.develop.lang.LocaleString;

import java.util.UUID;

public class CPlayer implements CommandPerformer {
    private final UUID playerId;
    private final String nickname;

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
        this.sendMessage(string.path);
    }

    @Override
    public void sendMessage(LocaleString s, LocaleString... args) {
        this.sendMessage(s.path);

    }

    public void sendMessage(String string){
        if(isOnline())
            Bukkit.getPlayer(playerId).sendMessage(string);

    }

    @Override
    public void sendMessage(String s, String... args) {

    }

    public boolean isOnline(){
        return Bukkit.getPlayer(playerId) != null;
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
