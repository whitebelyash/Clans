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

    private boolean insert = true;

    public Clan(ClanManager cm, UUID clanId, ClanMeta meta, ClanSettings settings, ClanLevelling levelling, boolean insert){
        this.cm = cm;
        this.clanId = clanId;
        this.meta = meta;
        this.levelling = levelling;
        this.insert = insert;
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

    boolean insert(){
        return insert;
    }
}
