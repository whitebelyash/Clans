package ru.whbex.develop.clans.bukkit.player.v2;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.whbex.develop.clans.bukkit.player.ConsoleActorBukkit;
import ru.whbex.develop.clans.bukkit.player.PlayerActorBukkit;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.player.ConsoleActor;
import ru.whbex.develop.clans.common.player.PlayerActor;
import ru.whbex.develop.clans.common.player.v2.PlayerManager;

import java.util.UUID;

public class PlayerManagerBukkit extends PlayerManager {
    private final ConsoleActor ca = new ConsoleActorBukkit();
    @Override
    public ConsoleActor consoleActor() {
        return ca;
    }

    @Override
    public PlayerActor createActorObject(UUID uuid) {
        return new PlayerActorBukkit(uuid);
    }
    public CommandActor asCommandActor(CommandSender sender){
        return sender instanceof Player ? (CommandActor)this.loadPlayerActor(((Player) sender).getUniqueId()) : (CommandActor) ca;
    }
}
