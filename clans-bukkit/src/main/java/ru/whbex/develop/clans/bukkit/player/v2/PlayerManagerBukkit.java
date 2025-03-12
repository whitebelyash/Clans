package ru.whbex.develop.clans.bukkit.player.v2;

import ru.whbex.develop.clans.bukkit.player.ConsoleActorBukkit;
import ru.whbex.develop.clans.bukkit.player.PlayerActorBukkit;
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
    protected PlayerActor createActorObject(UUID uuid) {
        return new PlayerActorBukkit(uuid);
    }
}
