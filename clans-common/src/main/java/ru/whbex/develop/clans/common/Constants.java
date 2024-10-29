package ru.whbex.develop.clans.common;

import ru.whbex.develop.clans.common.clan.ClanRank;

import java.util.concurrent.TimeUnit;

public class Constants {

    // Minecraft
    public static final char COLOR_SYMBOL = '§';
    public static final char PLAYER_COLOR_SYMBOL = '&';

    // ClanLevelling
    public static final int LVL_EXP_DEF = 0;
    public static final int NEXT_LEVEL_REQ = 128;
    public static final int NEXT_LEVEL_STEP = 64;

    // ClanManager creation
    public static final String CLAN_NAME_FORMAT = "{0} clan";

    // Ranks
    public static final ClanRank DEFAULT_RANK = ClanRank.NOVICE;

    // misc
    public static final String LANGUAGE_FILE_NAME = "messages.lang";
    public static final Long TASK_WAIT_TIMEOUT = 10L;
    public static final TimeUnit TASK_WAIT_TIMEOUT_UNIT = TimeUnit.SECONDS;




}
