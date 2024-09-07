package ru.whbex.develop.clans.common.clan.loader;

import ru.whbex.develop.clans.common.clan.Clan;

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
    boolean updateAll(Collection<Clan> clans);

    boolean insertClan(Clan clan, boolean replace);
    boolean insertAll(Collection<Clan> clans, boolean replace);
}
