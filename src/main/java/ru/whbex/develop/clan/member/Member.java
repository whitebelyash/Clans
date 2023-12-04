package ru.whbex.develop.clan.member;

import ru.whbex.develop.clan.Clan;
import ru.whbex.develop.clan.ClanRank;

import java.util.UUID;

public class Member {
    private final UUID playerId;
    private Clan clan;
    private int exp;
    private int kills = 0;
    private int deaths = 0;
    private ClanRank rank;

    public Member(UUID playerId, Clan clan, int exp, int kills, int deaths, ClanRank rank){
        this.playerId = playerId;
        this.exp = exp;
        this.kills = kills;
        this.deaths = deaths;
        this.rank = rank;
        this.clan = clan;
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
}
