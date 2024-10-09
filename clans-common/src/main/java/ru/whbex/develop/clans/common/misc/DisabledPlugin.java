package ru.whbex.develop.clans.common.misc;

import ru.whbex.develop.clans.common.TaskScheduler;
import ru.whbex.develop.clans.common.player.PlayerActor;
import ru.whbex.develop.clans.common.player.PlayerManager;
import ru.whbex.develop.clans.common.wrap.ConsoleActor;
import ru.whbex.develop.clans.common.wrap.Task;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.clan.ClanManager;
import ru.whbex.develop.clans.common.db.SQLAdapter;
import ru.whbex.develop.clans.common.lang.Language;
import ru.whbex.develop.clans.common.wrap.ConfigWrapper;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.slf4j.Logger;

public class DisabledPlugin implements ClansPlugin {
    private static final String MESSAGE = "Plugin is disabled!";

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
    public SQLAdapter getSQLAdapter() {
        throw new NullPointerException(MESSAGE);
    }
    @Override
    public <T extends SQLAdapter> T newSQLAdapter(Class<T> clazz){
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
    public ConfigWrapper getConfigWrapped() {
        throw new NullPointerException(MESSAGE);
    }
}
