package ru.whbex.develop.core.storage;

import ru.whbex.develop.core.clan.member.Member;

import java.util.Collection;
import java.util.UUID;

public interface MemberStorage {
    Member loadMember();
    void saveMember(Member member);
    Collection<Member> getAll(UUID clanId);
    void wipe();
    void close();
}
