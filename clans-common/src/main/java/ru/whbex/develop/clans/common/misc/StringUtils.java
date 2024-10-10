package ru.whbex.develop.clans.common.misc;

import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class StringUtils {

    public static String simpleformat(String base, Object... args){
        if(args.length < 1)
            return base;
        if(base.length() < 3)
            return base;
        StringBuilder sb = new StringBuilder(base);
        for(int p = 0; p < args.length; p++){
            if(args[p] == null)
                continue;
            int start = sb.indexOf("{" + p + "}");
            if(start > 0)
                sb.replace(start, start + 3, String.valueOf(args[p]));
        }
        return sb.toString();
    }
    public static String colorize(String message){
        return message.replace(Constants.PLAYER_COLOR_SYMBOL, Constants.COLOR_SYMBOL);
    }
    public static String stripColor(String message, boolean deep){
        String output = message.replace(Constants.PLAYER_COLOR_SYMBOL, '\0');
        if(deep)
            output = output.replace(Constants.COLOR_SYMBOL, '\0');
        return output;
    }
    /* Safe string to uuid converter */
    public static UUID UUIDFromString(String uuid){
        UUID id;
        try {
            id = UUID.fromString(uuid);
        } catch (IllegalArgumentException e){
            return null;
        }
        return id;
    }
    /* Unix epoch (in seconds) to date string converter */
    public static String epochAsString(String format, long epoch){
        Date date = new Date(epoch*1000);
        try {
            return new SimpleDateFormat(format).format(date);
        } catch (NullPointerException | IllegalArgumentException e){
            return new SimpleDateFormat().format(date);
        }

    }
}
