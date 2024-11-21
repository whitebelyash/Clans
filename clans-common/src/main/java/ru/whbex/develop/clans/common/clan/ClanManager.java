package ru.whbex.develop.clans.common.clan;

import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.Constants;
import ru.whbex.develop.clans.common.clan.bridge.Bridge;
import ru.whbex.develop.clans.common.clan.bridge.NullBridge;
import ru.whbex.develop.clans.common.conf.Config;
import ru.whbex.develop.clans.common.player.PlayerActor;
import ru.whbex.develop.clans.common.task.Task;
import ru.whbex.lib.log.LogContext;
import ru.whbex.lib.log.Debug;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

// simple clan manager
public class ClanManager {
    // Main clan map
    private final Map<UUID, Clan> clans = new HashMap<>();
    // Tag to clan map
    private final Map<String, Clan> tagClans = new HashMap<>();
    // Leader to clan map
    private final Map<UUID, Clan> leadClans = new HashMap<>();


    private final Bridge bridge;
    private Task flushTask;

    //
    // === Lifecycle ===
    //

    public ClanManager(Config config, Bridge bridge){
        Debug.print("init clanmanager");
        this.bridge = bridge;
        try {
            this.bridge.init();
            this.importAll(bridge).get();
        } catch (InterruptedException | ExecutionException e) {
            LogContext.log(Level.ERROR, "Failed to import clans");
            throw new RuntimeException(e);
        }
        startFlushTask();
    }

    public void shutdown(){
        LogContext.log(Level.INFO, "ClanManager is shutting down...");
        if(this.flushTask != null && !flushTask.cancelled())
            flushTask.cancel();
        try {
            this.exportAll(bridge).get();
        } catch (InterruptedException | ExecutionException e) {
            LogContext.log(Level.ERROR, "Failed to save clans!");
            throw new RuntimeException(e);
        }
    }

    // =========================================================================

    //
    // === Clan management ===
    //

    public Error createClan(String tag, String name, UUID leader){
        // Do not create clan if tag is already taken
        if(tagClans.containsKey(tag))
            return Error.CLAN_TAG_EXISTS;
        // Do not create clan if leader already has it
        if(leadClans.containsKey(leader))
            return Error.LEAD_HAS_CLAN;
        // TODO: Add check for clan membership

        // Create clan object
        UUID id = UUID.randomUUID();
        ClanMeta cm = new ClanMeta(tag, name, null, leader, System.currentTimeMillis() / 1000L, Constants.DEFAULT_RANK);
        ClanLevelling l = new ClanLevelling(1, 0);
        Clan clan = new Clan(this, id, cm, l, false);

        // Put clan object
        clans.put(id, clan);
        tagClans.put(tag.toLowerCase(Locale.ROOT), clan);
        leadClans.put(leader, clan);
        clan.addMember(leader);
        LogContext.log(Level.INFO, "Created clan {0} ({1})", tag, name);
        return null;
    }

    public Error disbandClan(Clan clan){
        if(clan.isDeleted() || !clans.containsKey(clan.getId()))
            return Error.CLAN_NOT_FOUND;
        clan.setDeleted(true);
        tagClans.remove(clan.getMeta().getTag().toLowerCase());
        LogContext.log(Level.INFO, "Disbanded clan {0} ({1})", clan.getMeta().getTag(), clan.getMeta().getName());
        return null;
    }

    public Error disbandClan(String tag){
        if(!tagClans.containsKey(tag.toLowerCase()))
            return Error.CLAN_NOT_FOUND;
        return disbandClan(tagClans.get(tag));
    }

    public Error removeClan(Clan clan){
        if(!clans.containsKey(clan.getId()))
            return Error.CLAN_NOT_FOUND;
        tagClans.remove(clan.getMeta().getTag());
        leadClans.remove(clan.getMeta().getLeader());
        Clan c = clans.remove(clan.getId());
        if(c == null)
            return Error.CLAN_NOT_FOUND;
        LogContext.log(Level.INFO, "Removed clan {0} ({1})", c.getMeta().getTag(), c.getMeta().getName());
        return null;
    }

    public Error removeClan(UUID uuid){
        if(!clans.containsKey(uuid))
            return Error.CLAN_NOT_FOUND;
        return removeClan(clans.get(uuid));
    }

    public Error removeClan(String tag){
        if(!tagClans.containsKey(tag.toLowerCase()))
            return Error.CLAN_NOT_FOUND;
        return this.removeClan(tagClans.get(tag.toLowerCase()));
    }

    public Error recoverClan(Clan clan, String newTag){
        // A bit changed copy of disband logic now, need to check for leader and other shit
        if(!clans.containsKey(clan.getId()))
            return Error.CLAN_NOT_FOUND;
        if(!clan.isDeleted())
            return Error.CLAN_REC_EXISTS;
        if(tagClans.containsKey(clan.getMeta().getTag().toLowerCase())) {
            if (newTag == null)
                return Error.CLAN_TAG_EXISTS;
            else {
                clan.getMeta().setTag(newTag);
            }
        }
        clan.setDeleted(false);
        tagClans.put(clan.getMeta().getTag(), clan);
        LogContext.log(Level.INFO, "Recovered clan {0} ({1})", clan.getMeta().getTag(), clan.getMeta().getName());
        return null;
    }

    // =========================================================================

    //
    // === Clan getters ===
    //

    public Clan getClan(UUID id){
        return clans.get(id);
    }

    public Clan getClan(String tag){
        return tagClans.get(tag.toLowerCase());
    }

    public Clan getClan(PlayerActor leader){
        return leadClans.get(leader.getUniqueId());
    }

    // =========================================================================

    //
    // === Clan checks ===
    //

    public boolean clanExists(String tag){
        return tagClans.containsKey(tag.toLowerCase()) && clans.containsKey(tagClans.get(tag.toLowerCase()).getId());
    }

    public boolean clanExists(UUID id){
        return clans.containsKey(id);
    }

    public boolean isClanLeader(UUID leader){
        return leadClans.containsKey(leader);
    }

    // =========================================================================

    //
    // === Clan map getters ===
    //

    // this returns any loaded clans
    public Collection<Clan> getAllClans(){
        return clans.values();
    }
    // this returns only real clans, not deleted
    public Collection<Clan> getClans(){
        return tagClans.values();
    }

    // =========================================================================

    //
    // === Database ===
    //

    public void tmpExportClan(Clan clan){
        bridge.insertClan(clan, true);
    }
    public void tmpImportClan(String tag){
        Clan clan = bridge.fetchClan(tag);
        if(clan == null) {
            Debug.print("fetch fail");
            return;
        }
        clans.put(clan.getId(), clan);
        if(!clan.isDeleted())
            tagClans.put(tag, clan);
    }

    public Future<Void> importAll(Bridge bridge){
        LogContext.log(Level.INFO, "Importing clans from " + bridge.getClass().getSimpleName());
        if(bridge instanceof NullBridge){
            LogContext.log(Level.WARN, "Bridge is NOP, will not import clans");
            return CompletableFuture.completedFuture(null);
        }
        Callable<Void> call = () -> {
            LogContext.log(Level.INFO, "Loading clans...");
            Collection<Clan> fetched = bridge.fetchAll();
            // no sync for now
            // TODO: Discover concurrency issues here
            fetched.forEach(c -> {
                if(clans.containsKey(c.getId())) {
                    LogContext.log(Level.ERROR, "Clan with UUID {0} is already loaded, skipping", c.getMeta().getTag(), clans.get(c.getId()).getMeta().getTag());
                    return;
                }
                if(!c.isDeleted() && clanExists(c.getMeta().getTag())){
                    LogContext.log(Level.ERROR, "Clan tag conflict while loading {0} (conflicts with: {1}), skipping", c.getId(), getClan(c.getMeta().getTag()).getId());
                    return;
                }
                if(leadClans.containsKey(c.getMeta().getLeader())){
                    LogContext.log(Level.ERROR, "Clan {0} leader already has clan {1}, skipping", c.getMeta().getTag(), leadClans.get(c.getMeta().getLeader()).getMeta().getTag());
                    return;
                }
                c.addMember(c.getMeta().getLeader());
                clans.put(c.getId(), c);
                leadClans.put(c.getMeta().getLeader(), c);
                if(!c.isDeleted())
                    tagClans.put(c.getMeta().getTag().toLowerCase(), c);
            });
            LogContext.log(Level.INFO, "Import complete! Loaded {0} clans ({1} are/is deleted)", clans.size(), tagClans.size());
            return null;
        };
        return ClansPlugin.Context.INSTANCE.plugin.getTaskScheduler().runCallable(call);
    }

    public Future<Boolean> exportAll(Bridge bridge){
        LogContext.log(Level.INFO, "Exporting clans to " + bridge.getClass().getSimpleName());
        Callable<Boolean> call = () -> {
            if(clans.isEmpty())
                return true;
            LogContext.log(Level.INFO, "Saving clans...");
            boolean res = bridge.insertAll(clans.values(), true);
            // TODO: clarify if insertAll returned false
            LogContext.log(Level.INFO, "Complete!");
            return res;
        };
        return ClansPlugin.Context.INSTANCE.plugin.getTaskScheduler().runCallable(call);
    }

    public Future<Boolean> exportAll(){
        return this.exportAll(bridge);
    }

    // TODO: Use as fallback, save clan changes immediately
    private void startFlushTask(){
        long flushDelay = ClansPlugin.Context.INSTANCE.plugin.getConfigWrapped().getClanFlushDelay();
        if(ClansPlugin.Context.INSTANCE.plugin.getConfigWrapped().getClanFlushDelay() > 1 && !(bridge instanceof NullBridge))
            this.flushTask = ClansPlugin.Context.INSTANCE.plugin.getTaskScheduler().runRepeating(() -> exportAll(this.bridge), flushDelay * 20, flushDelay * 20);
        else flushTask = null;
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
        CLAN_ALR_DISBAND
    }
}
