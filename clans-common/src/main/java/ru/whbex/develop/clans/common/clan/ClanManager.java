package ru.whbex.develop.clans.common.clan;

import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.event.EventSystem;
import ru.whbex.develop.clans.common.event.def.ClanEvent;
import ru.whbex.develop.clans.common.misc.SQLUtils;
import ru.whbex.develop.clans.common.player.PlayerActor;
import ru.whbex.develop.clans.common.task.DatabaseService;
import ru.whbex.develop.clans.common.task.Task;
import ru.whbex.lib.log.LogContext;
import ru.whbex.lib.log.Debug;
import ru.whbex.lib.sql.SQLAdapter;

import java.sql.ResultSet;
import java.util.*;

/**
 * Manages clans, including creation, disbanding, and synchronization with the database.
 */
public class ClanManager {
    // Blocks database writes
    // TODO: Disable when database syncing will be completed
    private boolean transientSession = Boolean.getBoolean("clans.transient");
    // Main clan map
    private final Map<UUID, Clan> clans = new HashMap<>();
    // Tag to clan map
    private final Map<String, Clan> tagClans = new HashMap<>();
    // Leader to clan map
    private final Map<UUID, Clan> leadClans = new HashMap<>();
    private DatabaseBridge databaseBridge;

    public ClanManager() {
        Debug.tprint("ClanManager", "ClanManager is initializing...");
        if(!DatabaseService.isInitialized()){
            Debug.tprint("ClanManager", "DatabaseService was not configured, going transient");
            transientSession = true;
        }
        // This should not write to the database
        if(!transientSession){
            this.databaseBridge = new DatabaseBridge();
            databaseBridge.createTable();
            databaseBridge.preloadClans();
        } else notifyAboutTransient();
        registerEvents();
        Debug.tprint("ClanManager", "ClanManager init complete!");
    }

    private void notifyAboutTransient() {
        LogContext.log(Level.WARN, "Note: Clan Manager transient session is active. Possible causes:");
        LogContext.log(Level.WARN, "1) User requested transient session");
        LogContext.log(Level.WARN, "2) Database is not configured or connection has failed");
        LogContext.log(Level.WARN, "3) Error occurred while preloading clans/creating clans table");
        LogContext.log(Level.WARN, "Note: All clan changes will stay in the memory for this session. Database access is blocked");
    }
    private void registerEvents(){
        Debug.tprint("ClanManager", "Registering events...");
        EventSystem.CLAN_CREATE.register((actor, clan) -> ClansPlugin.playerManager().broadcastT("notify.clan.create", clan.getMeta().getTag(), clan.getMeta().getName(), actor.getName()));
        EventSystem.CLAN_DISBAND.register((actor, clan) -> ClansPlugin.playerManager().broadcastT("notify.clan.disband", clan.getMeta().getTag(), clan.getMeta().getName()));
        EventSystem.CLAN_DISBAND_OTHER.register((actor, clan) -> ClansPlugin.playerManager().broadcastT("notify.clan.disband-admin", clan.getMeta().getTag(), clan.getMeta().getName()));
        EventSystem.CLAN_RECOVER.register((actor, clan) -> ClansPlugin.playerManager().broadcastT("notify.clan.recover", clan.getMeta().getTag(), clan.getMeta().getName(), actor.getName()));
        EventSystem.CLAN_RECOVER_OTHER.register((actor, clan) -> ClansPlugin.playerManager().broadcastT("notify.clan.recover", clan.getMeta().getTag(), clan.getMeta().getName(), actor.getName()));
        EventSystem.CLAN_LVLUP.register(((actor, clan) -> clan.sendMessageT("notify.clan.lvlup", clan.getLevelling().getLevel(), clan.getLevelling().nextExp())));
    }

    /**
     * Shuts down the ClanManager, cancels the sync task, and clears all clan maps.
     */
    public void shutdown() {
        LogContext.log(Level.INFO, "ClanManager is shutting down...");
        // As we're now doing immediate sync, all clans should be flushed to disk already at this point
        // if not - skill issue. Cleaning maps anyway
        clans.clear();
        tagClans.clear();
        leadClans.clear();
    }

    // TODO: Javadocs for below methods
    private void cleanClan(Clan clan){
        clans.remove(clan.getId(), clan);
        tagClans.remove(clan.getMeta().getTag().toLowerCase(), clan);
        leadClans.remove(clan.getMeta().getLeader(), clan);
    }
    private void putClan(Clan clan){
        clans.put(clan.getId(), clan);
        if(!clan.isDeleted()) tagClans.put(clan.getMeta().getTag().toLowerCase(), clan);
        leadClans.put(clan.getMeta().getLeader(), clan);
    }

    /**
     * Creates a new clan with the specified tag, name, and leader.
     *
     * @param tag The tag of the new clan.
     * @param name The name of the new clan.
     * @param leader The UUID of the leader of the new clan.
     * @return A Result enum indicating the result of the operation.
     */
    public Result createClan(String tag, String name, UUID leader) {
        if (tagClans.containsKey(tag))
            return Result.CLAN_TAG_EXISTS;
        if (leadClans.containsKey(leader))
            return Result.LEAD_HAS_CLAN;
        // TODO: Add check for clan membership

        PlayerActor actor = ClansPlugin.playerManager().loadPlayerActor(leader);
        Clan c = Clan.newClan(tag, name, actor, false);

        clans.put(c.getId(),c);
        tagClans.put(c.getMeta().getTag().toLowerCase(), c);
        leadClans.put(c.getMeta().getLeader(), c);
        LogContext.log(Level.INFO, "New clan was created. Welcome there, {0}!", c.getMeta().getTag());
        EventSystem.CLAN_CREATE.call((CommandActor) actor, c);
        return Result.SUCCESS;
    }

    /**
     * Disbands the specified clan.
     *
     * @param clan The clan to disband.
     * @param actor The actor performing the disband operation.
     * @return A Result enum indicating the result of the operation.
     */
    public Result disbandClan(Clan clan, CommandActor actor) {
        if (clan.isDeleted() || !clans.containsKey(clan.getId()))
            return Result.CLAN_NOT_FOUND;
        clan.setDeleted(true);
        clan.touch();
        tagClans.remove(clan.getMeta().getTag().toLowerCase());
        boolean self = actor.getUniqueId().equals(clan.getMeta().getLeader());
        ClanEvent ev = self ? EventSystem.CLAN_DISBAND : EventSystem.CLAN_DISBAND_OTHER;
        ev.call(actor, clan);
        LogContext.log(Level.INFO, "Clan {0} was disbanded :(", clan.getMeta().getTag());
        return Result.SUCCESS;
    }

    /**
     * Disbands the clan with the specified tag.
     *
     * @param tag The tag of the clan to disband.
     * @param actor The actor performing the disband operation.
     * @return A Result enum indicating the result of the operation.
     */
    public Result disbandClan(String tag, CommandActor actor) {
        if (!tagClans.containsKey(tag.toLowerCase()))
            return Result.CLAN_NOT_FOUND;
        return disbandClan(tagClans.get(tag), actor);
    }

    /**
     * Removes the specified clan from the manager.
     *
     * @param clan The clan to remove.
     * @return A Result enum indicating the result of the operation.
     */
    public Result removeClan(Clan clan) {
        if (!clans.containsKey(clan.getId()))
            return Result.CLAN_NOT_FOUND;
        tagClans.remove(clan.getMeta().getTag());
        leadClans.remove(clan.getMeta().getLeader());
        Clan c = clans.remove(clan.getId());
        if (c == null)
            return Result.CLAN_NOT_FOUND;
        EventSystem.CLAN_DELETE.call((CommandActor) ClansPlugin.playerManager().loadPlayerActor(clan.getMeta().getLeader()), clan);
        LogContext.log(Level.INFO, "Clan {0} was removed. We won't see you ever again );", c.getMeta().getTag());
        return Result.SUCCESS;
    }

    /**
     * Removes the clan with the specified UUID from the manager.
     *
     * @param uuid The UUID of the clan to remove.
     * @return A Result enum indicating the result of the operation.
     */
    public Result removeClan(UUID uuid) {
        if (!clans.containsKey(uuid))
            return Result.CLAN_NOT_FOUND;
        return removeClan(clans.get(uuid));
    }

    /**
     * Removes the clan with the specified tag from the manager.
     *
     * @param tag The tag of the clan to remove.
     * @return A Result enum indicating the result of the operation.
     */
    public Result removeClan(String tag) {
        if (!tagClans.containsKey(tag.toLowerCase()))
            return Result.CLAN_NOT_FOUND;
        return this.removeClan(tagClans.get(tag.toLowerCase()));
    }

    /**
     * Recovers a previously disbanded clan.
     *
     * @param clan The clan to recover.
     * @param newTag The new tag for the clan, or null to keep the existing tag.
     * @param actor The actor performing the recovery operation.
     * @return A Result enum indicating the result of the operation.
     */
    public Result recoverClan(Clan clan, String newTag, CommandActor actor) {
        if (!clans.containsKey(clan.getId()))
            return Result.CLAN_NOT_FOUND;
        if (!clan.isDeleted())
            return Result.CLAN_REC_EXISTS;
        if (tagClans.containsKey(clan.getMeta().getTag().toLowerCase())) {
            if (newTag == null)
                return Result.CLAN_TAG_EXISTS;
            else {
                clan.getMeta().setTag(newTag);
            }
        }
        clan.setDeleted(false);
        clan.touch();
        tagClans.put(clan.getMeta().getTag(), clan);
        boolean self = actor.getUniqueId().equals(clan.getMeta().getLeader());
        ClanEvent ev = self ? EventSystem.CLAN_RECOVER : EventSystem.CLAN_RECOVER_OTHER;
        ev.call(actor, clan);
        LogContext.log(Level.INFO, "Clan {0} was recovered! (from ashes, I suppose?)", clan.getMeta().getTag());
        return Result.SUCCESS;
    }

    /**
     * Retrieves the clan with the specified UUID.
     *
     * @param id The UUID of the clan to retrieve.
     * @return The clan with the specified UUID, or null if not found.
     */
    public Clan getClan(UUID id) {
        return clans.get(id);
    }

    /**
     * Retrieves the clan with the specified tag.
     *
     * @param tag The tag of the clan to retrieve.
     * @return The clan with the specified tag, or null if not found.
     */
    public Clan getClan(String tag) {
        return tagClans.get(tag.toLowerCase());
    }

    /**
     * Retrieves the clan led by the specified player.
     *
     * @param leader The player actor representing the leader.
     * @return The clan led by the specified player, or null if not found.
     */
    public Clan getClan(PlayerActor leader) {
        return leadClans.get(leader.getUniqueId());
    }

    /**
     * Checks if a clan with the specified tag exists.
     *
     * @param tag The tag to check.
     * @return true if a clan with the specified tag exists, false otherwise.
     */
    public boolean clanExists(String tag) {
        return tagClans.containsKey(tag.toLowerCase()) && clans.containsKey(tagClans.get(tag.toLowerCase()).getId());
    }

    /**
     * Checks if a clan with the specified UUID exists.
     *
     * @param id The UUID to check.
     * @return true if a clan with the specified UUID exists, false otherwise.
     */
    public boolean clanExists(UUID id) {
        return clans.containsKey(id);
    }

    /**
     * Checks if the specified player is a leader of any clan.
     *
     * @param leader The UUID of the player to check.
     * @return true if the player is a leader of any clan, false otherwise.
     */
    public boolean isClanLeader(UUID leader) {
        return leadClans.containsKey(leader);
    }

    /**
     * Retrieves all clans managed by this ClanManager.
     *
     * @return A collection of all clans.
     */
    public Collection<Clan> getAllClans() {
        return clans.values();
    }

    /**
     * Retrieves all clans managed by this ClanManager, indexed by tag. (i.e. not removed)
     *
     * @return A collection of all clans.
     */
    public Collection<Clan> getClans() {
        return tagClans.values();
    }


    /**
     * Triggers an immediate synchronization of clans with the database.
     */
    public void triggerSync(){
        // TODO: Drop this
    }

    /**
     * Enum representing clan management operations result.
     */
    public enum Result {
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

    private class DatabaseBridge {
        private DatabaseBridge(){
            EventSystem.CLAN_CREATE.register(onCreate);
            EventSystem.CLAN_DELETE.register(onDelete);
            EventSystem.CLAN_DISBAND.register(onDisband);
            EventSystem.CLAN_DISBAND_OTHER.register(onDisband);
            EventSystem.CLAN_RECOVER.register(onRecover);
            EventSystem.CLAN_RECOVER_OTHER.register(onRecover);

            Debug.tprint("DatabaseBridge", "DatabaseBridge is alive");
        }

        private void createTable() {
            DatabaseService.getExecutor(SQLAdapter::update)
                    .sql("CREATE TABLE IF NOT EXISTS clans (" +
                            "id varchar(36) NOT NULL UNIQUE PRIMARY KEY, " +
                            "tag varchar(16) NOT NULL, " +
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
                        ClanManager.this.transientSession = true;
                    })
                    .updateCallback(resp -> {
                        Debug.tprint("ClanManager/createTable", "Created table, updated rows -> {0}", resp.updateResult());
                        return null;
                    })
                    .execute();
        }

        // TODO: Only load cached data instead of all clans
        private void preloadClans() {
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
                                Debug.tprint("ClanManager/preloadClans", "Loaded clan {0}/{1}", c.getId(), c.getMeta().getTag());
                            } while (r.next());
                        return null;
                    })
                    .execute();
        }




        // TODO: Do clan existence checks on the db side
        // TODO: Handle sync errors properly
        // TODO: Rollback changes if sync failed
        // TODO: Throttle fast syncs somehow.
        private ClanEvent.ClanEventHandler onCreate = (actor, clan) -> {
            Debug.tprint("DatabaseBridge", "Synchronizing clan {0} create with db...", clan.getId());
            DatabaseService.getAsyncExecutor(SQLAdapter::preparedUpdate)
                    .sql("INSERT INTO clans VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")
                    .exceptionally(e -> {
                        LogContext.log(Level.ERROR, "Failed to sync created clan with database!");
                        ClanManager.this.cleanClan(clan);
                    })
                    .setVerbose(true)
                    .setPrepared(ps -> SQLUtils.clanToPrepStatement(ps, clan))
                    // TODO: Remove
                    .updateCallback(resp -> {
                        Debug.tprint("DatabaseBridge", "Sync complete!");
                        return null;
                    })
                    .executeAsync();
        };
        private ClanEvent.ClanEventHandler onDelete = (actor, clan) -> {
            Debug.tprint("DatabaseBridge", "Synchronizing clan {0} delete with db...", clan.getId());
            DatabaseService.getAsyncExecutor(SQLAdapter::preparedUpdate)
                    .sql("DELETE FROM clans WHERE id=?")
                    .exceptionally(e -> {
                        // TODO: Cancel remove somehow
                        LogContext.log(Level.ERROR, "Failed to sync removed clan with database!");
                        clan.setDeleted(true);
                    })
                    .setVerbose(true)
                    .setPrepared(ps -> ps.setString(1, clan.getId().toString()))
                    // TODO: Remove
                    .updateCallback(resp -> {
                        Debug.tprint("DatabaseBridge", "Sync complete!");
                        return null;
                    })
                    .executeAsync();
        };
        private ClanEvent.ClanEventHandler onDisband = (actor, clan) -> {
            Debug.tprint("DatabaseBridge", "Synchronizing clan {0} disband with db...", clan.getId());
            DatabaseService.getAsyncExecutor(SQLAdapter::preparedUpdate)
                    .sql("UPDATE clans SET deleted=1 WHERE id=?")
                    .exceptionally(e -> {
                        LogContext.log(Level.ERROR, "Failed to sync clan deleted flag with database!");
                    })
                    .setVerbose(true)
                    .setPrepared(ps -> ps.setString(1, clan.getId().toString()))
                    // TODO: Remove
                    .updateCallback(resp -> {
                        Debug.tprint("DatabaseBridge", "Sync complete!");
                        return null;
                    })
                    .executeAsync();
        };
        private ClanEvent.ClanEventHandler onRecover = (actor, clan) -> {
            Debug.tprint("DatabaseBridge", "Synchronizing clan {0} recover with db...", clan.getId());
            DatabaseService.getAsyncExecutor(SQLAdapter::preparedUpdate)
                    .sql("UPDATE clans SET deleted=0 WHERE id=?")
                    .exceptionally(e -> {
                        LogContext.log(Level.ERROR, "Failed to sync clan deleted flag with database!");
                    })
                    .setVerbose(true)
                    .setPrepared(ps -> ps.setString(1, clan.getId().toString()))
                    // TODO: Remove
                    .updateCallback(resp -> {
                        Debug.tprint("DatabaseBridge", "Sync complete!");
                        return null;
                    })
                    .executeAsync();
        };
    }
}
