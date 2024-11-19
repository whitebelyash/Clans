package ru.whbex.develop.clans.common.player;

import ru.whbex.lib.string.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class PlayerProfile {
    private UUID owner;
    private String name;
    private final long regDate;
    private long lastSeen;

    /**
     * Player profile
     * @param owner Profile's owner
     * @param name Nickname
     * @param regDate Registration date (-1 - sets time at object creation)
     * @param lastSeen Last seen time (update with updateLastSeen())
     */
    public PlayerProfile(UUID owner, String name, long regDate, long lastSeen){
        this.owner = owner;
        this.name = name;
        this.regDate = regDate == -1 ? System.currentTimeMillis() : regDate;
        this.lastSeen = lastSeen;
    }
    public static PlayerProfile fromResultSet(ResultSet rs) throws SQLException {
        return new PlayerProfile(
                Objects.requireNonNull(StringUtils.UUIDFromString(rs.getString("id")), "uuid"),
                rs.getString("name"),
                rs.getLong("regDate"),
                rs.getLong("lastSeen"));
    }

    public UUID getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRegDate() {
        return regDate;
    }

    public long getLastSeen() {
        return lastSeen*1000L;
    }

    public void updateLastSeen() {
        this.lastSeen = System.currentTimeMillis() / 1000L;
    }
}
