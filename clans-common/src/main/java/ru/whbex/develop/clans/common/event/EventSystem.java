package ru.whbex.develop.clans.common.event;

import ru.whbex.develop.clans.common.event.def.ClanEvent;
import ru.whbex.develop.clans.common.event.def.PlayerActorEvent;
import ru.whbex.develop.clans.common.event.def.PlayerEvent;

public class EventSystem {
    public static final PlayerEvent PLAYER_LOGIN = new PlayerEvent();
    public static final PlayerEvent PLAYER_JOIN = new PlayerEvent();
    public static final PlayerEvent PLAYER_LEFT = new PlayerEvent();
    public static final PlayerActorEvent PLAYER_INTERACT = new PlayerActorEvent();
    public static final PlayerActorEvent PLAYER_KILL = new PlayerActorEvent();
    public static final PlayerActorEvent PLAYER_DEATH = new PlayerActorEvent();
    public static final ClanEvent CLAN_CREATE = new ClanEvent();
    public static final ClanEvent CLAN_DELETE = new ClanEvent();
    public static final ClanEvent CLAN_DISBAND = new ClanEvent();
    public static final ClanEvent CLAN_DISBAND_OTHER = new ClanEvent();
    public static final ClanEvent CLAN_RECOVER = new ClanEvent();
    public static final ClanEvent CLAN_RECOVER_OTHER = new ClanEvent();
    public static final ClanEvent CLAN_TRANSFER_LEAD = new ClanEvent();
    public static final ClanEvent CLAN_JOIN = new ClanEvent();
    public static final ClanEvent CLAN_LEAVE = new ClanEvent();
    public static final ClanEvent CLAN_LVLUP = new ClanEvent();
}
