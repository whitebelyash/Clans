package ru.whbex.develop.storage.impl;

import ru.whbex.develop.Clans;
import ru.whbex.develop.clan.ClanLevelling;
import ru.whbex.develop.clan.ClanMeta;
import ru.whbex.develop.clan.ClanSettings;
import ru.whbex.develop.clan.member.MemberHolder;
import ru.whbex.develop.storage.ClanStorage;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TBDClanStorage implements ClanStorage {

    public TBDClanStorage(){
        Clans.LOGGER.info("Created dummy storage instance");
    }
    @Override
    public ClanMeta loadMeta(UUID clanId) {
        return null;
    }

    @Override
    public ClanLevelling loadLevelling(UUID clanId) {
        return null;
    }

    @Override
    public ClanSettings loadSettings(UUID clanId) {
        return null;
    }

    @Override
    public MemberHolder loadMembers(UUID clanId) {
        return new MemberHolder();
    }

    @Override
    public void saveMeta(UUID clanId, ClanMeta meta) {

    }

    @Override
    public void saveLevelling(UUID clanId, ClanLevelling levelling) {

    }

    @Override
    public void saveSettings(UUID clanId, ClanSettings settings) {

    }

    @Override
    public void saveMembers(UUID clanId, MemberHolder holder) {

    }

    @Override
    public boolean updateMeta(UUID clanId, ClanMeta meta) {
        return true;
    }

    @Override
    public boolean updateLevelling(UUID clanId, ClanLevelling levelling) {
        return true;
    }

    @Override
    public boolean updateSettings(UUID clanId, ClanSettings settings) {
        return true;
    }

    @Override
    public boolean updateMembers(UUID clanId, MemberHolder holder) {
        return false;
    }

    @Override
    public Set<UUID> loadAllUUID() {
        return new HashSet<>();
    }

    @Override
    public boolean clanExists(UUID clanId) {
        return false;
    }

    @Override
    public void clanRemove(UUID clanId) {

    }

    @Override
    public void close() {

    }

    @Override
    public void wipe() {

    }
}
