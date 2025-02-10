package ru.whbex.develop.clans.common.cmd;

import ru.whbex.develop.clans.common.misc.Messenger;

import java.util.UUID;

public interface CommandActor extends Messenger {

    boolean isPlayer();
    String getName();
    UUID getUniqueId();

    default boolean equals(CommandActor another){
        return another.getUniqueId().equals(getUniqueId());
    }

    // Data (Runtime)
    void setData(String key, Object data);
    Object getData(String key);
    boolean hasData(String key);
    void removeData(String key);
    void removeDataAll();
}
