package ru.whbex.develop.clans.common.clan;

public enum ClanRank {

    NOVICE("rank.novice"),
    MEMBER("rank.member"),
    RECRUITER("rank.recruiter"),
    DEPUTY("rank.deputy"),
    LEADER("rank.leader")
    ;
    public final String name;

    ClanRank(String name){
        this.name = name;

    }
}
