package ru.whbex.develop.clans.common.clan.loader;

import ru.whbex.develop.clans.common.clan.Clan;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class NullBridge implements Bridge {
    @Override
    public boolean valid() {
        return true;
    }

    @Override
    public void init() {

    }

    @Override
    public Clan fetchClan(String tag) {
        return null;
    }

    @Override
    public Clan fetchClan(UUID id) {
        return null;
    }

    @Override
    public Collection<Clan> fetchAll() {
        return List.of();
    }

    @Override
    public UUID fetchUUIDFromTag(String tag) {
        return null;
    }

    @Override
    public String fetchTagFromUUID(UUID id) {
        return null;
    }

    @Override
    public boolean updateClan(Clan clan) {
        return false;
    }

    @Override
    public boolean updateAll(Collection<Clan> clans) {
        return false;
    }

    @Override
    public boolean insertClan(Clan clan, boolean replace) {
        return false;
    }

    @Override
    public boolean insertAll(Collection<Clan> clans, boolean replace) {
        return false;
    }
}
