package ru.whbex.develop.clans.common.player;

import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.lib.log.LogContext;

import java.util.UUID;

public class PlayerProfile {
    private final UUID owner;
    private String name;
    private final long regDate;
    private long lastSeen;

    private UUID clanId;



    /**
     * Player profile
     * @param owner Profile's owner
     * @param name Nickname
     * @param regDate Registration date (-1 - sets time at object creation)
     * @param lastSeen Last seen time (update with updateLastSeen())
     */
    public PlayerProfile(UUID owner, String name, long regDate, long lastSeen, UUID cid){
        this.owner = owner;
        this.name = name;
        this.regDate = regDate == -1 ? System.currentTimeMillis() : regDate;
        this.lastSeen = lastSeen;
        if(cid != null && !ClansPlugin.clanManager().clanExists(cid)){
            LogContext.log(Level.WARN, "Loaded PlayerProfile with unknown clan id {0}, WTF?? Removing...", cid);
            cid = null;
        }
        this.clanId = cid;
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

    public void setClanId(UUID clanId) {
        this.clanId = clanId;
    }

    public UUID getClanId() {
        return clanId;
    }
}
