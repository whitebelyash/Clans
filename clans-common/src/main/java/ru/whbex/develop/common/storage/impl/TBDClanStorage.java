package ru.whbex.develop.common.storage.impl;

import ru.whbex.develop.common.Clans;
import ru.whbex.develop.common.clan.ClanLevelling;
import ru.whbex.develop.common.clan.ClanMeta;
import ru.whbex.develop.common.clan.ClanSettings;
import ru.whbex.develop.common.clan.ClanMember;
import ru.whbex.develop.common.storage.ClanStorage;
import ru.whbex.develop.common.storage.MemberStorage;

import java.util.*;

public class TBDClanStorage implements ClanStorage, MemberStorage {
    private UUID dummyId = new UUID(127, 127);
    private UUID leaderId = new UUID(128, 128);
    private ClanMeta dummyMeta = new ClanMeta("Dummy", "Dummy clan",null, leaderId, 0);
    private ClanSettings dummySettings = ClanSettings.builder().build();
    private ClanLevelling dummyLevelling = new ClanLevelling(0);


    public TBDClanStorage(){
        Clans.dbg("Created dummy storage instance");
    }
    @Override
    public ClanMeta loadMeta(UUID clanId) {
        return dummyMeta;
    }

    @Override
    public ClanLevelling loadLevelling(UUID clanId) {
        return dummyLevelling;
    }

    @Override
    public ClanSettings loadSettings(UUID clanId) {
        return dummySettings;
    }



    @Override
    public void saveMeta(UUID clanId, ClanMeta meta) {
        Clans.dbg("Meta save called for " + clanId);

    }

    @Override
    public void saveLevelling(UUID clanId, ClanLevelling levelling) {
        Clans.dbg("Levelling save called for " + clanId);

    }

    @Override
    public void saveSettings(UUID clanId, ClanSettings settings) {
        Clans.dbg("Settings save called for " + clanId);

    }


    @Override
    public Set<UUID> loadAllUUID() {
        return Collections.singleton(dummyId);
    }

    @Override
    public boolean clanExists(UUID clanId) {
        return clanId == dummyId;
    }

    @Override
    public void clanRemove(UUID clanId) {
        Clans.dbg("Clan remove called for " + clanId);

    }

    @Override
    public void close() {
        Clans.dbg("Closing dummy storage");

    }

    @Override
    public ClanMember loadMember() {
        return null;
    }

    @Override
    public void saveMember(ClanMember member) {

    }

    @Override
    public Collection<ClanMember> getAll(UUID clanId) {
        return Collections.emptySet();
    }

    @Override
    public void wipe() {

    }
}
