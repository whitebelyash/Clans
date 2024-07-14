package ru.whbex.develop.common.player;

import ru.whbex.develop.common.cmd.CommandActor;

import java.util.UUID;

public interface PlayerActor {

    void sendMessage(String string);
    boolean isOnline();
    void teleport(int x, int y, int z, String world);
    UUID getUniqueId();
    String getName();
}
