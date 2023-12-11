package ru.whbex.develop.core.misc;

import ru.whbex.develop.core.Clans;

public class PlayerUtils {

    public static void broadcast(String message){
        Clans.instance().getPlayerManager().getConsole().sendMessage(message);
        Clans.instance().getPlayerManager().getPlayers().forEach(p -> p.sendMessage(message));
    }

    // Using simpleformat() here
    public static void broadcast(String message, String... args){


    }



}
