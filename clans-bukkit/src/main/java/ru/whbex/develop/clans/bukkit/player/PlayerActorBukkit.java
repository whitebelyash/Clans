package ru.whbex.develop.clans.bukkit.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.clan.Clan;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.misc.requests.Request;
import ru.whbex.develop.clans.common.player.PlayerActor;
import ru.whbex.develop.clans.common.player.PlayerProfile;
import ru.whbex.lib.lang.Language;
import ru.whbex.lib.log.LogContext;
import ru.whbex.lib.log.Debug;
import ru.whbex.lib.sql.SQLAdapter;
import ru.whbex.lib.sql.SQLCallback;
import ru.whbex.lib.string.StringUtils;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Future;

public class PlayerActorBukkit implements PlayerActor, CommandActor {
    private UUID id;
    private String name;
    private Future<Void> fetch;
    private PlayerProfile profile;
    private final Map<PlayerActor, Request> requests = new HashMap<>();

    private final void initPlayer(Player player){
        this.id = player.getUniqueId();
        this.name = player.getName();
    }

    /* Create PlayerActor from online player */
    public PlayerActorBukkit(Player player) {
        if(!player.isOnline())
            throw new UnsupportedOperationException("Creating player from offline Player object is not supported");
        this.initPlayer(player);
    }
    /* Create PlayerActor from offline player */
    public PlayerActorBukkit(UUID id){
        if(Bukkit.getPlayer(id) != null){
            this.initPlayer(Bukkit.getPlayer(id));
            return;
        }
        this.id = id;
    }


    private void sendMsg(String s){
        s = ChatColor.translateAlternateColorCodes('&', s);
        Bukkit.getPlayer(id).sendMessage(s);
    }
    @Override
    public void sendMessage(String string) {
        if(!isOnline()) return;
        sendMsg(string);

    }

    @Override
    public void sendMessage(String s, Object... args) {
        if(!isOnline()) return;
        this.sendMsg(StringUtils.simpleformat(s, args));
    }

    @Override
    public void sendMessageT(String s) {
        if(!isOnline()) return;

        this.sendMsg(ClansPlugin.Context.INSTANCE.plugin.getLanguage().getPhrase(s));
    }

    @Override
    public void sendMessageT(String s, Object... args) {
        if(!isOnline()) return;
        this.sendMsg(StringUtils.simpleformat(ClansPlugin.Context.INSTANCE.plugin.getLanguage().getPhrase(s), args));
    }

    @Override
    public String getName() {
        return profile != null ? profile.getName() : null;
    }

    @Override
    public Language getLanguage() {
        // TODO: Implement per-player locale support
        return ClansPlugin.Context.INSTANCE.plugin.getLanguage();
    }

    @Override
    public PlayerProfile getProfile() {
        return profile;
    }

    @Override
    public void setProfile(PlayerProfile profile) {
        this.profile = profile;
    }

    // TODO: implement this, stubbing to fix compiling
    @Override
    public Clan getClan() {
        return null;
    }

    @Override
    public boolean hasClan() {
     //   return ClansPlugin.Context.INSTANCE.plugin.getClanManager().getMemberManager().hasMember()
        return false;
    }

    @Override
    public void addRequest(Request request) {
        if(request.recipient() != this){
            // TODO: Remove this branch or log to WARNING level
            Debug.print("!!! Got invalid request " + request);
            return;
        }
        requests.put(request.sender(), request);

    }

    @Override
    public void removeRequest(Request request) {
        requests.remove(request.sender(), request);
    }

    @Override
    public void removeRequest(PlayerActor sender) {
        requests.remove(sender);

    }

    @Override
    public boolean hasRequestFrom(PlayerActor sender) {
        return requests.containsKey(sender);
    }

    @Override
    public boolean hasRequest(Request request) {
        return requests.containsValue(request);
    }

    @Override
    public Request getRequest(PlayerActor sender) {
        return requests.get(sender);
    }


    @Override
    public boolean isOnline() {
        return Bukkit.getPlayer(id) != null;
     }

    @Override
    public UUID getUniqueId() {
        return id;
    }

    @Override
    public void teleport(int x, int y, int z, String world) {
        if(isOnline())
            Bukkit.getPlayer(id).teleport(new Location(Objects.requireNonNull(Bukkit.getWorld(world), "world"), x, y, z), PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(id);
    }


    @Override
    public String toString() {
        return "PlayerActorBukkit{" +
                "id=" + id +
                "online="+ isOnline() +
                '}';
    }

    public void setName(String name){
        this.name = name;
    }
    public void setFetcher(Future<Void> fetcher){
        this.fetch = fetch;
    }

    public Future<Void> getFetcher() {
        return fetch;
    }
}
