package ru.whbex.develop.common.misc;

import ru.whbex.develop.common.Clans;

public class PlayerUtils {

    public static void broadcast(String message){
        Clans.instance().getPlayerManager().getConsole().sendMessage(message);
        Clans.instance().getPlayerManager().getPlayers().forEach(p -> p.sendMessage(message));
    }

    // Using simpleformat() here
    public static void broadcast(String message, String... args){


    }



}
