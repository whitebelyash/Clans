package ru.whbex.develop.clans.common.clan;


import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.Constants;
import ru.whbex.develop.clans.common.misc.Messenger;
import ru.whbex.develop.clans.common.player.PlayerActor;
import ru.whbex.lib.log.Debug;
import ru.whbex.lib.string.StringUtils;

import java.util.*;

// Clan class
public class Clan implements Messenger {
    private final UUID clanId;
    private final ClanMeta meta;
    private final ClanLevelling levelling;
    private final EnumSet<ClanFlag> flags = EnumSet.noneOf(ClanFlag.class);

    private Set<UUID> members;


    // Prevent saving clan to database
    private boolean transient_ = false;
    // Prevent updating clan in database if not changed
    private boolean touched = false;

    // Stored in runtime
    private final Map<String, Object> data = new HashMap<>();

    public Clan(UUID clanId, ClanMeta meta, ClanLevelling levelling, boolean trans){
        this.clanId = Objects.requireNonNull(clanId, "clanId");
        this.meta = meta;
        this.levelling = levelling;
        this.transient_ = trans;
        this.members = new HashSet<>();
    }
    public static Clan newClan(String tag, String name, PlayerActor leader, boolean trans){
        UUID id = UUID.randomUUID();
        Debug.lprint("New clan UUID: " + id);
        ClanMeta cm = new ClanMeta(tag, name, null, leader.getUniqueId(), System.currentTimeMillis() / 1000L, Constants.DEFAULT_RANK);
        ClanLevelling l = new ClanLevelling(1, 0);
        return new Clan(id, cm, l, trans);
    }

    public UUID getId() {
        return clanId;
    }

    public ClanMeta getMeta() {
        return meta;
    }

    public ClanLevelling getLevelling() {
        return levelling;
    }


    // For compatibility. don't touch
    public boolean isDeleted() {
        return flags.contains(ClanFlag.DELETED);
    }

    public void setDeleted(boolean deleted) {
        flags.add(ClanFlag.DELETED);
    }

    public boolean hasFlag(ClanFlag flag){
        return flags.contains(flag);
    }
    public void setFlag(ClanFlag flag){
        flags.add(flag);
    }
    public void removeFlag(ClanFlag flag){
        flags.remove(flag);
    }

    public void addMember(UUID id){
        members.add(id);
    }
    public void addMember(PlayerActor actor){
        members.add(actor.getUniqueId());
    }
    public void removeMember(UUID id){
        members.remove(id);
    }
    public void removeMember(PlayerActor actor){
        members.remove(actor.getUniqueId());
    }
    public boolean isMember(UUID id){
        return members.contains(id);
    }
    public boolean isMember(PlayerActor actor){
        return members.contains(actor.getUniqueId());
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public boolean isTransient() {
        return transient_;
    }

    public void setTransient(boolean trans) {
        this.transient_ = trans;
    }
    void touch(){
        touched = true;
    }
    boolean checkTouch(){
        return touched;
    }

    public void setData(String key, Object obj){
        data.put(key, obj);
    }
    public boolean checkData(String key){
        return data.containsValue(key);
    }
    public Object getData(String key, Object obj){
        if(!checkData(key))
            Debug.lprint("data has no key {0}. returning null", key);
        return data.get(key);
    }


    @Override
    public void sendMessage(String string) {
        members.forEach(m -> ClansPlugin.playerManager().loadPlayerActor(m).sendMessage(string));
    }

    @Override
    public void sendMessage(String format, Object... args) {
        String to = StringUtils.simpleformat(format, args);
        members.forEach(m -> ClansPlugin.playerManager().loadPlayerActor(m).sendMessage(to));
    }

    @Override
    public void sendMessageT(String translatableString) {
        String to = ClansPlugin.mainLanguage().getPhrase(translatableString);
        members.forEach(m -> ClansPlugin.playerManager().loadPlayerActor(m).sendMessage(to));
    }

    @Override
    public void sendMessageT(String translatableFormat, Object... args) {
        String to = StringUtils.simpleformat(ClansPlugin.mainLanguage().getPhrase(translatableFormat), args);
        members.forEach(m -> ClansPlugin.playerManager().loadPlayerActor(m).sendMessage(to));
    }
}
