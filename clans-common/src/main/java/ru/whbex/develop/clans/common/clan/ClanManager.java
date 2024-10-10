package ru.whbex.develop.clans.common.clan;

import org.slf4j.Logger;
import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.Constants;
import ru.whbex.develop.clans.common.clan.bridge.Bridge;
import ru.whbex.develop.clans.common.conf.ConfigWrapper;
import ru.whbex.develop.clans.common.task.Task;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

// simple clan manager
public class ClanManager {
    public enum Error {
        CLAN_NOT_FOUND,
        CLAN_TAG_EXISTS
    }
    private final Logger log = ClansPlugin.Context.INSTANCE.logger;



    // Main clan map
    private final Map<UUID, Clan> clans = new HashMap<>();
    // Tag to uuid map
    private final Map<String, Clan> tagClans = new HashMap<>();

    private final Bridge bridge;
    private final Task flushTask;


    public ClanManager(ConfigWrapper config, Bridge bridge){
        ClansPlugin.dbg("init clanmanager");
        this.bridge = bridge;
        try {
            this.bridge.init();
            this.importAll(bridge).get();
        } catch (InterruptedException | ExecutionException e) {
            ClansPlugin.log(Level.ERROR, "Failed to import clans on ClanManager init!!!");
            throw new RuntimeException(e);
        }
        long flushDelay = ClansPlugin.Context.INSTANCE.plugin.getConfigWrapped().getClanFlushDelay();
        if(ClansPlugin.Context.INSTANCE.plugin.getConfigWrapped().getClanFlushDelay() > 1)
            this.flushTask = ClansPlugin.Context.INSTANCE.plugin.getTaskScheduler().runRepeating(() -> exportAll(this.bridge), flushDelay * 20, flushDelay * 20);
        else flushTask = null;
    }


    public Error createClan(String tag, String name, UUID leader){
        if(tagClans.containsKey(tag))
            return Error.CLAN_TAG_EXISTS;
        UUID id = UUID.randomUUID();
        ClansPlugin.dbg("creating clan (tag: {0}, name: {1}, leader: {2})", tag, name, leader);
        ClanMeta cm = new ClanMeta(tag, name, null, leader, System.currentTimeMillis() / 1000L, Constants.DEFAULT_RANK);
        ClanLevelling l = new ClanLevelling(1, 0);
        Clan clan = new Clan(this, id, cm, l, true);
        clans.put(id, clan);
        tagClans.put(tag.toLowerCase(Locale.ROOT), clan);
        ClansPlugin.dbg("ok, not requesting clan flush,wait for scheduled");
        return null;
    }
    public Error removeClan(UUID uuid){
        ClansPlugin.dbg("removing clan {0}", clans.get(uuid));
        clans.remove(uuid);
        ClansPlugin.dbg("done");
        return null;
    }
    public Error removeClan(String tag){
        if(!tagClans.containsKey(tag.toLowerCase()))
            return Error.CLAN_NOT_FOUND;
        return this.removeClan(tagClans.get(tag.toLowerCase()).getId());
    }
    public Clan getClan(UUID id){
        return clans.get(id);
    }
    public Clan getClan(String tag){
        return tagClans.get(tag.toLowerCase());
    }

    public void onLevelUp(Clan clan){
        ClansPlugin.dbg("onLvlUp stub " + clan.getId());

    }

    public boolean clanExists(String tag){
        return tagClans.containsKey(tag.toLowerCase()) && clans.containsKey(tagClans.get(tag).getId());
    }
    public boolean clanExists(UUID id){
        return clans.containsKey(id);
    }
    // this returns any loaded clans
    public Collection<Clan> getAllClans(){
        return clans.values();
    }
    // this returns only real clans, not deleted
    public Collection<Clan> getClans(){
        return tagClans.values();
    }

    public void tmpExportClan(Clan clan){
        bridge.insertClan(clan, true);
    }
    public void tmpImportClan(String tag){
        Clan clan = bridge.fetchClan(tag);
        if(clan == null) {
            ClansPlugin.dbg("fetch fail");
            return;
        }
        clans.put(clan.getId(), clan);
        if(!clan.isDeleted())
            tagClans.put(tag, clan);
    }

    public Future<Void> importAll(Bridge bridge){
        ClansPlugin.log(Level.INFO, "Importing clans from " + bridge.getClass().getSimpleName());
        Callable<Void> call = () -> {
            ClansPlugin.log(Level.INFO, "Loading clans...");
            Collection<Clan> fetched = bridge.fetchAll();
            // no sync for now
            // TODO: Discover concurrency issues here
            fetched.forEach(c -> {
                if(clans.containsKey(c.getId())) {
                    ClansPlugin.log(Level.ERROR, "Clan UUID collide detected: {0} with {1}, skipping", c.getMeta().getTag(), clans.get(c.getId()).getMeta().getTag());
                    return;
                }
                if(!c.isDeleted() && clanExists(c.getMeta().getTag())){
                    ClansPlugin.log(Level.ERROR, "Clan tag conflict detected: {0} with {1}, skipping", c.getMeta().getTag(), tagClans.get(c.getMeta().getTag()).getMeta().getTag());
                    return;
                }
                // TODO: Check leader collide
                clans.put(c.getId(), c);
                if(!c.isDeleted())
                    tagClans.put(c.getMeta().getTag().toLowerCase(), c);
                if(c.insert()){
                    ClansPlugin.log(Level.ERROR, "Loaded clan with insert enabled, this is not ok !!!!");
                }
            });
            ClansPlugin.log(Level.INFO, "Import complete! Loaded {0}/{1} clans", clans.size(), tagClans.size());
            return null;
        };
        return ClansPlugin.Context.INSTANCE.plugin.getTaskScheduler().runCallable(call);
    }
    public Future<Void> exportAll(Bridge bridge){
        ClansPlugin.log(Level.INFO, "Exporting clans to " + bridge.getClass().getSimpleName());
        Callable<Void> call = () -> {
            if(clans.isEmpty())
                return null;
            ClansPlugin.log(Level.INFO, "Saving clans...");
            bridge.insertAll(clans.values(), true);
            ClansPlugin.log(Level.INFO, "Complete!");
            return null;
        };
        return ClansPlugin.Context.INSTANCE.plugin.getTaskScheduler().runCallable(call);
    }
    public void shutdown(){
        if(this.flushTask != null && !flushTask.cancelled())
            flushTask.cancel();
        try {
            this.exportAll(bridge).get();
        } catch (InterruptedException | ExecutionException e) {
            ClansPlugin.log(Level.ERROR, "Failed to save clans!");
            throw new RuntimeException(e);
        }
    }
}
