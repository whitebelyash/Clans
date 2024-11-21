package ru.whbex.develop.clans.common.clan.bridge.sql;

import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.Constants;
import ru.whbex.develop.clans.common.task.DatabaseService;
import ru.whbex.develop.clans.common.clan.Clan;
import ru.whbex.develop.clans.common.clan.ClanLevelling;
import ru.whbex.develop.clans.common.clan.ClanMeta;
import ru.whbex.develop.clans.common.clan.ClanRank;
import ru.whbex.develop.clans.common.clan.bridge.Bridge;
import ru.whbex.lib.log.Debug;
import ru.whbex.lib.log.LogContext;
import ru.whbex.lib.sql.SQLAdapter;
import ru.whbex.lib.sql.SQLCallback;
import ru.whbex.lib.sql.SQLResponse;
import ru.whbex.lib.string.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/* SQLAdapter bridge to ClanManager */
public abstract class SQLBridge implements Bridge {

    private static final String TAG_QUERY_SQL = "SELECT * FROM clans WHERE tag=?;";
    private static final String UUID_QUERY_SQL = "SELECT * FROM clans WHERE id=?;";

    protected Clan clanFromQuery(UUID id, String tag, ResultSet rs) throws SQLException {
        String name = rs.getString("name");
        String description = rs.getString("description");
        long time = rs.getLong("creationEpoch");
        ClanRank rank;
        int rid = rs.getInt("defaultRank");
        if (rid < 0 || rid >= ClanRank.values().length) {
        LogContext.log(Level.ERROR, "Invalid default rank id {0}!", rid);
            rank = Constants.DEFAULT_RANK;
        } else rank = ClanRank.values()[rid];
        String lid_s = rs.getString("leader");
        UUID lid;
        if ((lid = StringUtils.UUIDFromString(lid_s)) == null) {
            LogContext.log(Level.ERROR, "Invalid leader UUID {0}!", lid_s);
            return null;
        }
        boolean deleted = rs.getBoolean("deleted");
        int lvl = rs.getInt("level");
        int exp = rs.getInt("exp");
        if(lvl < 0 || exp < 0){
            LogContext.log(Level.ERROR, "Invalid level/experience data {0}/{1}!", lvl, exp);
            lvl = 0; exp = 0;
        }
        ClanLevelling levelling = new ClanLevelling(lvl, exp);
        ClanMeta meta = new ClanMeta(tag, name, description, lid, time, rank);
        Clan c = new Clan(id, meta, levelling, false);
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
        ps.setInt(7, clan.isDeleted() ? 1 : 0);
        ps.setInt(8, clan.getLevelling().getLevel());
        ps.setInt(9, clan.getLevelling().getExperience());
        ps.setInt(10, clan.getMeta().getDefaultRank().ordinal());
    }

    // Fetching clan from a tag is not safe as the tag is not unique unlike id
    public Clan fetchClan(String tag) {
        /*
        Debug.print("fetch clan {0}", tag);
        SQLCallback<PreparedStatement, Void> sql = ps -> {
            ps.setString(1, tag);
            return null;
        };
        SQLCallback<SQLResponse, Clan> cb = r -> {
            if (rs.next())
                do {
                    UUID id;
                    if ((id = StringUtils.UUIDFromString(rs.getString("id"))) == null) {
                        LogContext.log(Level.ERROR, "UUID of clan {0} is null!", tag);
                        return null;
                    }
                    return clanFromQuery(id, tag, rs);
                } while (rs.next());
            else {
                LogContext.log(Level.ERROR, "No clan with tag {0} was found!", tag);
                return false;
            }
            return true;
        };
        Clan clan = DatabaseService.getExecutor(Clan.class, SQLAdapter::preparedQuery)
                .sql("SELECT * FROM clans WHERE tag=?")
        if (!ret) LogContext.log(Level.ERROR, "Failed to fetch clan with tag {0}!", tag);
        return clan.get();
         */
        // TODO: Complete this
        throw new UnsupportedOperationException("WIP");
    }

    @Override
    public Clan fetchClan(UUID id) {
        Debug.print("fetch clan {0}", id);
        SQLCallback.PreparedCallback sql = ps -> {
            ps.setString(1, id.toString());
        };
        SQLCallback<SQLResponse, Clan> cb = resp -> {
            ResultSet rs = resp.resultSet();
            if (rs.next())
                do {
                    String tag;
                    if ((tag = rs.getString("tag")) == null) {
                        LogContext.log(Level.ERROR, "Tag of clan with UUID {0} is null!", id);
                        return null;
                    }
                    return clanFromQuery(id, tag, rs);
                } while (rs.next());
            else {
                LogContext.log(Level.ERROR, "No clan with UUID {0} was found!", id);
                return null;
            }
        };
        Clan clan = DatabaseService.getExecutor(Clan.class, SQLAdapter::preparedQuery)
                        .sql("SELECT * FROM clans WHERE id=?")
                        .setVerbose(true)
                        .setPrepared(sql)
                        .queryCallback(cb)
                        .execute();
        if (clan == null) LogContext.log(Level.ERROR, "Failed to fetch clan with UUID {0}!", id);
        return clan;
    }

    @Override
    public UUID fetchUUIDFromTag(String tag) {
        Debug.print("fetch uuid from tag " + tag);
        SQLCallback.PreparedCallback sql = ps -> {
            ps.setString(1, tag);
        };
        SQLCallback<SQLResponse, UUID> cb = resp -> {
            ResultSet rs = resp.resultSet();
            if (rs.next()) do {
                return UUID.fromString(rs.getString("id"));
            } while (rs.next());
            return null;
        };
        return DatabaseService.getExecutor(UUID.class, SQLAdapter::preparedQuery)
                .sql("SELECT * FROM clans WHERE tag=?")
                .setPrepared(sql)
                .queryCallback(cb)
                .setVerbose(true)
                .execute();
    }

    @Override
    public String fetchTagFromUUID(UUID id) {
        Debug.print("fetch uuid from uuid " + id);
        SQLCallback.PreparedCallback sql = ps -> {
            ps.setString(1, id.toString());
        };
        SQLCallback<SQLResponse, String> cb = resp -> {
            ResultSet rs = resp.resultSet();
            if (rs.next()) do {
                return rs.getString("tag");
            } while (rs.next());
            return null;
        };
        return DatabaseService.getExecutor(String.class, SQLAdapter::preparedQuery)
                .sql("SELECT * FROM clans WHERE id=?")
                .setVerbose(true)
                .setPrepared(sql)
                .queryCallback(cb)
                .execute();
    }

    @Override
    public Collection<Clan> fetchAll() {
        Debug.print("fetch all!");
        final String sql = "SELECT * FROM clans;";
        List<Clan> clans = new ArrayList<>();
        SQLCallback<SQLResponse, Integer> cb = resp -> {
            ResultSet rs = resp.resultSet();
            int am = 0;
            while (rs.next()) {
                String tag;
                UUID id;
                if ((tag = rs.getString("tag")) == null) {
                    LogContext.log(Level.ERROR, "Invalid tag data on row {0}!",  rs.getRow());
                    continue;
                }
                if ((id = StringUtils.UUIDFromString(rs.getString("id"))) == null) {
                    LogContext.log(Level.ERROR, "Invalid UUID data on row {0}!", rs.getRow());
                    continue;
                }
                Clan c;
                if((c = clanFromQuery(id, tag, rs)) == null)
                    continue;
                clans.add(c);
                am++;
            }
            return am;
        };
        // do something with this
        int success = DatabaseService.getExecutor(Integer.class, SQLAdapter::query)
                .sql(sql)
                .queryCallback(cb)
                .setVerbose(true)
                .execute();
        return clans;
    }
    public abstract boolean insertClan(Clan clan, boolean replace);
    public abstract boolean insertAll(Collection<Clan> clans, boolean replace);

}
