package ru.whbex.develop.storage;

import ru.whbex.develop.clan.member.Member;

import java.util.Collection;
import java.util.UUID;

public interface MemberStorage {
    Member loadMember();
    void saveMember(Member member);
    Collection<Member> getAll(UUID clanId);
    void wipe();
    void close();
}
