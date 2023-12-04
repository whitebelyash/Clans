package ru.whbex.develop.clan;

public enum ClanRank {

    // Specify color rank in lang file
    NOVICE(LocaleString.RANK_NOVICE),
    MEMBER(LocaleString.RANK_MEMBER),
    MANAGER(LocaleString.RANK_MANAGER),
    DEPUTY(LocaleString.RANK_DEPUTY),
    LEADER(LocaleString.RANK_LEADER)
    ;
    public final LocaleString name;

    ClanRank(LocaleString name){
        this.name = name;

    }
}
