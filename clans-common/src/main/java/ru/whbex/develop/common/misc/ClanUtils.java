package ru.whbex.develop.common.misc;

import ru.whbex.develop.common.ClansPlugin;
import ru.whbex.develop.common.clan.ClanManager;

import java.util.UUID;

public class ClanUtils {

    public static boolean isClanMember(UUID playerId){
        ClanManager cm = ClansPlugin.Context.INSTANCE.plugin.getClanManager();
        return cm.getMemberHolder().memberExists(playerId) &&
                cm.getMemberHolder().getMember(playerId).getClan() != null &&
                cm.clanRegistered(cm.getMemberHolder().getMember(playerId).getClan());
    }
    public void notify(String message){

    }


}
