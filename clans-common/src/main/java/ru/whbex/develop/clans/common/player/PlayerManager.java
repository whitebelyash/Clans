package ru.whbex.develop.clans.common.player;

import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.task.DatabaseService;
import ru.whbex.lib.log.Debug;
import ru.whbex.lib.sql.SQLAdapter;
import ru.whbex.lib.string.StringUtils;

import java.util.Collection;
import java.util.UUID;

public interface PlayerManager {
    String TABLE_CREATE_STMT = "CREATE TABLE IF NOT EXISTS players(" +
            "id varchar(18) UNIQUE NOT NULL, " +
            "name varchar(16) NOT NULL, " +
            "regDate bigint, " +
            "lastSeen bigint" +
            ");";
    String FETCH_PROFILE_STMT = "SELECT * FROM players WHERE id=?;";
    String INSERT_PROFILE_STMT_SQLITE = "INSERT OR REPLACE INTO players VALUES(?, ?, ?, ?);";
    String INSERT_PROFILE_STMT_H2 = "MERGE INTO players VALUES(?, ?, ?, ?);";



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
     * @return registered actor
     */
    // TODO: Remove
    PlayerActor registerPlayerActor(PlayerActor actor);

    /**
     * Register player actor by uuid.
     * @param id actor's UUID
     */
    PlayerActor registerPlayerActor(UUID id);
    /**
     * Get all online actors.
     * @return collection of actors.
     */
    Collection<PlayerActor> getOnlinePlayerActors();

    /**
     * Get console actor.
     * @return console actor.
     */
    ConsoleActor getConsoleActor();

    // Utils
    default void broadcast(String string){
        getOnlinePlayerActors().forEach(a -> a.sendMessage(string));
    }
    default void broadcast(String format, Object... args){
        getOnlinePlayerActors().forEach(a -> a.sendMessage(StringUtils.simpleformat(format, args)));
    }
    default void broadcastT(String translatableString){
        String t = ClansPlugin.mainLanguage().getPhrase(translatableString);
        getOnlinePlayerActors().forEach(a -> {
            a.sendMessage(t);
        });
    }
    default void broadcastT(String translatableFormat, Object... args){
        String t = StringUtils.simpleformat(ClansPlugin.mainLanguage().getPhrase(translatableFormat), args);
        getOnlinePlayerActors().forEach(a -> a.sendMessage(t));
    }
    default void createTables(){
        Debug.print("Creating players table...");
        DatabaseService.getExecutor(SQLAdapter::update)
                .sql(TABLE_CREATE_STMT)
                .setVerbose(true)
                .updateCallback(resp -> {
                    Debug.print("Updated {0} rows", resp.updateResult());
                    return null;
                })
                .exceptionally(e -> {throw new RuntimeException(e);})
                .execute();
    }
}
