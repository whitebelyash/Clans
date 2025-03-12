package ru.whbex.develop.clans.common.player.v2;

import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.Constants;
import ru.whbex.develop.clans.common.misc.SQLUtils;
import ru.whbex.develop.clans.common.player.ConsoleActor;
import ru.whbex.develop.clans.common.player.PlayerActor;
import ru.whbex.develop.clans.common.player.PlayerProfile;
import ru.whbex.develop.clans.common.task.DatabaseService;
import ru.whbex.lib.log.Debug;
import ru.whbex.lib.log.LogContext;
import ru.whbex.lib.sql.SQLAdapter;
import ru.whbex.lib.string.StringUtils;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

public abstract class PlayerManager {
    private final Map<UUID, PlayerActor> actors = new HashMap<>();
    private final Map<UUID, PlayerActor> onlineActors = new HashMap<>();
    private final Map<String, UUID> nameIdMap = new HashMap<>();
    public abstract ConsoleActor consoleActor();

    private static final boolean PM_TRANSIENT = true;

    public PlayerManager(){
        Debug.tprint("PlayerManager", "initializing...");
        if(!PM_TRANSIENT) {
            Debug.tprint("PlayerManager", "Creating actor table");
            DatabaseService.getExecutor(SQLAdapter::update)
                    .sql("CREATE TABLE IF NOT EXISTS actors(" +
                            // Actor UUID
                            "id varchar(36) UNIQUE NOT NULL" + ',' +
                            // Actor nickname
                            "name varchar(32) UNIQUE NOT NULL" + ',' +
                            // Actor reg date (epoch)
                            "reg BIGINT" + ',' +
                            // Actor last seen date (epoch)
                            "last BIGINT" + ',' +
                            // Actor clan id
                            "cid varchar(36)" +
                            ");"
                    )
                    .exceptionally(t -> {
                        LogContext.log(Level.ERROR, "Unable to create players table. See below stacktrace for more info");
                        t.printStackTrace();
                    })
                    .execute();
            Debug.tprint("PlayerManager", "Complete");
        }
    }
    public PlayerActor playerActor(UUID uuid){
        return actors.get(uuid);
    }
    public PlayerActor resolvePlayerActor(String name){
        return nameIdMap.get(name) == null ? null : actors.get(nameIdMap.get(name));
    }
    public UUID resolveUUID(String name){
        return nameIdMap.get(name);
    }

    public Collection<PlayerActor> getOnlineActors() {
        return onlineActors.values();
    }

    public Collection<PlayerActor> getActors() {
        return actors.values();
    }

    // Utils
    public void broadcast(String string){
        onlineActors.values().forEach(a -> a.sendMessage(string));
    }
    public void broadcast(String format, Object... args){
        onlineActors.values().forEach(a -> a.sendMessage(StringUtils.simpleformat(format, args)));
    }
    public void broadcastT(String translatableString){
        String t = ClansPlugin.mainLanguage().getPhrase(translatableString);
        onlineActors.values().forEach(a -> {
            a.sendMessage(t);
        });
    }
    public void broadcastT(String translatableFormat, Object... args){
        String t = StringUtils.simpleformat(ClansPlugin.mainLanguage().getPhrase(translatableFormat), args);
        onlineActors.values().forEach(a -> a.sendMessage(t));
    }

    public PlayerActor loadPlayerActor(UUID uuid){
        if(actors.containsKey(uuid))
            return actors.get(uuid);
        PlayerActor a = createActorObject(uuid);
        actors.put(uuid, a);
        if(a.isOnline())
            onlineActors.put(uuid, a);
        long currentTime = System.currentTimeMillis()/1000L;
        // Set a stub profile before real is fetched
        PlayerProfile stubProfile = new PlayerProfile(uuid, null, currentTime, currentTime, null);
        a.setProfile(stubProfile);
        Future<PlayerProfile> playerProfileFetcher = loadProfileAsync(uuid);
        a.bindFetcher(playerProfileFetcher);
        // LOAD PLAYER DATA FINALLY!!!
        ClansPlugin.taskScheduler().runAsync(() -> {
            try {
                Debug.lprint("Waiting on Future<PlayerProfile> for {0}...", uuid);
                // smol??
                PlayerProfile pp = playerProfileFetcher.get(Constants.TASK_WAIT_TIMEOUT, Constants.TASK_WAIT_TIMEOUT_UNIT);
                if(pp != null)
                    a.setProfile(pp);
                Debug.lprint("Profile data fetched for {0}!", uuid);
                // Save data back
                // TODO: Autoinsert values in loadProfile()
                insertProfile(uuid);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                LogContext.log(Level.ERROR, "Unable to load actor data for UUID " + uuid + "! See below stacktrace for more info");
                e.printStackTrace();
            }
        });
        return a;
    }
    public void unloadPlayerActor(UUID uuid){
        if(!actors.containsKey(uuid))
            throw new NullPointerException("No such actor " + uuid + "!");
        if(actors.get(uuid).getProfile().getClanId() != null)
            throw new IllegalArgumentException("Actor " + uuid + " has a clan bound to it!");

        // Just remove actor from maps for now
        onlineActors.remove(uuid);
        String n = actors.remove(uuid).getName();
        nameIdMap.remove(n);
    }

    private PlayerProfile loadProfile(UUID uuid){
        return DatabaseService.getExecutor(PlayerProfile.class, SQLAdapter::preparedQuery)
                .sql("SELECT * FROM actors WHERE id=?;")
                .setPrepared(ps -> ps.setString(1, uuid.toString()))
                .queryCallback(resp -> {
                    ResultSet r = resp.resultSet();
                    if(r.next())
                        do {
                            return SQLUtils.profileFromQuery(r);
                        } while (r.next());
                    else {
                        Debug.lprint("Profile data was not found for {0} :(", uuid);
                        return null;
                    }
                })
                .execute();
    }
    private Future<PlayerProfile> loadProfileAsync(UUID uuid){
        return DatabaseService.getAsyncExecutor(PlayerProfile.class, SQLAdapter::preparedQuery)
                .sql("SELECT * FROM actors WHERE id=?;")
                .setPrepared(ps -> ps.setString(1, uuid.toString()))
                .queryCallback(resp -> {
                    ResultSet r = resp.resultSet();
                    if(r.next())
                        do {
                            return SQLUtils.profileFromQuery(r);
                        } while (r.next());
                    else {
                        Debug.lprint("Profile data was not found for {0} :(", uuid);
                        return null;
                    }
                })
                .executeAsync();
    }

    private void insertProfile(UUID uuid){
        if(actors.get(uuid) == null)
            throw new NullPointerException("No such actor " + uuid + "!");
        if(actors.get(uuid).getProfile() == null)
            throw new NullPointerException("PlayerProfile on " + uuid + " is null!");
        DatabaseService.getExecutor(SQLAdapter::preparedUpdate)
                .sql("INSERT INTO actors VALUES(?,?,?,?,?);")
                .setPrepared(ps -> SQLUtils.profileToPrepStatement(ps, actors.get(uuid).getProfile()))
                .execute();
    }
    private Future<Void> insertProfileAsync(UUID uuid){
        if(actors.get(uuid) == null)
            throw new NullPointerException("No such actor " + uuid + "!");
        if(actors.get(uuid).getProfile() == null)
            throw new NullPointerException("PlayerProfile on " + uuid + " is null!");
        return DatabaseService.getAsyncExecutor(SQLAdapter::preparedUpdate)
                .sql("INSERT INTO actors VALUES(?,?,?,?,?);")
                .setPrepared(ps -> SQLUtils.profileToPrepStatement(ps, actors.get(uuid).getProfile()))
                .executeAsync();
    }
    // Same logic as in ClanManager
    private static class DatabaseSyncer {
        private DatabaseSyncer(){



        }
    }

    protected abstract PlayerActor createActorObject(UUID uuid);

}
