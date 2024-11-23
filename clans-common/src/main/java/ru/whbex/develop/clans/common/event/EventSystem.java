package ru.whbex.develop.clans.common.event;

import ru.whbex.develop.clans.common.player.PlayerActor;
import ru.whbex.lib.log.Debug;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/* Simple event system for plugin needs. */
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
        CLAN_DISBAND_OTHER,
        CLAN_RECOVERY,
        CLAN_RECOVERY_OTHER;
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
    public static void callEvent(Events event, UUID actorId, Object data){
        event.handlers.forEach(h -> h.handle(actorId, data));
    }
    public static void callEvent(Events event, PlayerActor actor, Object data){
        event.handlers.forEach(h -> h.handle(actor.getUniqueId(), actor, data));
    }
}
