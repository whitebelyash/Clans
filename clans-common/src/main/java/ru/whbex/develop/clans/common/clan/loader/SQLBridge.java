package ru.whbex.develop.clans.common.clan.loader;

import ru.whbex.develop.clans.common.misc.StringUtils;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.clan.Clan;
import ru.whbex.develop.clans.common.clan.ClanLevelling;
import ru.whbex.develop.clans.common.clan.ClanMeta;
import ru.whbex.develop.clans.common.db.SQLAdapter;
import ru.whbex.develop.clans.common.db.SQLCallback;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

/* SQLAdapter bridge to ClanManager */
/* Anything here must be run in the same thread as the SQLAdapter. */
public class SQLBridge implements Bridge {
    private final SQLAdapter adapter;

    private static final String TAG_QUERY_SQL = "SELECT * FROM clans WHERE tag=?;";
    private static final String UUID_QUERY_SQL = "SELECT * FROM clans WHERE id=?;";
    /*
    ID, TAG, NAME, DESCRIPTION, CREATIONEPOCH, LEADER, DELETED, LEVEL, EXP
     */
    private static final String INSERT_SQL = "INSERT INTO clans VALUES(?, ?, ?, ?, ?, ?, ?, ? ,?);";
    private static final String REPLACE_SQL = "REPLACE INTO clans(id, tag, name, description, creationEpoch, leader, deleted, level, exp) VALUES(?, ?, ?, ?, ?, ?, ?, ? ,?);";
    public SQLBridge(SQLAdapter adapter){
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
    public Clan fetchClan(String tag) {
        ClansPlugin.dbg("fetch clan {0}", tag);
        AtomicReference<Clan> clan = new AtomicReference<>();
        SQLCallback<PreparedStatement> sql = ps -> {ps.setString(1, tag); return true;};
        SQLCallback<ResultSet> cb = rs -> {
                if(rs.next())
                    do {
                        UUID id;
                        if((id = StringUtils.UUIDFromString(rs.getString("id"))) == null){
                            ClansPlugin.log(Level.SEVERE, "Failed to load clan {0}: UUID is null!");
                            return false;
                        }
                        String name = rs.getString("name");
                        String description = rs.getString("description");
                        long time = rs.getLong("creationEpoch");
                        UUID lid;
                        if((lid = StringUtils.UUIDFromString(rs.getString("leader"))) == null){
                            ClansPlugin.log(Level.SEVERE, "Failed to load clan {0}: leader UUID is null!");
                            return false;
                        }
                        ClansPlugin.Context.INSTANCE.plugin.getPlayerActorOrRegister(lid).sendMessage("Clan load!"); // TODO: remove
                        int lvl = rs.getInt("level");
                        int exp = rs.getInt("exp");
                        ClanLevelling levelling = new ClanLevelling(lvl, exp);
                        ClanMeta meta = new ClanMeta(tag, name, description, lid, time);
                        Clan c = new Clan(ClansPlugin.Context.INSTANCE.plugin.getClanManager(), id, meta, null, levelling, false);
                        clan.set(c);
                    } while(rs.next());
                else {
                    ClansPlugin.log(Level.SEVERE, "No clan with tag " + tag + " was found!");
                    return false;
                }
            return true;
        };
        boolean ret = false;
        // this is being run in the same thread as the query
        try {
           ret = adapter.queryPrepared(TAG_QUERY_SQL, sql, cb);
        } catch (SQLException e) {
            ClansPlugin.log(Level.SEVERE, "Clan fetch failed: " + e.getLocalizedMessage());
            ClansPlugin.dbg_printStacktrace(e);
        }
        if(!ret) ClansPlugin.log(Level.SEVERE, "Failed to fetch clan with tag {0}!", tag);
        return clan.get();
    }

    @Override
    public Clan fetchClan(UUID id) {
        ClansPlugin.dbg("fetch clan {0}", id);
        AtomicReference<Clan> clan = new AtomicReference<>();
        SQLCallback<PreparedStatement> sql = ps -> {ps.setString(1, id.toString()); return true;};
        SQLCallback<ResultSet> cb = rs -> {
                if(rs.next())
                    do {
                        String tag;
                        if((tag = rs.getString("tag")) == null){
                            ClansPlugin.log(Level.SEVERE, "Failed to load clan {0}: tag is null!");
                            return false;
                        }
                        String name = rs.getString("name");
                        String description = rs.getString("description");
                        long time = rs.getLong("creationEpoch");
                        UUID lid;
                        if((lid = StringUtils.UUIDFromString(rs.getString("leader"))) == null){
                            ClansPlugin.log(Level.SEVERE, "Failed to load clan {0}: leader UUID is null!");
                            return false;
                        }
                        ClansPlugin.Context.INSTANCE.plugin.getPlayerActorOrRegister(lid).sendMessage("Clan loaded!"); // TODO: remove
                        int lvl = rs.getInt("level");
                        int exp = rs.getInt("exp");
                        ClanLevelling levelling = new ClanLevelling(lvl, exp);
                        ClanMeta meta = new ClanMeta(tag, name, description, lid, time);
                        Clan c = new Clan(ClansPlugin.Context.INSTANCE.plugin.getClanManager(), id, meta, null, levelling, false);
                        clan.set(c);
                    } while(rs.next());
                else {
                    ClansPlugin.log(Level.SEVERE, "No clan with id " + id + " was found!");
                    return false;
                }
                return true;
        };
        boolean ret = false;
        // this is being run in the same thread as the query
        try {
            ret = adapter.queryPrepared(UUID_QUERY_SQL, sql, cb);
        } catch (SQLException e) {
            ClansPlugin.log(Level.SEVERE, "Clan fetch failed: " + e.getLocalizedMessage());
            ClansPlugin.dbg_printStacktrace(e);
        }
        if(!ret) ClansPlugin.log(Level.SEVERE, "Failed to fetch clan with UUID {0}!", id);
        return clan.get();
    }

    @Override
    public UUID fetchUUIDFromTag(String tag) {
        ClansPlugin.dbg("fetch uuid from tag " + tag);
        AtomicReference<UUID> uuid = new AtomicReference<>();
        SQLCallback<PreparedStatement> sql = ps -> {ps.setString(1, tag); return true;};
        SQLCallback<ResultSet> cb = rs -> {
                if(rs.next()) do { uuid.set(StringUtils.UUIDFromString(rs.getString("id")));
                    } while(rs.next());
                return true;
        };
        try {
            adapter.queryPrepared(TAG_QUERY_SQL, sql, cb);
        } catch (SQLException e) {
            ClansPlugin.log(Level.SEVERE, "Failed to fetch UUID from tag {0}!", tag);
        }
        return uuid.get();
    }

    @Override
    public String fetchTagFromUUID(UUID id) {
        ClansPlugin.dbg("fetch uuid from uuid " + id);
        SQLCallback<PreparedStatement> sql = ps -> {ps.setString(1, id.toString()); return true;};
        AtomicReference<String> tag = new AtomicReference<>();
        SQLCallback<ResultSet> cb = rs -> {
                if(rs.next()) do { tag.set(rs.getString("tag")); } while (rs.next());
                return true;
        };
        try {
            adapter.queryPrepared(UUID_QUERY_SQL, sql, cb);
        } catch (SQLException e) {
            ClansPlugin.log(Level.SEVERE, "Failed to fetch tag from UUID {0}!", id);
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
                while(rs.next()){
                    String tag;
                    UUID id;
                    if((tag = rs.getString("tag")) == null){
                        ClansPlugin.log(Level.SEVERE, "Tag fetch failed: Invalid clan data on row " + rs.getRow());
                        continue;
                    }
                    if((id = StringUtils.UUIDFromString(rs.getString("id"))) == null){
                        ClansPlugin.log(Level.SEVERE, "UUID fetch failed: Invalid clan data on row " + rs.getRow());
                        continue;
                    }
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    boolean deleted = rs.getBoolean("deleted");
                    long time = rs.getLong("creationEpoch");
                    UUID lid;
                    if((lid = StringUtils.UUIDFromString(rs.getString("leader"))) == null){
                        ClansPlugin.log(Level.SEVERE, "Failed to load clan {0}: leader UUID is invalid!");
                        continue;
                    }
                    ClansPlugin.Context.INSTANCE.plugin.getPlayerActorOrRegister(lid).sendMessage("Clan loaded!"); // TODO: remove
                    int lvl = rs.getInt("level");
                    int exp = rs.getInt("exp");
                    ClanLevelling levelling = new ClanLevelling(lvl, exp);
                    ClanMeta meta = new ClanMeta(tag, name, description, lid, time);
                    Clan c = new Clan(ClansPlugin.Context.INSTANCE.plugin.getClanManager(), id, meta, null, levelling, false);
                    c.setDeleted(deleted);
                    clans.add(c);
                }
                return ret;
        };
        boolean ret;
        try {
            ret = adapter.query(sql, cb);
        } catch (SQLException e) {
            ClansPlugin.log(Level.SEVERE, "Caught exception on clan loading!!!");
            ret = false;
        }
        if(!ret) ClansPlugin.log(Level.SEVERE, "Clan fetch was unsuccessful, beware"); // fix
        return clans;
    }

    @Override
    public boolean updateClan(Clan clan) {
        ClansPlugin.dbg("update clan {0}", clan.getId());
        SQLCallback<PreparedStatement> sql = ps -> {ps.setString(1, clan.getId().toString()); return true;};
        SQLCallback<ResultSet> cb = rs -> {
            if (rs.next()) do {
                rs.updateString("tag", clan.getMeta().getTag());
                rs.updateString("name", clan.getMeta().getName());
                rs.updateString("description", clan.getMeta().getDescription());
                rs.updateLong("creationEpoch", clan.getMeta().getCreationTime());
                rs.updateString("leader", clan.getMeta().getLeader().toString());
                rs.updateBoolean("deleted", clan.isDeleted());
                rs.updateInt("level", clan.getLevelling().getLevel());
                rs.updateInt("exp", clan.getLevelling().getExperience());
                rs.updateRow();
                return true;
            } while (rs.next());
            else {
                ClansPlugin.log(Level.SEVERE, "Update fail: no such clan {0}", clan.toString());
                return false;
            }
        };
        boolean ret = false;
        try {
            ret = adapter.queryPrepared(UUID_QUERY_SQL, sql, cb);
        } catch (SQLException e){
            ClansPlugin.log(Level.SEVERE, "Update failed");
        }
        return ret;
    }
    // Don't use
    // TODO: Requires big refactor !!!
    @Override
    public boolean updateAll(Collection<Clan> clans) {
        throw new UnsupportedOperationException("Usupported for now");
        /*
        ClansPlugin.dbg("update all!");
        int amount = clans.size();
        if(amount < 1){
            ClansPlugin.log(Level.WARNING, "Requested update with empty clan collection");
            return;
        }
        StringBuilder sqlb = new StringBuilder("SELECT * FROM clans WHERE id IN(");
        for (int i = 0; i < amount; i++){
            sqlb.append("?, ");
        }
        sqlb.setLength(sqlb.length() - 2);
        sqlb.append(");");
        ClansPlugin.dbg("sql string: " + sqlb);
        HashMap<String, Clan> ids = new HashMap<>();
        SQLCallback<PreparedStatement> sql = ps -> {
            int i = 1;
            for(Clan c : clans){
                ps.setString(1, c.getId().toString());
                ids.put(c.getId().toString(), c);
                i++;
            }
            return true;
        };
        SQLCallback<ResultSet> cb = rs -> {
            if (rs.next())
                do {
                if (!ids.containsKey(rs.getString("id"))) {
                    ClansPlugin.log(Level.WARNING, "Clan {0} was not found in db for update");
                    continue;
                }
                Clan c = ids.get(rs.getString("id"));
                rs.updateString("tag", c.getMeta().getTag());
                rs.updateString("name", c.getMeta().getName());
                rs.updateString("description", c.getMeta().getDescription());
                rs.updateLong("creationEpoch", c.getMeta().getCreationTime());
                rs.updateString("leader", c.getMeta().getLeader().toString());
                rs.updateBoolean("deleted", c.isDeleted());
                rs.updateInt("level", c.getLevelling().getLevel());
                rs.updateInt("exp", c.getLevelling().getExperience());
                rs.updateRow();
            } while (rs.next());
            return true;
        };
        try {
            adapter.queryPrepared(sqlb.toString(), sql, cb);
        } catch (SQLException e) {
            ClansPlugin.log(Level.SEVERE, "Caught exception updating clan collection!!");
        }

         */
    }

    @Override
    public boolean insertClan(Clan clan, boolean replace) {
        ClansPlugin.dbg("clan {0} insert", clan.getId());
        SQLCallback<PreparedStatement> sql = ps -> {
            ps.setString(1, clan.getId().toString());
            ps.setString(2, clan.getMeta().getTag());
            ps.setString(3, clan.getMeta().getName());
            ps.setString(4, clan.getMeta().getDescription());
            ps.setLong(5, clan.getMeta().getCreationTime());
            ps.setString(6, clan.getMeta().getLeader().toString());
            ps.setBoolean(7, clan.isDeleted());
            ps.setInt(8, clan.getLevelling().getLevel());
            ps.setInt(9, clan.getLevelling().getExperience());
            return true;
        };
        try {
            int rows = adapter.updatePrepared(replace ? INSERT_SQL : REPLACE_SQL, sql);
            ClansPlugin.dbg("affected rows after insert: {0}", rows);
            return true;
        } catch (SQLException e) {
            ClansPlugin.log(Level.SEVERE, "Caught exception inserting clan " + clan.getId());
            return false;
        }
    }

    @Override
    public boolean insertAll(Collection<Clan> clans, boolean replace) {
        if(clans.isEmpty()){
            ClansPlugin.log(Level.WARNING, "Tried to insert empty clan collection");
            return false;
        }
        SQLCallback<PreparedStatement> sql = ps -> {
            for (Clan clan : clans) {
                ps.setString(1, clan.getId().toString());
                ps.setString(2, clan.getMeta().getTag());
                ps.setString(3, clan.getMeta().getName());
                ps.setString(4, clan.getMeta().getDescription());
                ps.setLong(5, clan.getMeta().getCreationTime());
                ps.setString(6, clan.getMeta().getLeader().toString());
                ps.setBoolean(7, clan.isDeleted());
                ps.setInt(8, clan.getLevelling().getLevel());
                ps.setInt(9, clan.getLevelling().getExperience());
                ps.addBatch();
            }
            return true;
        };
        try {
            adapter.updateBatched(replace ? REPLACE_SQL : INSERT_SQL, sql);
            return true;
        } catch (SQLException e) {
            ClansPlugin.log(Level.SEVERE, "Caught exception inserting clans!!");
            return false;
        }


    }
}
