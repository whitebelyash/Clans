package ru.whbex.develop.clans.bukkit.cmd;

import org.bukkit.command.CommandSender;
import ru.whbex.develop.clans.bukkit.player.v2.PlayerManagerBukkit;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.cmd.CommandActor;

// util
class StaticActorProxy {
    static CommandActor asActor(CommandSender performer) {
        return ((PlayerManagerBukkit) ClansPlugin.Context.INSTANCE.plugin.getPlayerManager()).asCommandActor(performer);
    }
}
