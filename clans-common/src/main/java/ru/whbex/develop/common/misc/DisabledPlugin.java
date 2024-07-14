package ru.whbex.develop.common.misc;

import ru.whbex.develop.common.ClansPlugin;
import ru.whbex.develop.common.clan.ClanManager;
import ru.whbex.develop.common.lang.Language;
import ru.whbex.develop.common.wrap.ConsoleActor;
import ru.whbex.develop.common.player.PlayerActor;

import java.util.UUID;
import java.util.logging.Logger;

public class DisabledPlugin implements ClansPlugin {
    private static final String MESSAGE = "Plugin is disabled!";
    @Override
    public Logger getLogger() {
        throw new NullPointerException(MESSAGE);
    }

    @Override
    public ConsoleActor getConsoleActor() {
        throw new NullPointerException(MESSAGE);
    }

    @Override
    public PlayerActor getPlayerActor(UUID id) {
        throw new NullPointerException(MESSAGE);
    }

    @Override
    public PlayerActor getPlayerActor(String name) {
        throw new NullPointerException(MESSAGE);
    }

    @Override
    public ClanManager getClanManager() {
        throw new NullPointerException(MESSAGE);

    }

    @Override
    public Language getLanguage() {
        throw new NullPointerException(MESSAGE);

    }

    @Override
    public void reloadLocales() throws Exception {
        throw new NullPointerException(MESSAGE);

    }

    @Override
    public void reloadConfigs() throws Exception {
        throw new NullPointerException(MESSAGE);

    }
}
