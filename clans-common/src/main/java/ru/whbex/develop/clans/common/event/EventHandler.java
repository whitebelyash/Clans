package ru.whbex.develop.clans.common.event;

import ru.whbex.develop.clans.common.player.PlayerActor;

public interface EventHandler {

    void handle(PlayerActor actor, Object data);
}
