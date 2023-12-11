package ru.whbex.develop.core.clan;


import java.util.UUID;

// Clan class
public class Clan {
    private final UUID clanId;
    private final ClanMeta meta;
    private final ClanSettings settings;
    private final ClanLevelling levelling;

    public Clan(UUID clanId, ClanMeta meta, ClanSettings settings, ClanLevelling levelling){
        this.clanId = clanId;
        this.meta = meta;
        this.settings = settings;
        this.levelling = levelling;
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

}
