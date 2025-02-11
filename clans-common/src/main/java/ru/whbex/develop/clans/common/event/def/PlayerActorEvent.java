package ru.whbex.develop.clans.common.event.def;

import ru.whbex.develop.clans.common.event.AbstractEvent;
import ru.whbex.develop.clans.common.event.EventHandler;
import ru.whbex.develop.clans.common.player.PlayerActor;

public class PlayerActorEvent extends AbstractEvent<PlayerActorEvent.PlayerActorEventHandler> {
    public PlayerActorEvent(String name) {
        super(name);
    }

    public interface PlayerActorEventHandler extends EventHandler {
        void call(PlayerActor actor);
    }
    public void call(PlayerActor actor){
        handlerList.forEach(handler -> handler.call(actor));
    }
}
