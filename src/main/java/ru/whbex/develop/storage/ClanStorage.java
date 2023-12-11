package ru.whbex.develop.storage;

import ru.whbex.develop.clan.ClanLevelling;
import ru.whbex.develop.clan.ClanMeta;
import ru.whbex.develop.clan.ClanSettings;
import ru.whbex.develop.clan.member.MemberHolder;

import java.util.Set;
import java.util.UUID;

// ClanDao

public interface ClanStorage {

    // load

    ClanMeta loadMeta(UUID clanId);
    ClanLevelling loadLevelling(UUID clanId);
    ClanSettings loadSettings(UUID clanId);

    // save

    void saveMeta(UUID clanId, ClanMeta meta);
    void saveLevelling(UUID clanId, ClanLevelling levelling);
    void saveSettings(UUID clanId, ClanSettings settings);


    // Utilities

    Set<UUID> loadAllUUID();
    boolean clanExists(UUID clanId);
    void clanRemove(UUID clanId);
    void close();



    // pls don't call it bro
    void wipe();

}
