package ru.whbex.develop.clans.common.misc;

import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.Constants;
import ru.whbex.develop.clans.common.clan.Clan;
import ru.whbex.develop.clans.common.clan.ClanLevelling;
import ru.whbex.develop.clans.common.clan.ClanMeta;
import ru.whbex.develop.clans.common.clan.ClanRank;
import ru.whbex.develop.clans.common.player.PlayerProfile;
import ru.whbex.lib.log.LogContext;
import ru.whbex.lib.string.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class SQLUtils {
    public static void clanToPrepStatement(PreparedStatement ps, Clan clan) throws SQLException {
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
    public static Clan clanFromQuery(ResultSet rs) throws SQLException {
        String cid_s = rs.getString("id");
        String tag = rs.getString("tag");
        String name = rs.getString("name");
        String description = rs.getString("description");
        String lid_s = rs.getString("leader");
        int rid = rs.getInt("defaultRank");
        long time = rs.getLong("creationEpoch");
        boolean deleted = rs.getBoolean("deleted");
        int lvl = rs.getInt("level");
        int exp = rs.getInt("exp");
        UUID cid, lid;
        ClanRank rank;
        if(tag == null || tag.isEmpty()){
            LogContext.log(Level.ERROR, "Null or empty tag! Cannot continue");
            return null;
        }
        if ((cid = StringUtils.UUIDFromString(cid_s)) == null) {
            LogContext.log(Level.ERROR, "Invalid leader UUID {0}! Cannot continue", lid_s);
            return null;
        }
        if ((lid = StringUtils.UUIDFromString(lid_s)) == null) {
            LogContext.log(Level.ERROR, "Invalid leader UUID {0}! Cannot continue", lid_s);
            return null;
        }
        if (rid < 0 || rid >= ClanRank.values().length) {
            LogContext.log(Level.ERROR, "Invalid default rank id {0}! Falling back to {1}" + Constants.DEFAULT_RANK, rid);
            rank = Constants.DEFAULT_RANK;
        } else rank = ClanRank.values()[rid];
        if(lvl < 0 || exp < 0){
            LogContext.log(Level.ERROR, "Invalid level/experience data {0}/{1}! Using default values", lvl, exp);
            lvl = 0; exp = 0;
        }

        ClanLevelling levelling = new ClanLevelling(lvl, exp);
        ClanMeta meta = new ClanMeta(tag, name, description, lid, time, rank);
        Clan c = new Clan(cid, meta, levelling, false);
        c.setDeleted(deleted);
        return c;
    }

    public static PlayerProfile profileFromQuery(ResultSet rs) throws SQLException {
        return new PlayerProfile(
                Objects.requireNonNull(StringUtils.UUIDFromString(rs.getString("id")), "uuid"),
                rs.getString("name"),
                rs.getLong("regDate"),
                rs.getLong("lastSeen"),
                Objects.requireNonNull(StringUtils.UUIDFromString(rs.getString("id")), "cid"));
    }
    public static void profileToPrepStatement(PreparedStatement stat, PlayerProfile prof) throws SQLException {
        stat.setString(1, prof.getOwner().toString());
        stat.setString(2, prof.getName());
        stat.setLong(3, prof.getRegDate());
        stat.setLong(4, prof.getLastSeen());
        stat.setString(5, prof.getClanId().toString());
    }
}
