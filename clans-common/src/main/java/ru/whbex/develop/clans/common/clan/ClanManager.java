package ru.whbex.develop.clans.common.clan;

import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.clan.loader.Bridge;
import ru.whbex.develop.clans.common.wrap.ConfigWrapper;
import ru.whbex.develop.clans.common.ClansPlugin;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.slf4j.Logger;

// simple clan manager
public class ClanManager {
    public enum Error {
        CLAN_NOT_FOUND,
        CLAN_TAG_EXISTS
    }

    private final Logger log = ClansPlugin.Context.INSTANCE.logger;



    // Main clan map
    private final Map<UUID, Clan> clans = new HashMap<>();

    private final Bridge bridge;

    // Tag to uuid map
    private final Map<String, Clan> tagClans = new HashMap<>();
    public ClanManager(ConfigWrapper config, Bridge bridge){
        ClansPlugin.dbg("init clanmanager");
        this.bridge = bridge;
        // TODO: Fetch clans from ClanLoader
        try {
            this.bridge.init();
            this.importAll(bridge).get();
        } catch (InterruptedException | ExecutionException e) {
            ClansPlugin.log(Level.ERROR, "Failed to import clans on ClanManager init!!!");
            throw new RuntimeException(e);
        }

    }


    public Error createClan(String tag, String name, UUID leader){
        if(tagClans.containsKey(tag))
            return Error.CLAN_TAG_EXISTS;
        UUID id = UUID.randomUUID();
        ClansPlugin.dbg("creating clan (tag: {0}, name: {1}, leader: {2})", tag, name, leader);
        ClanMeta cm = new ClanMeta(tag, name, null, leader, System.currentTimeMillis() / 1000L);
        ClanLevelling l = new ClanLevelling(1, 0);
        Clan clan = new Clan(this, id, cm, null, l, true);
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
            ClansPlugin.log(Level.INFO, "Loaded {0}/{1} clans", clans.size(), tagClans.size());
            return null;
        };
        return ClansPlugin.Context.INSTANCE.plugin.runCallable(call);
    }
    public Future<Void> exportAll(Bridge bridge){
        Callable<Void> call = () -> {
            if(clans.isEmpty())
                return null;
            ClansPlugin.log(Level.INFO, "Saving clans...");
            bridge.insertAll(clans.values(), true);
            ClansPlugin.log(Level.INFO, "Complete!");
            return null;
        };
        return ClansPlugin.Context.INSTANCE.plugin.runCallable(call);
    }
    public void shutdown(){
        try {
            this.exportAll(bridge).get();
        } catch (InterruptedException | ExecutionException e) {
            ClansPlugin.log(Level.ERROR, "Failed to save clans!");
            throw new RuntimeException(e);
        }
    }

    /* !!! LEGACY !!! TODO: REMOVE */

    /*

    public void loadClan(UUID clanId) throws IllegalArgumentException, NullPointerException {
        if(!cs.clanExists(clanId))
            throw new IllegalArgumentException("Unknown clan " + clanId);
        ClanMeta meta = Objects.requireNonNull(cs.loadMeta(clanId));

        ClanLevelling levelling = Objects.requireNonNull(cs.loadLevelling(clanId));
        ClanSettings settings = Objects.requireNonNull(cs.loadSettings(clanId));


        Clan clan = new Clan(clanId, meta, settings, levelling);
        registerClan(clan, true);
        ClansPlugin.dbg("Loaded clan " + clanId);
    }
    public void unloadClan(UUID clanId){
        if(!clans.containsKey(clanId))
            throw new IllegalArgumentException("Unknown clan " + clanId);
        Clan clan = clans.get(clanId);
        cs.saveMeta(clanId, clan.getMeta());
        cs.saveLevelling(clanId, clan.getLevelling());
        cs.saveSettings(clanId, clan.getSettings());
    }
    public void saveClan(UUID id){

    }


    public Clan getClan(UUID clanId){
        if(!clanExists(clanId))
            throw new NoSuchElementException("Unknown clan " + clanId);
        return clans.get(clanId);
    }
    public boolean clanExists(UUID clanId){
        return clans.containsKey(clanId);
    }
    public boolean clanExists(String tag){
        return tagToId.containsKey(tag.toLowerCase(Locale.ROOT)) && clans.containsKey(tagToId.get(tag.toLowerCase(Locale.ROOT)));
    }
    public boolean clanRegistered(Clan clan){
        return clans.containsValue(clan);
    }

    // Clan registration methods
    public void registerClan(Clan clan, boolean loadMembers) throws IllegalArgumentException {
        if(clans.containsKey(clan.getId()))
            throw new IllegalArgumentException(String.format("Clan %s (%s) already exists!",
                    clan.getId(), clan.getMeta().getTag()));
        clans.put(clan.getId(), clan);
        if(clan.getMeta().getTag() != null)
            tagToId.put(clan.getMeta().getTag().toLowerCase(Locale.ROOT), clan.getId());
        else
            log.warning(String.format("Registered clan %s with null tag!", clan.getId()));
        ClansPlugin.dbg("Registered clan " + clan.getId());
        if(loadMembers){
            ClansPlugin.dbg("Now will load members");
            ms.getAll(clan.getId()).forEach(m -> {
                if(m == null)
                    ClansPlugin.dbg("Member is null");
                if(!clanRegistered(m.getClan())){
                    ClansPlugin.dbg("Member doesn't belong to this clan!");
                    return;
                }
                if(getMemberHolder().memberExists(m)){
                    ClansPlugin.dbg("Member already registered!");
                    return;
                }
                holder.addMember(m.getPlayerId(), m, clan);
            });
        }
        if(!holder.memberExists(clan.getMeta().getLeader())){
            ClansPlugin.dbg("Clan has no leader member instance, creating new");
            Member leader = new Member(clan.getMeta().getLeader(), clan, 0, 0, 0, ClanRank.LEADER);
            holder.addMember(clan.getMeta().getLeader(), leader, clan);
        }
    }
    public void unregisterClan(UUID clanId) throws NoSuchElementException{
        if(!clans.containsKey(clanId))
            throw new NoSuchElementException(String.format("Clan %s not found", clanId));
        if(getClan(clanId).getMeta().getTag() != null)
            tagToId.remove(getClan(clanId).getMeta().getTag().toLowerCase(Locale.ROOT));
        clans.remove(clanId);
        ClansPlugin.dbg("Unregistered clan " + clanId);
    }
    public void unregisterClan(Clan clan) throws NoSuchElementException {
        if(!clans.containsValue(clan))
            throw new NoSuchElementException(String.format("Clan %s not registered in %s",
                    clan.getId(), this.getClass().getSimpleName()));
        clans.remove(clan.getId(), clan);
        ClansPlugin.dbg("Unregistered clan " + clan.getId());
    }

    public Clan createClan(String tag, String name, UUID leader) throws IllegalArgumentException {
        if(clanExists(tag))
            throw new IllegalArgumentException("Clan already exists!");
        if(ClanUtils.isClanMember(leader))
            throw new IllegalArgumentException("Provided leader already has clan!");
        // Clan name = tag for now
        if(name == null)
            name = tag + " clan";
        ClanMeta meta = new ClanMeta(tag, name, null, leader, System.currentTimeMillis() / 1000);
        ClanLevelling lvl = new ClanLevelling(0);
        ClanSettings settings = ClanSettings.builder().build();
        UUID clanId = UUID.randomUUID();
        Clan clan = new Clan(clanId, meta, settings, lvl);
        ClansPlugin.dbg(String.format("Created clan %s %s", tag, name));
        registerClan(clan, false);
        return clan;
    }
    public void disbandClan(String tag){
        // TODO: remove clan from CS and notify online players
        unregisterClan(tag);
    }
    public void unregisterClan(String tag){
        tag = tag.toLowerCase(Locale.ROOT);
        if(!tagToId.containsKey(tag))
            throw new IllegalArgumentException("Unknown clan " + tag);
        if(!clans.containsKey(tagToId.get(tag)))
            throw new IllegalArgumentException(String.format("Clan %s not found", tag));
        Clan clan = clans.get(tagToId.get(tag));
        clans.remove(clan.getId(), clan);
        tagToId.remove(tag, clan.getId());
        ClansPlugin.dbg("Unregistered clan " + tag);
    }


    public void saveAll(){

    }
    public Collection<Clan> getAll(){
        return clans.values();
    }

    public MemberHolder getMemberHolder() {
        return holder;
    }

     */
}
