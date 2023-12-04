package ru.whbex.develop.player;

public interface CommandPerformer {
    void sendMessage(String s);
    void sendMessage(String s, String... args);
    boolean isOnline();
    boolean isPlayer();

    String getName();

}
