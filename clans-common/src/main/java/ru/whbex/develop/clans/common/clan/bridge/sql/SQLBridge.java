package ru.whbex.develop.clans.common.clan.bridge.sql;

import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.Constants;
import ru.whbex.develop.clans.common.clan.Clan;
import ru.whbex.develop.clans.common.clan.ClanLevelling;
import ru.whbex.develop.clans.common.clan.ClanMeta;
import ru.whbex.develop.clans.common.clan.ClanRank;
import ru.whbex.develop.clans.common.clan.bridge.Bridge;
import ru.whbex.develop.clans.common.clan.member.Member;
import ru.whbex.develop.clans.common.db.SQLAdapter;
import ru.whbex.develop.clans.common.db.SQLCallback;
import ru.whbex.develop.clans.common.misc.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/* SQLAdapter bridge to ClanManager */
/* Anything here must be run in the same thread as the SQLAdapter. */
public abstract class SQLBridge implements Bridge {
    private final SQLAdapter adapter;

    private static final String TAG_QUERY_SQL = "SELECT * FROM clans WHERE tag=?;";
    private static final String UUID_QUERY_SQL = "SELECT * FROM clans WHERE id=?;";
    /*
    ID, TAG, NAME, DESCRIPTION, CREATIONEPOCH, LEADER, DELETED, LEVEL, EXP, DEFAULTRANK
     */
    private static final String INSERT_SQL = "INSERT INTO clans VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private static final String REPLACE_SQL = "REPLACE INTO clans(id, tag, name, description, creationEpoch, leader, deleted, level, exp, defaultRank) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    public SQLBridge(SQLAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public boolean valid() {
        try {
            return adapter.isClosed() && adapter.isValid();
        } catch (SQLException e) {
            return false;
        }
    }

    protected Clan clanFromQuery(UUID id, String tag, ResultSet rs) throws SQLException {
        String name = rs.getString("name");
        String description = rs.getString("description");
        long time = rs.getLong("creationEpoch");
        ClanRank rank;
        int rid = rs.getInt("defaultRank");
        if (rid < 0 || rid >= ClanRank.values().length) {
            ClansPlugin.log(Level.ERROR, "Invalid default rank id! Using default");
            rank = Constants.DEFAULT_RANK;
        } else rank = ClanRank.values()[rid];
        UUID lid;
        if ((lid = StringUtils.UUIDFromString(rs.getString("leader"))) == null) {
            ClansPlugin.log(Level.ERROR, "Failed to load clan {0}: leader UUID is null!");
            return null;
        }
        ClansPlugin.Context.INSTANCE.plugin.getPlayerManager().getOrRegisterPlayerActor(lid).sendMessage("Clan load!"); // TODO: remove
        boolean deleted = rs.getBoolean("deleted");
        int lvl = rs.getInt("level");
        int exp = rs.getInt("exp");
        ClanLevelling levelling = new ClanLevelling(lvl, exp);
        ClanMeta meta = new ClanMeta(tag, name, description, lid, time, rank);
        Clan c = new Clan(ClansPlugin.Context.INSTANCE.plugin.getClanManager(), id, meta, levelling, false);
        c.setDeleted(deleted);
        return c;
    }

    protected void clanToPrepStatement(PreparedStatement ps, Clan clan) throws SQLException {
        ps.setString(1, clan.getId().toString());
        ps.setString(2, clan.getMeta().getTag());
        ps.setString(3, clan.getMeta().getName());
        ps.setString(4, clan.getMeta().getDescription());
        ps.setLong(5, clan.getMeta().getCreationTime());
        ps.setString(6, clan.getMeta().getLeader().toString());
        ps.setBoolean(7, clan.isDeleted());
        ps.setInt(8, clan.getLevelling().getLevel());
        ps.setInt(9, clan.getLevelling().getExperience());
        ps.setInt(10, clan.getMeta().getDefaultRank().ordinal());
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

    public Clan fetchClan(String tag) {
        ClansPlugin.dbg("fetch clan {0}", tag);
        AtomicReference<Clan> clan = new AtomicReference<>();
        SQLCallback<PreparedStatement> sql = ps -> {
            ps.setString(1, tag);
            return true;
        };
        SQLCallback<ResultSet> cb = rs -> {
            if (rs.next())
                do {
                    UUID id;
                    if ((id = StringUtils.UUIDFromString(rs.getString("id"))) == null) {
                        ClansPlugin.log(Level.ERROR, "Failed to load clan {0}: UUID is null!");
                        return false;
                    }
                    Clan c;
                    if((c = clanFromQuery(id, tag, rs)) == null)
                        return false;
                    clan.set(c);
                } while (rs.next());
            else {
                ClansPlugin.log(Level.ERROR, "No clan with tag " + tag + " was found!");
                return false;
            }
            return true;
        };
        boolean ret = false;
        // this is being run in the same thread as the query
        try {
            ret = adapter.queryPrepared(TAG_QUERY_SQL, sql, cb);
        } catch (SQLException e) {
            ClansPlugin.log(Level.ERROR, "Clan fetch failed: " + e.getLocalizedMessage());
            ClansPlugin.dbg_printStacktrace(e);
        }
        if (!ret) ClansPlugin.log(Level.ERROR, "Failed to fetch clan with tag {0}!", tag);
        return clan.get();
    }

    @Override
    public Clan fetchClan(UUID id) {
        ClansPlugin.dbg("fetch clan {0}", id);
        AtomicReference<Clan> clan = new AtomicReference<>();
        SQLCallback<PreparedStatement> sql = ps -> {
            ps.setString(1, id.toString());
            return true;
        };
        SQLCallback<ResultSet> cb = rs -> {
            if (rs.next())
                do {
                    String tag;
                    if ((tag = rs.getString("tag")) == null) {
                        ClansPlugin.log(Level.ERROR, "Failed to load clan {0}: tag is null!");
                        return false;
                    }
                    Clan c;
                    if((c = clanFromQuery(id, tag, rs)) == null)
                        return false;
                    clan.set(c);
                } while (rs.next());
            else {
                ClansPlugin.log(Level.ERROR, "No clan with id " + id + " was found!");
                return false;
            }
            return true;
        };
        boolean ret = false;
        // this is being run in the same thread as the query
        try {
            ret = adapter.queryPrepared(UUID_QUERY_SQL, sql, cb);
        } catch (SQLException e) {
            ClansPlugin.log(Level.ERROR, "Clan fetch failed: " + e.getLocalizedMessage());
            ClansPlugin.dbg_printStacktrace(e);
        }
        if (!ret) ClansPlugin.log(Level.ERROR, "Failed to fetch clan with UUID {0}!", id);
        return clan.get();
    }

    @Override
    public UUID fetchUUIDFromTag(String tag) {
        ClansPlugin.dbg("fetch uuid from tag " + tag);
        AtomicReference<UUID> uuid = new AtomicReference<>();
        SQLCallback<PreparedStatement> sql = ps -> {
            ps.setString(1, tag);
            return true;
        };
        SQLCallback<ResultSet> cb = rs -> {
            if (rs.next()) do {
                uuid.set(StringUtils.UUIDFromString(rs.getString("id")));
            } while (rs.next());
            return true;
        };
        try {
            adapter.queryPrepared(TAG_QUERY_SQL, sql, cb);
        } catch (SQLException e) {
            ClansPlugin.log(Level.ERROR, "Failed to fetch UUID from tag {0}!", tag);
        }
        return uuid.get();
    }

    @Override
    public String fetchTagFromUUID(UUID id) {
        ClansPlugin.dbg("fetch uuid from uuid " + id);
        SQLCallback<PreparedStatement> sql = ps -> {
            ps.setString(1, id.toString());
            return true;
        };
        AtomicReference<String> tag = new AtomicReference<>();
        SQLCallback<ResultSet> cb = rs -> {
            if (rs.next()) do {
                tag.set(rs.getString("tag"));
            } while (rs.next());
            return true;
        };
        try {
            adapter.queryPrepared(UUID_QUERY_SQL, sql, cb);
        } catch (SQLException e) {
            ClansPlugin.log(Level.ERROR, "Failed to fetch tag from UUID {0}!", id);
        }
        return tag.get();
    }

    @Override
    public Collection<Clan> fetchAll() {
        ClansPlugin.dbg("fetch all!");
        final String sql = "SELECT * FROM clans;";
        List<Clan> clans = new ArrayList<>();
        SQLCallback<ResultSet> cb = rs -> {
            boolean ret = true;
            while (rs.next()) {
                String tag;
                UUID id;
                if ((tag = rs.getString("tag")) == null) {
                    ClansPlugin.log(Level.ERROR, "Tag fetch failed: Invalid clan data on row " + rs.getRow());
                    continue;
                }
                if ((id = StringUtils.UUIDFromString(rs.getString("id"))) == null) {
                    ClansPlugin.log(Level.ERROR, "UUID fetch failed: Invalid clan data on row " + rs.getRow());
                    continue;
                }
                Clan c;
                if((c = clanFromQuery(id, tag, rs)) == null)
                    continue;
                clans.add(c);
            }
            return ret;
        };
        boolean ret;
        try {
            ret = adapter.query(sql, cb);
        } catch (SQLException e) {
            ClansPlugin.log(Level.ERROR, "Caught exception on clan loading!!!");
            ret = false;
        }
        if (!ret) ClansPlugin.log(Level.ERROR, "Clan fetch was unsuccessful, beware"); // fix
        return clans;
    }

    @Override
    public boolean insertClan(Clan clan, boolean replace) {
        ClansPlugin.dbg("clan {0} insert", clan.getId());
        SQLCallback<PreparedStatement> sql = ps -> {
            clanToPrepStatement(ps, clan);
            return true;
        };
        try {
            int rows = adapter.updatePrepared(replace ? INSERT_SQL : REPLACE_SQL, sql);
            ClansPlugin.dbg("affected rows after insert: {0}", rows);
            return true;
        } catch (SQLException e) {
            ClansPlugin.log(Level.ERROR, "Caught exception inserting clan " + clan.getId());
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
                clanToPrepStatement(ps, clan);
                ps.addBatch();
            }
            return true;
        };
        try {
            adapter.updateBatched(replace ? REPLACE_SQL : INSERT_SQL, sql);
            return true;
        } catch (SQLException e) {
            ClansPlugin.log(Level.ERROR, "Caught exception inserting clans!!");
            return false;
        }
    }
}
