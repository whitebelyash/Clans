package ru.whbex.develop.clans.common;

import ru.whbex.develop.clans.common.clan.ClanRank;

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

    // === Permissions ===
    // broadcast
    public static final String BC_CLAN_CREATE = "clans.broadcast.create";
    public static final String BC_CLAN_DISBAND_PLAYER = "clans.broadcast.disbandplayer";
    public static final String BC_CLAN_DISBAND_STAFF = "clans.broadcast.disbandstaff";
    // chats
    public static final String CLAN_CHAT = "clans.clanchat";
    public static final String ALLY_CHAT = "clans.allychat";
    public static final String CLAN_CHAT_SPY = "clans.clanchat.spy";
    public static final String ALLY_CHAT_SPY = "clans.allychat.spy";

    // misc
    public static final String LANGUAGE_FILE_NAME = "messages.lang";




}
