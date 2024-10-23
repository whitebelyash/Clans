package ru.whbex.develop.clans.common.clan.bridge.sql;

import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.db.SQLAdapter;

import java.sql.SQLException;

public class H2Bridge extends SQLBridge{
    public H2Bridge(SQLAdapter adapter) {
        super(adapter);
    }


    @Override
    public void init() {
        try {
                /*
                ID, TAG, NAME, DESCRIPTION, CREATIONEPOCH, LEADER, DELETED, LEVEL, EXP
                 */
            adapter.update("CREATE TABLE IF NOT EXISTS clans (id varchar(36), tag varchar(16), " +
                    "name varchar(24), " +
                    "description varchar(255), " +
                    "creationEpoch LONG, " + // TODO: fixxx
                    "leader varchar(36), " +
                    "deleted TINYINT, " +
                    "level INT, " +
                    "exp INT, " +
                    "defaultRank INT);");
        } catch (SQLException e) {
            ClansPlugin.log(Level.ERROR, "Failed to execute initial SQL Update: " + e.getLocalizedMessage());
        }
    }
}
