package ru.whbex.develop.clan.member;

import ru.whbex.develop.clan.Clan;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemberHolder {
    private final Map<UUID, Member> members = new HashMap<>();

    public Collection<Member> getMembers(){
        return members.values();
    }
    public void addMember(UUID id, Member member) throws IllegalArgumentException {
        if(members.containsValue(member))
            throw new IllegalArgumentException("Member already exists!");
        members.put(id, member);
    }
}
