package ru.whbex.develop.common.clan.member;

import ru.whbex.develop.common.clan.ClanRank;
import ru.whbex.develop.common.clan.Clan;
import ru.whbex.develop.common.player.PlayerActor;

import java.util.UUID;

public class Member {
    private PlayerActor actor;
    private Clan clan;
    private int exp;
    private int kills = 0;
    private int deaths = 0;
    private ClanRank rank;

    public Member(PlayerActor actor, Clan clan, int exp, int kills, int deaths, ClanRank rank){
        this.actor = actor;
        this.exp = exp;
        this.kills = kills;
        this.deaths = deaths;
        this.rank = rank;
        this.clan = clan;
    }

    public UUID getPlayerId() {
        return actor.getUniqueId();
    }
    public PlayerActor getActor(){
        return actor;
    }

    public void setClan(Clan clan){
        this.clan = clan;
    }

    public Clan getClan() {
        return clan;
    }

    public ClanRank getRank() {
        return rank;
    }

    public int getExp() {
        return exp;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }
}
