package ru.whbex.develop.clan.member;

import ru.whbex.develop.clan.ClanRank;

import java.util.UUID;

public class Member {
    private final UUID playerId;
    private UUID clanId;
    private int exp = 0;
    private int kills = 0;
    private int deaths = 0;
    private ClanRank rank;

    public Member(UUID playerId, UUID clanId, int exp, int kills, int deaths, ClanRank rank){
        this.playerId = playerId;
        this.exp = exp;
        this.kills = kills;
        this.deaths = deaths;
        this.rank = rank;
        this.clanId = clanId;
    }

    public void setClanId(UUID clanId) {
        this.clanId = clanId;
    }
}
