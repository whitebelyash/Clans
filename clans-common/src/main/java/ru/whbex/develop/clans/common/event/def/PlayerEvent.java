package ru.whbex.develop.clans.common.event.def;

import ru.whbex.develop.clans.common.event.AbstractEvent;
import ru.whbex.develop.clans.common.event.EventHandler;
import java.util.UUID;

public class PlayerEvent extends AbstractEvent<PlayerEvent.PlayerEventHandler> {
    public PlayerEvent(String name) {
        super(name);
    }

    public interface PlayerEventHandler extends EventHandler {
        void call(UUID uuid);
    }
    public void call(UUID uuid){
        handlerList.forEach(handler -> handler.call(uuid));
    }
}
