package ru.whbex.develop.clans.common.clan.member;

import ru.whbex.develop.clans.common.Constants;
import ru.whbex.develop.clans.common.clan.Clan;
import ru.whbex.develop.clans.common.clan.ClanRank;
import ru.whbex.develop.clans.common.player.PlayerActor;

/* Clan member object */
public class Member {

    private final PlayerActor actor;
    private Clan clan;
    private ClanRank rank = Constants.DEFAULT_RANK;
    private String role;

    public Member(PlayerActor actor){
        this.actor = actor;
    }

    public PlayerActor getActor() {
        return actor;
    }

    public Clan getClan() {
        return clan;
    }

    public ClanRank getRank() {
        return rank;
    }

    public String getRole() {
        return role;
    }

    public void setRank(ClanRank rank) {
        this.rank = rank;
    }

    public void setClan(Clan clan) {
        this.clan = clan;
    }

    public void setRole(String role) {
        this.role = role;
    }
    // Clan shouldn't be null
    public boolean hasClan(){
        return clan != null;
    }
    public boolean isLeader(){
        return clan.getMeta().getLeader() == actor.getUniqueId();
    }
}
