package ru.whbex.develop.clans.common.clan;


import ru.whbex.develop.clans.common.player.PlayerActor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

// Clan class
public class Clan {
    private final UUID clanId;
    private final ClanMeta meta;
    private final ClanLevelling levelling;

    private Set<UUID> members;

    private boolean isDeleted = false;

    // Prevent saving clan to database
    private boolean transient_ = false;

    public Clan(UUID clanId, ClanMeta meta, ClanLevelling levelling, boolean trans){
        this.clanId = Objects.requireNonNull(clanId, "clanId");
        this.meta = meta;
        this.levelling = levelling;
        this.transient_ = trans;
        this.members = new HashSet<>();
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


    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
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
}
