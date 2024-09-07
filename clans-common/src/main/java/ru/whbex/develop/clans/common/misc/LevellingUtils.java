package ru.whbex.develop.clans.common.misc;

import ru.whbex.develop.clans.common.Constants;

public class LevellingUtils {
    public static int getToNextExp(int currentLevel){
        return Constants.NEXT_LEVEL_REQ + currentLevel*Constants.NEXT_LEVEL_STEP;
    }
}
