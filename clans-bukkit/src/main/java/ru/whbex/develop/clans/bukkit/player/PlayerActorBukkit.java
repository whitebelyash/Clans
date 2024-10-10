package ru.whbex.develop.clans.bukkit.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.clan.Clan;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.lang.Language;
import ru.whbex.develop.clans.common.misc.StringUtils;
import ru.whbex.develop.clans.common.misc.requests.Request;
import ru.whbex.develop.clans.common.player.PlayerActor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PlayerActorBukkit implements PlayerActor, CommandActor {
    private final UUID id;
    private final OfflinePlayer offline;
    private final Map<PlayerActor, Request> requests = new HashMap<>();

    public PlayerActorBukkit(UUID id) {
        this.id = id;
        this.offline = Bukkit.getOfflinePlayer(id);

    }

    @Override
    public void sendMessage(String string) {
        if(!isOnline()) return;
        string = ChatColor.translateAlternateColorCodes('&', string);
        offline.getPlayer().sendMessage(string);
    }

    @Override
    public void sendMessage(String s, Object... args) {
        if(!isOnline()) return;

        this.sendMessage(StringUtils.simpleformat(s, args));
    }

    @Override
    public void sendMessageT(String s) {
        if(!isOnline()) return;

        this.sendMessage(ClansPlugin.Context.INSTANCE.plugin.getLanguage().getPhrase(s));
    }

    @Override
    public void sendMessageT(String s, Object... args) {
        if(!isOnline()) return;

        this.sendMessage(StringUtils.simpleformat(ClansPlugin.Context.INSTANCE.plugin.getLanguage().getPhrase(s), args));
    }

    @Override
    public String getName() {
        return offline.getName();
    }

    @Override
    public Language getLanguage() {
        // TODO: Implement per-player locale support
        return ClansPlugin.Context.INSTANCE.plugin.getLanguage();
    }

    @Override
    public Clan getClan() {
        return null;
    }

    @Override
    public boolean hasClan() {
        return false;
    }

    @Override
    public void addRequest(Request request) {
        if(request.recipient() != this){
            // TODO: Remove this branch or log to WARNING level
            ClansPlugin.dbg("!!! Got invalid request " + request);
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
        return offline.isOnline();
    }

    @Override
    public UUID getUniqueId() {
        return id;
    }

    @Override
    public void teleport(int x, int y, int z, String world) {
        if(offline.isOnline())
            offline.getPlayer().teleport(new Location(Objects.requireNonNull(Bukkit.getWorld(world), "world"), x, y, z), PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    public OfflinePlayer getOfflinePlayer() {
        return offline;
    }

    @Nullable
    public Player getPlayer() {
        return offline.getPlayer();
    }

    @Override
    public String toString() {
        return "PlayerActorBukkit{" +
                "id=" + id +
                "online="+ isOnline() +
                '}';
    }
}
