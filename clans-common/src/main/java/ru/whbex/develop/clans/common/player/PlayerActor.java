package ru.whbex.develop.clans.common.player;

import ru.whbex.develop.clans.common.lang.Language;
import ru.whbex.develop.clans.common.misc.requests.Request;

import java.util.UUID;

public interface PlayerActor {

    void sendMessage(String string);
    boolean isOnline();
    void teleport(int x, int y, int z, String world);
    UUID getUniqueId();
    String getName();
    Language getLanguage();

    // REQUESTS

    void addRequest(Request request);
    void removeRequest(Request request);
    void removeRequest(PlayerActor sender);
    boolean hasRequestFrom(PlayerActor sender);
    boolean hasRequest(Request request);
    Request getRequest(PlayerActor sender);
}
