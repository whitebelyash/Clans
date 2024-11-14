package ru.whbex.develop.clans.common.clan.bridge.sql;

import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.DatabaseService;
import ru.whbex.develop.clans.common.clan.Clan;
import ru.whbex.lib.log.Debug;
import ru.whbex.lib.log.LogContext;
import ru.whbex.lib.sql.SQLAdapter;
import ru.whbex.lib.sql.SQLCallback;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SQLiteBridge extends SQLBridge {
    /*
ID, TAG, NAME, DESCRIPTION, CREATIONEPOCH, LEADER, DELETED, LEVEL, EXP, DEFAULTRANK
 */
    private static final String INSERT_SQL = "INSERT OR REPLACE INTO " + "" +
            "clans(id, tag, name, description, creationEpoch, leader, deleted, level, exp, defaultRank) " +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    @Override
    public void init() {
        DatabaseService.getExecutor(SQLAdapter::update)
                .sql("CREATE TABLE IF NOT EXISTS clans (" +
                        "id varchar(36) NOT NULL UNIQUE PRIMARY KEY, " +
                        "tag varchar(16) NOT NULL, " + // this can't be unique as deleted clans can have same tag
                        "name varchar(24), " +
                        "description varchar(255), " +
                        "creationEpoch LONG, " + // TODO: fixxx
                        "leader varchar(36) NOT NULL, " +
                        "deleted TINYINT, " +
                        "level INT, " +
                        "exp INT, " +
                        "defaultRank INT);")
                .setVerbose(true)
                .execute();
    }

    @Override
    public boolean insertClan(Clan clan, boolean replace) {
        Debug.print("clan {0} insert", clan.getId());
        if(clan.isTransient())
            return false;
        AtomicBoolean ret = new AtomicBoolean(true);
        SQLCallback<PreparedStatement, Void> sql = ps -> {
            clanToPrepStatement(ps, clan);
            return null;
        };
        DatabaseService.getExecutor(SQLAdapter::preparedQuery)
                .sql(INSERT_SQL)
                .setVerbose(true)
                .setPrepared(sql)
                .exceptionally(e -> {
                    LogContext.log(Level.ERROR, "Failed to insert clan {0}/{1}", clan.getId(), clan.getMeta().getTag());
                    ret.set(false);
                })
                .execute();
        return ret.get();
    }

    @Override
    public boolean insertAll(Collection<Clan> clans, boolean replace) {
        if (clans.isEmpty()) {
            LogContext.log(Level.WARN, "Tried to insert empty clan collection");
            return false;
        }
        AtomicInteger failed = new AtomicInteger();
        SQLAdapter<Void>.Executor<Void> exec = DatabaseService.getExecutor(SQLAdapter::preparedUpdate)
                .sql(INSERT_SQL)
                .setVerbose(true)
                .exceptionally(e -> {
                    failed.incrementAndGet();
                    LogContext.log(Level.ERROR, "Failed to insert clan!");
                });
        clans.forEach(c -> {
            if(c.isTransient())
                return;
            exec.addPrepared(ps -> {
                clanToPrepStatement(ps, c);
                return null;
            });
        });
        exec.execute();
        return true; // return failed result
    }
}
