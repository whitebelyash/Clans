package ru.whbex.develop.clans.common.clan.bridge;

import ru.whbex.develop.clans.common.clan.Clan;
import ru.whbex.develop.clans.common.clan.member.Member;

import java.util.Collection;
import java.util.UUID;

public interface Bridge {


    void init();


    Clan fetchClan(String tag);
    Clan fetchClan(UUID id);
    Collection<Clan> fetchAll();

    UUID fetchUUIDFromTag(String tag);
    String fetchTagFromUUID(UUID id);

    boolean insertClan(Clan clan, boolean replace);
    boolean insertAll(Collection<Clan> clans, boolean replace);

}
