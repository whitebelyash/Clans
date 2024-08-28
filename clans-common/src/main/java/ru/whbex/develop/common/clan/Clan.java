package ru.whbex.develop.common.clan;


import ru.whbex.develop.common.ClansPlugin;

import java.util.UUID;

// Clan class
public class Clan {
    private final ClanManager cm;

    private final UUID clanId;
    private final ClanMeta meta;
    private final ClanLevelling levelling;

    private boolean isDeleted = false;

    public Clan(ClanManager cm, UUID clanId, ClanMeta meta, ClanSettings settings, ClanLevelling levelling){
        this.cm = cm;
        this.clanId = clanId;
        this.meta = meta;
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


    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }



    ClanManager clanManager(){
        return cm;
    }
}
