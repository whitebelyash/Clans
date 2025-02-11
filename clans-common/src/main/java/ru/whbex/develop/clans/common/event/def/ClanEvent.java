package ru.whbex.develop.clans.common.event.def;

import ru.whbex.develop.clans.common.clan.Clan;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.event.AbstractEvent;
import ru.whbex.develop.clans.common.event.EventHandler;
import ru.whbex.develop.clans.common.player.PlayerActor;

public class ClanEvent extends AbstractEvent<ClanEvent.ClanEventHandler> {
    public ClanEvent(String name) {
        super(name);
    }

    public interface ClanEventHandler extends EventHandler {
        void call(CommandActor actor, Clan clan);
    }
    public void call(CommandActor actor, Clan clan){
        handlerList.forEach(handler -> handler.call(actor, clan));
    }
}
