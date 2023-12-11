package ru.whbex.develop.clan.member;

import ru.whbex.develop.clan.Clan;
import ru.whbex.develop.clan.ClanManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemberHolder {
    private final Map<UUID, Member> members = new HashMap<>();
    private final Map<Member, Clan> clanByMember = new HashMap<>();
    private final ClanManager cm;

    public MemberHolder(ClanManager cm){
        this.cm = cm;
    }

    public Collection<Member> getMembers(){
        return members.values();
    }
    public void addMember(UUID id, Member member, Clan clan) throws IllegalArgumentException {
        if(members.containsValue(member))
            throw new IllegalArgumentException("Member already exists!");

        members.put(id, member);
        clanByMember.put(member, clan);
        member.setClan(clan);
    }
    public boolean memberExists(UUID id){
        return members.containsKey(id);
    }
    public boolean memberExists(Member member){
        return members.containsValue(member);
    }
    public Member getMember(UUID id){
        return members.get(id);
    }
}
