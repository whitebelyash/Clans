package ru.whbex.develop.clans.common.clan.sql;

import ru.whbex.develop.clans.common.ClansPlugin;

// Do we even have a better way to implement this?
public enum SQLString {
    REPLACE_OR_INSERT_CLANS("INSERT OR REPLACE INTO " + "" +
            "clans(id, tag, name, description, creationEpoch, leader, deleted, level, exp, defaultRank) " +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", "MERGE INTO clans KEY(id) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

    public final String SQLite;
    public final String H2;
    SQLString(String sqlite, String h2){
        this.SQLite = sqlite;
        this.H2 = h2;
    }
    public String current(){
        return switch(ClansPlugin.config().getDatabaseBackend()){
            case H2 -> H2;
            case SQLITE -> SQLite;
        };
    }
}
