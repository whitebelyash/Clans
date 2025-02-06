package ru.whbex.develop.clans.common.cmd;

import java.util.UUID;

public interface CommandActor {
    void sendMessage(String s);
    void sendMessage(String s, Object... args);
    void sendMessageT(String s);
    void sendMessageT(String s, Object... args);
    boolean isPlayer();
    String getName();
    UUID getUniqueId();

    // Data (Runtime)
    void setData(String key, Object data);
    Object getData(String key);
    boolean hasData(String key);
    void removeData(String key);
    void removeDataAll();
}
