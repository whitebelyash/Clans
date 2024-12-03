package ru.whbex.develop.clans.common.event.def;

import ru.whbex.develop.clans.common.clan.Clan;
import ru.whbex.develop.clans.common.event.AbstractEvent;
import ru.whbex.develop.clans.common.event.EventHandler;
import ru.whbex.develop.clans.common.player.PlayerActor;

public class ClanEvent extends AbstractEvent<ClanEvent.ClanEventHandler> {
    public interface ClanEventHandler extends EventHandler {
        void call(PlayerActor actor, Clan clan);
    }
    public void call(PlayerActor actor, Clan clan){
        handlerList.forEach(handler -> handler.call(actor, clan));
    }
}
