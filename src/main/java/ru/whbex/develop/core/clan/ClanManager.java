package ru.whbex.develop.core.clan;

import ru.whbex.develop.core.Clans;
import ru.whbex.develop.core.clan.member.Member;
import ru.whbex.develop.core.clan.member.MemberHolder;
import ru.whbex.develop.core.misc.ClanUtils;
import ru.whbex.develop.core.storage.ClanStorage;
import ru.whbex.develop.core.storage.MemberStorage;

import java.util.*;

// simple clan manager
public class ClanManager {



    // Main clan map
    private final Map<UUID, Clan> clans = new HashMap<>();

    // Tag to uuid map
    private final Map<String, UUID> tagToId = new HashMap<>();

    private final ClanStorage cs;
    private final MemberStorage ms;
    private final MemberHolder holder;
    public ClanManager(ClanStorage cs, MemberStorage ms){
        this.cs = cs;
        this.ms = ms;
        this.holder = new MemberHolder(this);
        Clans.LOGGER.info("Loading clans...");
        // Running sync
        Set<UUID> uuids = cs.loadAllUUID();
        uuids.forEach(this::loadClan);
        Clans.LOGGER.info("Loaded clans");


    }

    public void loadClan(UUID clanId) throws IllegalArgumentException, NullPointerException {
        if(!cs.clanExists(clanId))
            throw new IllegalArgumentException("Unknown clan " + clanId);
        ClanMeta meta = Objects.requireNonNull(cs.loadMeta(clanId));

        ClanLevelling levelling = Objects.requireNonNull(cs.loadLevelling(clanId));
        ClanSettings settings = Objects.requireNonNull(cs.loadSettings(clanId));

        Clan clan = new Clan(clanId, meta, settings, levelling);
        registerClan(clan, true);
        Clans.dbg("Loaded clan " + clanId);
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
            Clans.LOGGER.warning(String.format("Registered clan %s with null tag!", clan.getId()));
        Clans.dbg("Registered clan " + clan.getId());
        if(loadMembers){
            Clans.dbg("Now will load members");
            ms.getAll(clan.getId()).forEach(m -> {
                if(m == null)
                    Clans.dbg("Member is null");
                if(!clanRegistered(m.getClan())){
                    Clans.dbg("Member doesn't belong to this clan!");
                    return;
                }
                if(getMemberHolder().memberExists(m)){
                    Clans.dbg("Member already registered!");
                    return;
                }
                holder.addMember(m.getPlayerId(), m, clan);
            });
        }
        if(!holder.memberExists(clan.getMeta().getLeader())){
            Clans.dbg("Clan has no leader member instance, creating new");
            Member leader = new Member(clan.getMeta().getLeader(), clan, 0, 0, 0, ClanRank.LEADER);
        }
    }
    public void unregisterClan(UUID clanId) throws NoSuchElementException{
        if(!clans.containsKey(clanId))
            throw new NoSuchElementException(String.format("Clan %s not found", clanId));
        if(getClan(clanId).getMeta().getTag() != null)
            tagToId.remove(getClan(clanId).getMeta().getTag().toLowerCase(Locale.ROOT));
        clans.remove(clanId);
        Clans.dbg("Unregistered clan " + clanId);
    }
    public void unregisterClan(Clan clan) throws NoSuchElementException {
        if(!clans.containsValue(clan))
            throw new NoSuchElementException(String.format("Clan %s not registered in %s",
                    clan.getId(), this.getClass().getSimpleName()));
        clans.remove(clan.getId(), clan);
        Clans.dbg("Unregistered clan " + clan.getId());
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
        ClanSettings settings = new ClanSettings();
        UUID clanId = UUID.randomUUID();
        Clan clan = new Clan(clanId, meta, settings, lvl);
        Clans.dbg(String.format("Created clan %s %s", tag, name));
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
        Clans.dbg("Unregistered clan " + tag);
    }


    public void saveAll(){

    }
    public Collection<Clan> getAll(){
        return clans.values();
    }

    public MemberHolder getMemberHolder() {
        return holder;
    }
}