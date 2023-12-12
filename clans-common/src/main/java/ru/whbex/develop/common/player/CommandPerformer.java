package ru.whbex.develop.common.player;

public interface CommandPerformer {
    void sendMessage(String s);
    void sendMessage(String s, Object... args);
    void sendMessageT(String path);
    void sendMessageT(String path, Object... args);
    boolean isOnline();
    boolean isPlayer();

    String getName();

}
