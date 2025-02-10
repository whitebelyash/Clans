package ru.whbex.develop.clans.common.event;

import ru.whbex.develop.clans.common.event.def.ClanEvent;
import ru.whbex.develop.clans.common.event.def.PlayerActorEvent;
import ru.whbex.develop.clans.common.event.def.PlayerEvent;

public class EventSystem {
    public static final PlayerEvent PLAYER_LOGIN = new PlayerEvent("PLAYER_LOGIN");
    public static final PlayerEvent PLAYER_JOIN = new PlayerEvent("PLAYER_JOIN");
    public static final PlayerEvent PLAYER_LEFT = new PlayerEvent("PLAYER_LEFT");
    public static final PlayerActorEvent PLAYER_INTERACT = new PlayerActorEvent("PLAYER_INTERACT");
    public static final PlayerActorEvent PLAYER_KILL = new PlayerActorEvent("PLAYER_KILL");
    public static final PlayerActorEvent PLAYER_DEATH = new PlayerActorEvent("PLAYER_DEATH");
    public static final ClanEvent CLAN_CREATE = new ClanEvent("CLAN_CREATE");
    public static final ClanEvent CLAN_DELETE = new ClanEvent("CLAN_DELETE");
    public static final ClanEvent CLAN_DISBAND = new ClanEvent("CLAN_DISBAND");
    public static final ClanEvent CLAN_DISBAND_OTHER = new ClanEvent("CLAN_DISBAND_OTHER");
    public static final ClanEvent CLAN_RECOVER = new ClanEvent("CLAN_RECOVER");
    public static final ClanEvent CLAN_RECOVER_OTHER = new ClanEvent("CLAN_RECOVER_OTHER");
    public static final ClanEvent CLAN_TRANSFER_LEAD = new ClanEvent("CLAN_TRANSFER_LEAD");
    public static final ClanEvent CLAN_JOIN = new ClanEvent("CLAN_JOIN");
    public static final ClanEvent CLAN_LEAVE = new ClanEvent("CLAN_LEAVE");
    public static final ClanEvent CLAN_LVLUP = new ClanEvent("CLAN_LVLUP");
}
