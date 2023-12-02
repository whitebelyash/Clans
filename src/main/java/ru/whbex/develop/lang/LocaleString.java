package ru.whbex.develop.lang;

public enum LocaleString {
    RANK_NOVICE("clan.rank.novice"),
    RANK_MEMBER("clan.rank.member"),
    // TODO: RENAME
    RANK_MANAGER("clan.rank.manager"),
    RANK_DEPUTY("clan.rank.deputy"),
    RANK_LEADER("clan.rank.leader"),
    ROLE_UNSPECIFIED("clan.role.unspecified"),

    META_COMMAND_USAGE("meta.command.usage"),
    COMMAND_CREATE_USAGE("command.create.usage"),
    COMMAND_CREATE_SUCCESS("command.create.success"),
    COMMAND_CREATE_TAG_TAKEN("command.create.tag-exists"),
    COMMAND_CREATE_INVALID_TAG("command.create.tag-invalid"),
    META_BUTTON_NEXT("meta.button.next"),
    META_BUTTON_PREV("meta.button.prev"),
    ;







    public final String path;


    LocaleString(String path){
        this.path = path;

    }
}
