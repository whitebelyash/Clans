package ru.whbex.develop.clans.common.player;

import ru.whbex.develop.clans.common.clan.Clan;
import ru.whbex.develop.clans.common.misc.Messenger;
import ru.whbex.develop.clans.common.misc.requests.Request;
import ru.whbex.lib.lang.Language;

import java.util.UUID;
import java.util.concurrent.Future;

public interface PlayerActor extends Messenger {
    
    boolean isOnline();
    void teleport(int x, int y, int z, String world);
    UUID getUniqueId();
    String getName();
    String getOnlineName();
    Language getLanguage();
    PlayerProfile getProfile();
    void setProfile(PlayerProfile profile);

    Clan getClan();
    boolean hasClan();

    // REQUESTS
    void addRequest(Request request);
    void removeRequest(Request request);
    void removeRequest(PlayerActor sender);
    boolean hasRequestFrom(PlayerActor sender);
    boolean hasRequest(Request request);
    Request getRequest(PlayerActor sender);

    // Data (Runtime)
    void setData(String key, Object data);
    Object getData(String key);
    boolean hasData(String key);
    void removeData(String key);
    void removeDataAll();

    // Fetcher (used for PlayerProfile fetching)
    void bindFetcher(Future<?> future);
    Future<?> getFetcher();
    void unbindFetcher();
}
