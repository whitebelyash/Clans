package ru.whbex.develop.bukkit.wrap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.player.PlayerTeleportEvent;
import ru.whbex.develop.common.ClansPlugin;
import ru.whbex.develop.common.cmd.CommandActor;
import ru.whbex.develop.common.misc.StringUtils;
import ru.whbex.develop.common.player.PlayerActor;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class PlayerActorBukkit implements PlayerActor, CommandActor {
    private final UUID id;
    private final OfflinePlayer offline;

    public PlayerActorBukkit(UUID id) {
        this.id = id;
        this.offline = Bukkit.getOfflinePlayer(id);

    }

    @Override
    public void sendMessage(String string) {
        if(!isOnline()) return;

        offline.getPlayer().sendMessage(string);
    }

    @Override
    public void sendMessage(String s, Object... args) {
        if(!isOnline()) return;

        offline.getPlayer().sendMessage(StringUtils.simpleformat(s, args));
    }

    @Override
    public void sendMessageT(String s) {
        if(!isOnline()) return;


        offline.getPlayer().sendMessage(ClansPlugin.Context.INSTANCE.plugin.getLanguage().getPhrase(s));
    }

    @Override
    public void sendMessageT(String s, Object... args) {
        if(!isOnline()) return;

        offline.getPlayer().sendMessage(StringUtils.simpleformat(ClansPlugin.Context.INSTANCE.plugin.getLanguage().getPhrase(s), args));
    }

    @Override
    public String getName() {
        return offline.getName();
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
    public OfflinePlayer getPlayer() {
        return offline.getPlayer();
    }

    @Override
    public String toString() {
        return "PlayerActorBukkit{" +
                "id=" + id +
                '}';
    }
}
