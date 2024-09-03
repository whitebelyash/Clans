package ru.whbex.develop.common.clan.loader;

import org.bukkit.entity.Player;
import ru.whbex.develop.common.ClansPlugin;
import ru.whbex.develop.common.clan.Clan;
import ru.whbex.develop.common.clan.ClanLevelling;
import ru.whbex.develop.common.clan.ClanMeta;
import ru.whbex.develop.common.db.SQLAdapter;
import ru.whbex.develop.common.db.SQLCallback;
import ru.whbex.develop.common.misc.ClanUtils;
import ru.whbex.develop.common.misc.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.logging.Level;

// what the fuck im doing...
/* SQLAdapter bridge to ClanManager */
/* Anything here must be run in the same thread as the SQLAdapter. */
// TODO: IMPLEMENT PREPAREDSTATEMENTS !!!!!!!!!
// TODO: Fix shitty callback exception handling
public class SQLBridge implements Bridge {
    private final SQLAdapter adapter;

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
        final String sql = "SELECT * FROM clans WHERE tag='" + tag +"';";
        AtomicReference<Clan> clan = new AtomicReference<>(); // this will be redone after switching to a proper callback
        SQLCallback cb = rs -> {
                if(rs.next())
                    do {
                        UUID id;
                        if((id = StringUtils.UUIDFromString(rs.getString("id"))) == null){
                            ClansPlugin.log(Level.SEVERE, "Failed to load clan {0}: UUID is null!");
                            return false;
                        }
                        String name = rs.getString("name");
                        String description = rs.getString("description");
                     //   long time = rs.getLong("creationEpoch");
                        UUID lid;
                        if((lid = StringUtils.UUIDFromString(rs.getString("leader"))) == null){
                            ClansPlugin.log(Level.SEVERE, "Failed to load clan {0}: leader UUID is null!");
                            return false;
                        }
                        ClansPlugin.Context.INSTANCE.plugin.getPlayerActorOrRegister(lid).sendMessage("Clan load!"); // TODO: remove
                        int lvl = rs.getInt("level");
                        int exp = rs.getInt("exp");
                        ClanLevelling levelling = new ClanLevelling(lvl, exp);
                        ClanMeta meta = new ClanMeta(tag, name, description, lid, 0);
                        Clan c = new Clan(ClansPlugin.Context.INSTANCE.plugin.getClanManager(), id, meta, null, levelling);
                        if(!ClanUtils.validateClan(c)){
                            // TODO: specify why it was failed
                            ClansPlugin.log(Level.SEVERE, "Clan validation failed!");
                            c.setValidated(false);
                        }
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
           ret = adapter.query(sql, cb);
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
        final String sql = "SELECT * FROM clans WHERE id='" + id.toString() +"'";
        AtomicReference<Clan> clan = new AtomicReference<>();
        SQLCallback cb = rs -> {
                if(rs.next())
                    do {
                        String tag;
                        if((tag = rs.getString("tag")) == null){
                            ClansPlugin.log(Level.SEVERE, "Failed to load clan {0}: tag is null!");
                            return false;
                        }
                        String name = rs.getString("name");
                        String description = rs.getString("description");
                       // long time = rs.getLong("creationEpoch");
                        UUID lid;
                        if((lid = StringUtils.UUIDFromString(rs.getString("leader"))) == null){
                            ClansPlugin.log(Level.SEVERE, "Failed to load clan {0}: leader UUID is null!");
                            return false;
                        }
                        ClansPlugin.Context.INSTANCE.plugin.getPlayerActorOrRegister(lid).sendMessage("Clan loaded!"); // TODO: remove
                        int lvl = rs.getInt("level");
                        int exp = rs.getInt("exp");
                        ClanLevelling levelling = new ClanLevelling(lvl, exp);
                        ClanMeta meta = new ClanMeta(tag, name, description, lid, 0);
                        Clan c = new Clan(ClansPlugin.Context.INSTANCE.plugin.getClanManager(), id, meta, null, levelling);
                        if(!ClanUtils.validateClan(c)){
                            // TODO: specify why it was failed
                            ClansPlugin.log(Level.SEVERE, "Clan validation failed!");
                            c.setValidated(false);
                        }
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
            ret = adapter.query(sql, cb);
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
        final String sql = "SELECT * FROM clans WHERE tag='" + tag +"';";
        AtomicReference<UUID> uuid = new AtomicReference<>();
        SQLCallback cb = rs -> {
                if(rs.next()) do { uuid.set(StringUtils.UUIDFromString(rs.getString("id")));
                    } while(rs.next());
                return true;
        };
        try {
            adapter.query(sql, cb);
        } catch (SQLException e) {
            ClansPlugin.log(Level.SEVERE, "Failed to fetch UUID from tag {0}!", tag);
        }
        return uuid.get();
    }

    @Override
    public String fetchTagFromUUID(UUID id) {
        ClansPlugin.dbg("fetch uuid from uuid " + id);
        final String sql = "SELECT * FROM clans WHERE id='" + id +"';";
        AtomicReference<String> tag = new AtomicReference<>();
        SQLCallback cb = rs -> {
                if(rs.next()) do { tag.set(rs.getString("tag")); } while (rs.next());
                return true;
        };
        try {
            adapter.query(sql, cb);
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
        SQLCallback cb = rs -> {
            boolean ret = true;
                while(rs.next()){
                    String tag;
                    UUID id;
                    if((tag = rs.getString("tag")) == null){
                        ClansPlugin.log(Level.SEVERE, "Tag fetch failed: Invalid clan data on row " + rs.getRow());
                        continue;
                    }
                    if((id = StringUtils.UUIDFromString(rs.getString("uuid"))) == null){
                        ClansPlugin.log(Level.SEVERE, "UUID fetch failed: Invalid clan data on row " + rs.getRow());
                        continue;
                    }
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    boolean deleted = rs.getBoolean("deleted");
                  //  long time = rs.getLong("creationEpoch");
                    UUID lid;
                    if((lid = StringUtils.UUIDFromString(rs.getString("leader"))) == null){
                        ClansPlugin.log(Level.SEVERE, "Failed to load clan {0}: leader UUID is null!");
                        continue;
                    }
                    ClansPlugin.Context.INSTANCE.plugin.getPlayerActorOrRegister(lid).sendMessage("Clan loaded!"); // TODO: remove
                    int lvl = rs.getInt("level");
                    int exp = rs.getInt("exp");
                    ClanLevelling levelling = new ClanLevelling(lvl, exp);
                    ClanMeta meta = new ClanMeta(tag, name, description, lid, 0);
                    Clan c = new Clan(ClansPlugin.Context.INSTANCE.plugin.getClanManager(), id, meta, null, levelling);
                    c.setDeleted(deleted);
                    if(!ClanUtils.validateClan(c)){
                        // TODO: specify why it was failed
                        ClansPlugin.log(Level.SEVERE, "Clan {0} validation failed!", tag);
                        c.setValidated(false);
                        ret = false;
                    }
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
        final String sql = "SELECT * FROM clans WHERE uuid='" + clan.getId() + "';";
        SQLCallback cb = rs -> {
            try {
                if(rs.wasNull()) {
                    ClansPlugin.log(Level.SEVERE, "Clan " + clan.getId() + " was not found for update. " +
                            "Use SQLBridge#insertClan to insert a new clan");
                    return false;
                }
                rs.updateString("tag", clan.getMeta().getTag());
                rs.updateString("name", clan.getMeta().getName());
                rs.updateString("description", clan.getMeta().getDescription());
                rs.updateString("leader", clan.getMeta().getLeader().toString());
                rs.updateBoolean("deleted", clan.isDeleted());
                rs.updateInt("level", clan.getLevelling().getLevel());
                rs.updateInt("exp", clan.getLevelling().getExperience());
                rs.updateRow();
                return true;
            } catch (SQLException e){
                ClansPlugin.log(Level.SEVERE,  "Clan " + clan.getId() + " update failure!");
                return false;
            }
        };
        try {
            adapter.query(sql, cb);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;


    }
    // Don't use
    // TODO: Requires big refactor !!!
    @Override
    public void updateAll(Collection<Clan> clans) {
        ClansPlugin.dbg("update all!");
        clans.forEach(c -> {
            final String sql = "SELECT * FROM clans WHERE id='" + c.getId() + "';";
            SQLCallback cb = rs -> {
                try {
                    // TODO: shit fix this
                    if(rs.wasNull()){
                        ClansPlugin.log(Level.SEVERE, "Clan " + c.getId() + " was not found, use insertClans please!!");
                        return false;
                    }
                    rs.updateString("tag", c.getMeta().getTag());
                    rs.updateString("name", c.getMeta().getName());
                    rs.updateString("description", c.getMeta().getDescription());
                    rs.updateString("leader", c.getMeta().getLeader().toString());
                    rs.updateBoolean("deleted", c.isDeleted());
                    rs.updateInt("level", c.getLevelling().getLevel());
                    rs.updateInt("exp", c.getLevelling().getExperience());
                    rs.updateRow();
                } catch(SQLException e){
                    // fuck me
                    ClansPlugin.log(Level.SEVERE, "updateAll() failed!");
                    ClansPlugin.dbg_printStacktrace(e);
                    return false;
                }
                return true;
            };
            try {
                adapter.query(sql, cb);
            } catch (SQLException e) {
                ClansPlugin.log(Level.SEVERE, "Clan update failed for " + c.getId());
                ClansPlugin.dbg_printStacktrace(e);
            }
        });
    }

    @Override
    public void insertClan(Clan clan) {
        ClansPlugin.dbg("clan {0} insert", clan.getId());
        // preparedstatements...
        final String sql = "INSERT INTO clans VALUES(" +
                "'" + clan.getId() + "', " +
                "'" + clan.getMeta().getTag() + "', " +
                "'" + clan.getMeta().getName() + "', " +
                "'" + clan.getMeta().getDescription() + "', " +
                "'" + clan.getMeta().getLeader() + "', " +
                clan.isDeleted() + ", " +
                clan.getLevelling().getLevel() + ", " +
                clan.getLevelling().getExperience() + ");";
        try {
            int rows = adapter.update(sql);
            ClansPlugin.dbg("affected rows after insert: {0}", rows);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void insertAll(Collection<Clan> clans) {
        // Commented for now - TBD
        /*
        ClansPlugin.dbg("insert all!");
        StringBuilder sql = new StringBuilder("INSERT INTO clans VALUES");
        clans.forEach(c -> {
            String sq = "(" + c.getId() + ", " +
                    c.getMeta().getTag() + ", " +
                    c.getMeta().getName() + ", " +
                    c.getMeta().getDescription() + ", " +
                    c.getMeta().getLeader() + ", " +
                    c.isDeleted() + ", " +
                    c.getLevelling().getLevel() + ", " +
                    c.getLevelling().getExperience() + "), ";
            sql.append(sq);
        });

         */


    }
}
