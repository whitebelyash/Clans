package ru.whbex.develop.clans.common.event;

import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.event.def.ClanEvent;
import ru.whbex.develop.clans.common.event.def.PlayerActorEvent;
import ru.whbex.develop.clans.common.event.def.PlayerEvent;
import ru.whbex.develop.clans.common.player.PlayerActor;
import ru.whbex.lib.log.Debug;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/* Simple event system for plugin needs. Private. */
public class EventSystem {
    public static final PlayerEvent PLAYER_LOGIN = new PlayerEvent();
    public static final PlayerEvent PLAYER_JOIN = new PlayerEvent();
    public static final PlayerEvent PLAYER_LEFT = new PlayerEvent();
    public static final PlayerActorEvent PLAYER_INTERACT = new PlayerActorEvent();
    public static final PlayerActorEvent PLAYER_KILL = new PlayerActorEvent();
    public static final PlayerActorEvent PLAYER_DEATH = new PlayerActorEvent();
    public static final ClanEvent CLAN_CREATE = new ClanEvent();
    public static final ClanEvent CLAN_DISBAND = new ClanEvent();
    public static final ClanEvent CLAN_DISBAND_OTHER = new ClanEvent();
    public static final ClanEvent CLAN_RECOVER = new ClanEvent();
    public static final ClanEvent CLAN_RECOVER_OTHER = new ClanEvent();
    public static final ClanEvent CLAN_TRANSFER_LEAD = new ClanEvent();
    public static final ClanEvent CLAN_JOIN = new ClanEvent();
    public static final ClanEvent CLAN_LEAVE = new ClanEvent();
}
