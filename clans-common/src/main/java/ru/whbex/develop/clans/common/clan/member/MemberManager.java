package ru.whbex.develop.clans.common.clan.member;

import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.clan.ClanManager;
import ru.whbex.develop.clans.common.player.PlayerActor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemberManager {
    private final ClanManager inst;
    // Members map
    private final Map<UUID, Member> members = new HashMap<>();

    public MemberManager(ClanManager inst){
        this.inst = inst;
    }
    public boolean hasMember(UUID id){
        return members.containsKey(id);
    }
    public void addMember(UUID id){
        if(members.containsKey(id))
            return;
        PlayerActor a = ClansPlugin.Context.INSTANCE.plugin.getPlayerManager().getOrRegisterPlayerActor(id);
        Member m = new Member(a);
        members.put(id, m);
        ClansPlugin.dbg("new member register: " + id);
    }
    public Member getMember(UUID id){
        return members.get(id);
    }

    public Map<UUID, Member> getMembers() {
        return members;
    }
}
