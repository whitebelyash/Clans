package ru.whbex.develop.clans.common.misc;

import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.task.TaskScheduler;
import ru.whbex.develop.clans.common.clan.ClanManager;
import ru.whbex.develop.clans.common.player.v2.PlayerManager;
import ru.whbex.develop.clans.common.conf.Config;
import ru.whbex.lib.lang.Language;
import ru.whbex.lib.sql.SQLAdapter;

public class DisabledPlugin implements ClansPlugin {
    private static final String MESSAGE = "Plugin is disabled!";
    public static final DisabledPlugin INSTANCE = new DisabledPlugin();

    @Override
    public String _getName() {
        throw new NullPointerException(MESSAGE);

    }

    @Override
    public String _getDescription() {
        throw new NullPointerException(MESSAGE);
    }

    @Override
    public String _getVersionString() {
        throw new NullPointerException(MESSAGE);
    }

    @Override
    public PlayerManager getPlayerManager() {
        throw new NullPointerException(MESSAGE);
    }

    @Override
    public ClanManager getClanManager() {
        throw new NullPointerException(MESSAGE);

    }

    @Override
    public TaskScheduler getTaskScheduler() {
        throw new NullPointerException(MESSAGE);
    }

    @Override
    public Language getLanguage() {
        throw new NullPointerException(MESSAGE);

    }

    @Override
    public void reloadLangFiles() throws Exception {
        throw new NullPointerException(MESSAGE);

    }

    @Override
    public void reloadConfigs() throws Exception {
        throw new NullPointerException(MESSAGE);

    }

    @Override
    public Config getConfigWrapped() {
        throw new NullPointerException(MESSAGE);
    }
}
