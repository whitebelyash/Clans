package ru.whbex.develop.player;

import ru.whbex.develop.lang.LocaleString;

public interface CommandPerformer {

    void sendMessage(LocaleString s);
    void sendMessage(LocaleString s, String... args);
    void sendMessage(LocaleString s, LocaleString... args);
    void sendMessage(String s);
    void sendMessage(String s, String... args);
    boolean isOnline();
    boolean isPlayer();

    String getName();

}
