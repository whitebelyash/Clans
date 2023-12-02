package ru.whbex.develop.clan;


import ru.whbex.develop.clan.member.MemberHolder;

import java.util.UUID;

// Clan class
public class Clan {
    private final UUID clanId;
    private final ClanMeta meta;
    private final ClanSettings settings;
    private final ClanLevelling levelling;
    private MemberHolder holder;

    public Clan(UUID clanId, ClanMeta meta, ClanSettings settings, ClanLevelling levelling, MemberHolder holder){
        this.clanId = clanId;
        this.meta = meta;
        this.settings = settings;
        this.levelling = levelling;
        this.holder = holder;
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

    public ClanSettings getSettings() {
        return settings;
    }

    public MemberHolder getMemberHolder() {
        return holder;
    }

    void setMemberHolder(MemberHolder memberHolder){
        this.holder = memberHolder;
    }
}
