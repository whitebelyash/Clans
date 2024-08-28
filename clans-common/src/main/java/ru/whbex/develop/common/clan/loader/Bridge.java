package ru.whbex.develop.common.clan.loader;

import ru.whbex.develop.common.clan.Clan;

import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

public interface Bridge {

    boolean valid(); // exception is handled on method side, call this before calling other methods

    Clan fetchClan(String tag);
    Clan fetchClan(UUID id);
    Collection<Clan> fetchAll();

    UUID fetchUUIDFromTag(String tag);
    String fetchTagFromUUID(UUID id);


    boolean updateClan(Clan clan);
    void updateAll(Collection<Clan> clans);

    void insertClan(Clan clan);
    void insertAll(Collection<Clan> clans);
}
