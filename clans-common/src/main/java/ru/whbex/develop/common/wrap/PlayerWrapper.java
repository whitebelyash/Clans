package ru.whbex.develop.common.wrap;

import java.util.UUID;

public interface PlayerWrapper {

    void sendMessage(UUID receiver, String string);
    void sendMessageColorized(UUID receiver, String string);
    boolean isOnline(UUID target);
    void teleport(UUID target, int x, int y, int z, String world);
}
