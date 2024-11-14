package ru.whbex.develop.clans.common.clan;


import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.lib.log.Debug;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClanMeta {
    private String tag;
    private String name;
    private String description;
    private UUID leader;
    private final long creationTime;
    private final ClanRank defaultRank;

    /* Settings */
    private boolean viewable = true;
    private boolean joinable = false;
    private boolean friendlyFire = true;


    // Stored in runtime
    private final Map<String, Object> data = new HashMap<>();

    public ClanMeta(String tag, String name, String description, UUID leader, long creationTime, ClanRank defRank){
        this.tag = tag;
        this.name = name;
        this.description = description;
        this.leader = leader;
        this.creationTime = creationTime;
        this.defaultRank = defRank;
        Debug.print("meta created for " + tag);
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public String getDescription() {
        return description;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getLeader() {
        return leader;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setData(String key, Object obj){
        data.put(key, obj);
    }
    public boolean checkData(String key){
        return data.containsValue(key);
    }
    public Object getData(String key, Object obj){
        if(!checkData(key))
            Debug.print("data has no key {0}. returning null", key);
        return data.get(key);
    }

    public ClanRank getDefaultRank() {
        return defaultRank;
    }



    public boolean isViewable() {
        return viewable;
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public boolean isJoinable() {
        return joinable;
    }

    public void setViewable(boolean viewable) {
        this.viewable = viewable;
    }

    public void setJoinable(boolean joinable) {
        this.joinable = joinable;
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }
}
