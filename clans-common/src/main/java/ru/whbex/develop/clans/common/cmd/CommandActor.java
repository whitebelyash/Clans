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

}
