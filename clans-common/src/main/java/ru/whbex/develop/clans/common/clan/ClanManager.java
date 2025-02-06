package ru.whbex.develop.clans.common.clan;

import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.ClansPlugin;

import ru.whbex.develop.clans.common.conf.Config;
import ru.whbex.develop.clans.common.event.EventSystem;
import ru.whbex.develop.clans.common.misc.SQLUtils;
import ru.whbex.develop.clans.common.player.PlayerActor;
import ru.whbex.develop.clans.common.task.DatabaseService;
import ru.whbex.develop.clans.common.task.Task;
import ru.whbex.lib.log.LogContext;
import ru.whbex.lib.log.Debug;
import ru.whbex.lib.sql.SQLAdapter;

import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

// simple clan manager
public class ClanManager {
    // Allows skipping clan flushing to the database
    // TODO: Disable when database syncing will be completed
    private boolean transientSession = true;
    // Main clan map
    private final Map<UUID, Clan> clans = new HashMap<>();
    // Tag to clan map
    private final Map<String, Clan> tagClans = new HashMap<>();
    // Leader to clan map
    private final Map<UUID, Clan> leadClans = new HashMap<>();
    private Task syncTask;

    //
    // === Lifecycle ===
    //

    public ClanManager(Config config, boolean transientSession) {
        this.transientSession = transientSession;
        Debug.print("init clanmanager");
        createClanTable();
        preloadClans();
        if (transientSession)
            notifyAboutTransient();
        startSyncTask();
        Debug.print("Registering events...");
        EventSystem.CLAN_CREATE.register((actor, clan) -> ClansPlugin.playerManager().broadcastT("notify.clan.create", clan.getMeta().getTag(), clan.getMeta().getName(), actor.getProfile().getName()));
        EventSystem.CLAN_DISBAND.register((actor, clan) -> ClansPlugin.playerManager().broadcastT("notify.clan.disband", clan.getMeta().getTag(), clan.getMeta().getName()));
        EventSystem.CLAN_DISBAND_OTHER.register((actor, clan) -> ClansPlugin.playerManager().broadcastT("notify.clan.disband-admin", clan.getMeta().getTag(), clan.getMeta().getName()));
        EventSystem.CLAN_RECOVER.register((actor, clan) -> ClansPlugin.playerManager().broadcastT("notify.clan.recover", clan.getMeta().getTag(), clan.getMeta().getName(), actor.getProfile().getName()));
        EventSystem.CLAN_RECOVER_OTHER.register((actor, clan) -> ClansPlugin.playerManager().broadcastT("notify.clan.recover", clan.getMeta().getTag(), clan.getMeta().getName(), actor.getProfile().getName()));
        Debug.print("ClanManager init complete!");
    }

    private void notifyAboutTransient() {
        LogContext.log(Level.WARN, "Note: ClanManager transient session is enabled. Possible causes:");
        LogContext.log(Level.WARN, "1) DatabaseService is not configured");
        LogContext.log(Level.WARN, "2) Exception was thrown while creating clan table/fetching clans");
        LogContext.log(Level.WARN, "3) User requested transient session");
        LogContext.log(Level.WARN, "Note: All clan changes will stay in the memory for this session");
    }

    public void shutdown() {
        LogContext.log(Level.INFO, "ClanManager is shutting down...");
        syncTask.cancel();
        syncTask = null;
    }

    // =========================================================================

    //
    // === Clan management (some methods here are not asynchronous, do not call them on main thread!!!) ===
    //

    public synchronized Error createClan(String tag, String name, UUID leader) {
        // Do not create clan if tag is already taken
        if (tagClans.containsKey(tag))
            return Error.CLAN_TAG_EXISTS;
        // Do not create clan if leader already has it
        if (leadClans.containsKey(leader))
            return Error.LEAD_HAS_CLAN;
        // TODO: Add check for clan membership

        PlayerActor actor = ClansPlugin.playerManager().getPlayerActor(leader);
        Clan c = Clan.newClan(tag, name, actor, false);

        // Sync to db
        if (!transientSession) {
            // TODO: Find another way to check for exception status
            AtomicBoolean status = new AtomicBoolean(false);
            DatabaseService.getExecutor(SQLAdapter::preparedUpdate)
                    .sql("INSERT INTO clans VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")
                    .exceptionally(e -> {
                        LogContext.log(Level.ERROR, "Failed to sync created clan with database!");
                        status.set(true);
                    })
                    .setVerbose(true)
                    .setPrepared(ps -> SQLUtils.clanToPrepStatement(ps, c))
                    .execute();
            if (status.get())
                return Error.CLAN_SYNC_ERROR;
        }
        clans.put(c.getId(),c);
        tagClans.put(c.getMeta().getTag().toLowerCase(), c);
        leadClans.put(c.getMeta().getLeader(), c);
        LogContext.log(Level.INFO, "New clan was created. Welcome there, {0}!", c.getMeta().getTag());
        return Error.SUCCESS;
    }

    /* Disband clan. Can run on main thread */
    public Error disbandClan(Clan clan) {
        if (clan.isDeleted() || !clans.containsKey(clan.getId()))
            return Error.CLAN_NOT_FOUND;
        clan.setDeleted(true);
        clan.touch();
        tagClans.remove(clan.getMeta().getTag().toLowerCase());
        LogContext.log(Level.INFO, "Clan {0} was disbanded :(", clan.getMeta().getTag());
        return Error.SUCCESS;
    }

    public Error disbandClan(String tag) {
        if (!tagClans.containsKey(tag.toLowerCase()))
            return Error.CLAN_NOT_FOUND;
        return disbandClan(tagClans.get(tag));
    }

    public synchronized Error removeClan(Clan clan) {
        if (!clans.containsKey(clan.getId()))
            return Error.CLAN_NOT_FOUND;

        if (!transientSession || !clan.isTransient()) {
            AtomicBoolean shit = new AtomicBoolean(false);
            DatabaseService.getExecutor(SQLAdapter::preparedUpdate)
                    .sql("DELETE FROM clans WHERE id=?")
                    .exceptionally(e -> {
                        LogContext.log(Level.ERROR, "Failed to sync removed clan with database! Cancelling remove");
                        shit.set(true);
                    })
                    .setVerbose(true)
                    .setPrepared(ps -> ps.setString(1, clan.getId().toString()))
                    .execute();
            if (shit.get())
                return Error.CLAN_SYNC_ERROR;
        }
        tagClans.remove(clan.getMeta().getTag());
        leadClans.remove(clan.getMeta().getLeader());
        Clan c = clans.remove(clan.getId());
        if (c == null)
            return Error.CLAN_NOT_FOUND;
        LogContext.log(Level.INFO, "Clan {0} was removed. We won't see you ever again );", c.getMeta().getTag());
        return Error.SUCCESS;
    }

    public Error removeClan(UUID uuid) {
        if (!clans.containsKey(uuid))
            return Error.CLAN_NOT_FOUND;
        return removeClan(clans.get(uuid));
    }

    public Error removeClan(String tag) {
        if (!tagClans.containsKey(tag.toLowerCase()))
            return Error.CLAN_NOT_FOUND;
        return this.removeClan(tagClans.get(tag.toLowerCase()));
    }

    public Error recoverClan(Clan clan, String newTag) {
        // A bit changed copy of disband logic now, need to check for leader and other things
        if (!clans.containsKey(clan.getId()))
            return Error.CLAN_NOT_FOUND;
        if (!clan.isDeleted())
            return Error.CLAN_REC_EXISTS;
        if (tagClans.containsKey(clan.getMeta().getTag().toLowerCase())) {
            if (newTag == null)
                return Error.CLAN_TAG_EXISTS;
            else {
                clan.getMeta().setTag(newTag);
            }
        }
        clan.setDeleted(false);
        clan.touch();
        tagClans.put(clan.getMeta().getTag(), clan);
        LogContext.log(Level.INFO, "Clan {0} was recovered! (from ashes, I suppose?)", clan.getMeta().getTag());
        return Error.SUCCESS;
    }

    // =========================================================================

    //
    // === Clan getters ===
    //

    public Clan getClan(UUID id) {
        return clans.get(id);
    }

    public Clan getClan(String tag) {
        return tagClans.get(tag.toLowerCase());
    }

    public Clan getClan(PlayerActor leader) {
        return leadClans.get(leader.getUniqueId());
    }

    // =========================================================================

    //
    // === Clan checks ===
    //

    public boolean clanExists(String tag) {
        return tagClans.containsKey(tag.toLowerCase()) && clans.containsKey(tagClans.get(tag.toLowerCase()).getId());
    }

    public boolean clanExists(UUID id) {
        return clans.containsKey(id);
    }

    public boolean isClanLeader(UUID leader) {
        return leadClans.containsKey(leader);
    }

    // =========================================================================

    //
    // === Clan map getters ===
    //

    // this returns any loaded clans
    public Collection<Clan> getAllClans() {
        return clans.values();
    }

    // this returns only real clans, not deleted
    public Collection<Clan> getClans() {
        return tagClans.values();
    }

    // =========================================================================

    //
    // === Database ===
    //

    // Will load all clans from database
    // TODO: Only load cached data instead of all clans
    // will be pretty hard to implement, i think
    private void preloadClans() {
        if (!transientSession) {
            DatabaseService.getExecutor(SQLAdapter::preparedQuery)
                    .sql("SELECT * FROM clans;")
                    .exceptionally(e -> {
                        LogContext.log(Level.ERROR, "Exception was thrown while preloading clans from the database. See below stacktrace for more info");
                        e.printStackTrace();
                        transientSession = true;
                    })
                    .setVerbose(true)
                    .queryCallback(resp -> {
                        ResultSet r = resp.resultSet();
                        if (r.next())
                            do {
                                Clan c = SQLUtils.clanFromQuery(r);
                                if (c == null) {
                                    LogContext.log(Level.ERROR, "Failed to preload clan");
                                    continue;
                                }
                                clans.put(c.getId(), c);
                                if (!c.isDeleted())
                                    tagClans.put(c.getMeta().getTag(), c);
                                leadClans.put(c.getMeta().getLeader(), c);
                                Debug.print("Loaded clan {0}/{1}", c.getId(), c.getMeta().getTag());
                            } while (r.next());
                        return null;
                    })
                    .execute();
        }
    }

    private void createClanTable() {
        if (DatabaseService.isInitialized())
            DatabaseService.getExecutor(SQLAdapter::update)
                    .sql("CREATE TABLE IF NOT EXISTS clans (" +
                            "id varchar(36) NOT NULL UNIQUE PRIMARY KEY, " +
                            "tag varchar(16), " +
                            "name varchar(24), " +
                            "description varchar(255), " +
                            "creationEpoch LONG, " +
                            "leader varchar(36), " +
                            "deleted TINYINT, " +
                            "level INT, " +
                            "exp INT, " +
                            "defaultRank INT);")
                    .exceptionally(e -> {
                        LogContext.log(Level.ERROR, "Unable to create clans table in the database. See below stacktrace for more info");
                        e.printStackTrace();
                        transientSession = true;
                    })
                    .updateCallback(resp -> {
                        Debug.print("Created table, updated rows -> {0}", resp.updateResult());
                        return null;
                    })
                    .execute();
    }

    private void startSyncTask(){
        long flushDelay = ClansPlugin.config().getClanFlushDelay();
        if(ClansPlugin.config().getClanFlushDelay() > 1 && !transientSession) {
            Debug.print("Starting sync task");
            // multiple delay by 20 because taskScheduler uses ticks, not seconds
            // TODO: Change var names to sync too idk im lazy
            this.syncTask = ClansPlugin.TaskScheduler().runRepeatingAsync(() -> {
                throw new RuntimeException("Not implemented");
                // TODO: Switch to UPDATE statement instead of merge/replace
                /* SQLAdapter<Void>.Executor<Void> exec = DatabaseService.getExecutor(SQLAdapter::preparedUpdate)
                        .sql(SQLString.REPLACE_OR_INSERT_CLANS.current());
                // WholesomeLib bug workaround. Will fix it there too
                AtomicBoolean doUpdate = new AtomicBoolean(false);
                clans.values().stream().filter(Clan::checkTouch).forEach(clan -> {
                    exec.addPrepared(ps -> SQLUtils.clanToPrepStatement(ps, clan));
                    doUpdate.set(true);
                });
                // Do not continue if there are no clans to update
                if(!doUpdate.get())
                    return;
                exec.exceptionally(e -> {
                            LogContext.log(Level.ERROR, "Failed to execute clan sync task, see below stacktrace for more info");
                            e.printStackTrace();
                        })
                        .execute();
                 */
            }, flushDelay * 20, flushDelay * 20);
        }
        else syncTask = null;
    }

    /* Will block execution */
    public void triggerSync(){
        if(syncTask == null){
            LogContext.log(Level.WARN, "Unable to trigger sync task because it's not created");
            return;
        }
        syncTask.run();
    }

    // =========================================================================

    public enum Error {
        // Return if clan was not found in maps
        CLAN_NOT_FOUND,
        // Return if clan with provided tag exists in tagClans map
        CLAN_TAG_EXISTS,
        // Return if recovered clan tag was taken by another clan
        CLAN_REC_EXISTS,
        // Return if leader has a clan
        LEAD_HAS_CLAN,
        // Return if clan is already disbanded (has a deleted bit)
        CLAN_ALR_DISBAND,
        // Return if db sync failed
        CLAN_SYNC_ERROR,
        // Says for itself
        SUCCESS
    }
}
