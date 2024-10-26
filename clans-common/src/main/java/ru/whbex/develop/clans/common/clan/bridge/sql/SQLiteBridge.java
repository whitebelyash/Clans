package ru.whbex.develop.clans.common.clan.bridge.sql;

import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.clan.Clan;
import ru.whbex.lib.sql.SQLAdapter;
import ru.whbex.lib.sql.SQLCallback;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class SQLiteBridge extends SQLBridge {
    /*
ID, TAG, NAME, DESCRIPTION, CREATIONEPOCH, LEADER, DELETED, LEVEL, EXP, DEFAULTRANK
 */
    private static final String INSERT_SQL = "INSERT OR REPLACE INTO " + "" +
            "clans(id, tag, name, description, creationEpoch, leader, deleted, level, exp, defaultRank) " +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

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
                    "tag varchar(16) NOT NULL, " + // this can't be unique as deleted clans can have same tag
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

    @Override
    public boolean insertClan(Clan clan, boolean replace) {
        ClansPlugin.dbg("clan {0} insert", clan.getId());
        SQLCallback<PreparedStatement> sql = ps -> {
            clanToPrepStatement(ps, clan);
            return true;
        };
        try {
            int rows = adapter.updatePrepared(INSERT_SQL, sql);
            ClansPlugin.dbg("affected rows after insert: {0}", rows);
            return true;
        } catch (SQLException e) {
            ClansPlugin.log(Level.ERROR, "Failed inserting clan {0}/{1}", clan.getId(), clan.getMeta().getTag());
            return false;
        }
    }

    @Override
    public boolean insertAll(Collection<Clan> clans, boolean replace) {
        if (clans.isEmpty()) {
            ClansPlugin.log(Level.WARN, "Tried to insert empty clan collection");
            return false;
        }
        SQLCallback<PreparedStatement> sql = ps -> {
            for (Clan clan : clans) {
                if(clan.isTransient())
                    continue;
                clanToPrepStatement(ps, clan);
                ps.addBatch();
            }
            return true;
        };
        try {
            adapter.updateBatched(INSERT_SQL, sql);
            return true;
        } catch (SQLException e) {
            ClansPlugin.dbg_printStacktrace(e);
            return false;
        }
    }
}
