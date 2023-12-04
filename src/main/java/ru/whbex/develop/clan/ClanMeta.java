package ru.whbex.develop.clan;


import java.util.UUID;

public class ClanMeta {
    private String tag;
    private String name;
    private String description;
    private UUID leader;
    private boolean disband;
    private final long creationTime;

    public ClanMeta(String tag, String name, String description, UUID leader, long creationTime){
        this.tag = tag;
        this.name = name;
        this.description = description;
        this.leader = leader;
        this.creationTime = creationTime;
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
}
