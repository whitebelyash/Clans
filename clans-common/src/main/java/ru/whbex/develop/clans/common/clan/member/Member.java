package ru.whbex.develop.clans.common.clan.member;

import ru.whbex.develop.clans.common.clan.Clan;
import ru.whbex.develop.clans.common.clan.ClanRank;
import ru.whbex.develop.clans.common.player.PlayerActor;

import java.util.UUID;

public class Member {
    private final PlayerActor actor;
    private Clan clan;
    private final int exp;
    private int kills = 0;
    private int deaths = 0;
    private final ClanRank rank;

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
