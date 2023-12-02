package ru.whbex.develop.misc;

import ru.whbex.develop.Clans;
import ru.whbex.develop.Constants;
import ru.whbex.develop.player.CPlayer;
import ru.whbex.develop.player.PlayerManager;

public class PlayerUtils {

    public static void broadcast(String message){
        Clans.instance().getPlayerManager().getConsole().sendMessage(message);
        Clans.instance().getPlayerManager().getPlayers().forEach(p -> p.sendMessage(message));
    }

    // Using simpleformat() here
    public static void broadcast(String message, String... args){


    }



}
