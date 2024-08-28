package ru.whbex.develop.common.misc;

import ru.whbex.develop.common.ClansPlugin;
import ru.whbex.develop.common.clan.Clan;
import ru.whbex.develop.common.clan.ClanManager;

import java.util.UUID;

public class ClanUtils {

    public static boolean isClanMember(UUID playerId){
        return false;
    }
    public void notify(String message){

    }

    public static boolean validateClan(Clan clan){
        return clan.getMeta().getName() == null ||
                clan.getMeta().getLeader() == null ||
                clan.getMeta().getTag() == null ||
                clan.getLevelling().getLevel() == 0;

    }


}
