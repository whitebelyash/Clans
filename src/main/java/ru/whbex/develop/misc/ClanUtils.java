package ru.whbex.develop.misc;

import ru.whbex.develop.Clans;
import ru.whbex.develop.clan.ClanManager;

import java.util.UUID;

public class ClanUtils {
    private static final ClanManager cm = Clans.instance().getClanManager();

    public static boolean isClanMember(UUID playerId){
        return cm.getMemberHolder().memberExists(playerId) &&
                cm.getMemberHolder().getMember(playerId).getClan() != null &&
                cm.clanRegistered(cm.getMemberHolder().getMember(playerId).getClan());
    }
    public void notify(String message){

    }


}
