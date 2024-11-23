package ru.whbex.develop.clans.common.event;

import ru.whbex.develop.clans.common.player.PlayerActor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/* Simple event system for plugin needs. Does not support event environment objects, only PlayerActor */
public class EventSystem {
    public enum Events {
        PLAYER_LOGIN,
        PLAYER_JOIN,
        PLAYER_QUIT,
        PLAYER_INTERACT,
        PLAYER_KILL,
        PLAYER_DEATH,
        CLAN_CREATE,
        CLAN_DISBAND,
        CLAN_RECOVERY;
        // add others

        private final List<EventHandler> handlers = new LinkedList<>();
        public void register(EventHandler handler){
            handlers.add(handler);
        }
        public void unregister(EventHandler handler){
            handlers.remove(handler);
        }
        public void unregisterAll(){
            handlers.clear();
        }
    }
    public void callEvent(Events event, PlayerActor actor, Object data){
        event.handlers.forEach(h -> h.handle(actor, data));
    }
}
