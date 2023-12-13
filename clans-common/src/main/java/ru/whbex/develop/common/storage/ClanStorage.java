package ru.whbex.develop.common.storage;

import ru.whbex.develop.common.clan.ClanLevelling;
import ru.whbex.develop.common.clan.ClanMeta;
import ru.whbex.develop.common.clan.ClanSettings;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Future;

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
