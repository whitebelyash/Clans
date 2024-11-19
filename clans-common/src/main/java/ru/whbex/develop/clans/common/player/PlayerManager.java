package ru.whbex.develop.clans.common.player;

import java.util.Collection;
import java.util.UUID;

public interface PlayerManager {

    /**
     * Get player actor by uuid
     * @param id actor's UUID
     * @return actor if found, otherwise null
     */
    PlayerActor getPlayerActor(UUID id);

    /**
     * Get player actor by his nickname
     * @param nickname name
     * @return actor if found (loaded), otherwise null
     */
    PlayerActor getPlayerActor(String nickname);

    /**
     * Get online player actor
     * @param id actor's UUID
     * @return player actor or null if offline
     */
    PlayerActor getOnlinePlayerActor(UUID id);

    /**
     * Register player actor.
     * @param actor actor
     */
    void registerPlayerActor(PlayerActor actor);

    /**
     * Register player actor by uuid.
     * @param id actor's UUID
     */
    void registerPlayerActor(UUID id);

    /**
     * Get player actor or register if not found. Maybe useful for chained calls in builder.
     * @param actor actor
     * @return player actor
     */
    PlayerActor getOrRegisterPlayerActor(PlayerActor actor);

    /**
     * Get player actor by UUID or register if not found.
     * @param id actor's UUID.
     * @return player actor
     */
    PlayerActor getOrRegisterPlayerActor(UUID id);

    /**
     * Get all online actors.
     * @return collection of actors.
     */
    Collection<PlayerActor> getOnlinePlayerActors();

    void onJoin(UUID id);
    void onQuit(UUID id);

    /**
     * Get console actor.
     * @return console actor.
     */
    ConsoleActor getConsoleActor();
}
