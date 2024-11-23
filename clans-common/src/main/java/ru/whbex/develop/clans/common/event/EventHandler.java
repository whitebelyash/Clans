package ru.whbex.develop.clans.common.event;

import ru.whbex.develop.clans.common.player.PlayerActor;

import java.util.UUID;

public interface EventHandler {

    void handle(UUID actorId, Object... data);
}
