package ru.whbex.develop.clans.common.clan;

public enum ClanRank {

    NOVICE("rank.novice", '7'),
    MEMBER("rank.member", 'f'),
    RECRUITER("rank.recruiter", 'b'),
    DEPUTY("rank.deputy", '6'),
    LEADER("rank.leader", 'c')
    ;
    public final String name;
    public final char color;

    ClanRank(String name, char color){
        this.name = name;
        this.color = color;
    }
}
