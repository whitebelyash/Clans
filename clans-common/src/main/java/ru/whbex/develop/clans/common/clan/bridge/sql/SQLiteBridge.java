package ru.whbex.develop.clans.common.clan.bridge.sql;

import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.lib.sql.SQLAdapter;

import java.sql.SQLException;

public class SQLiteBridge extends SQLBridge {
    public SQLiteBridge(SQLAdapter adapter) {
        super(adapter);
    }

    @Override
    public void init() {
        try {
                /*
                ID, TAG, NAME, DESCRIPTION, CREATIONEPOCH, LEADER, DELETED, LEVEL, EXP
                 */
            adapter.update("CREATE TABLE IF NOT EXISTS clans (" +
                    "id varchar(36) NOT NULL UNIQUE PRIMARY KEY, " +
                    "tag varchar(16) NOT NULL, " +
                    "name varchar(24), " +
                    "description varchar(255), " +
                    "creationEpoch LONG, " + // TODO: fixxx
                    "leader varchar(36) NOT NULL, " +
                    "deleted TINYINT, " +
                    "level INT, " +
                    "exp INT, " +
                    "defaultRank INT);");
        } catch (SQLException e) {
            ClansPlugin.log(Level.ERROR, "Failed to execute initial SQL Update: " + e.getLocalizedMessage());
        }
    }
}
