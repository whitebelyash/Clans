package ru.whbex.develop.common.misc;

import ru.whbex.develop.common.Constants;

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
}
