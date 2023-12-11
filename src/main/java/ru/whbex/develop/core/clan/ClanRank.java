package ru.whbex.develop.core.clan;

public enum ClanRank {

    // Specify color rank in lang file
    NOVICE("rank.novice"),
    MEMBER("rank.member"),
    MANAGER("rank.manager"),
    DEPUTY("rank.deputy"),
    LEADER("rank.leader")
    ;
    public final String name;

    ClanRank(String name){
        this.name = name;

    }
}
