package ru.whbex.develop.clans.common.misc;

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
    public PlayerManager getPlayerManager() {
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
    public SQLAdapter getSQLAdapter() {
        throw new NullPointerException(MESSAGE);
    }
    @Override
    public <T extends SQLAdapter> T newSQLAdapter(Class<T> clazz){
        throw new NullPointerException(MESSAGE);
    }

    @Override
    public Task run(Runnable task) {
        throw new NullPointerException(MESSAGE);
    }

    @Override
    public Task runLater(long delay, Runnable task) {
        throw new NullPointerException(MESSAGE);
    }

    @Override
    public Task runAsync(Runnable task) {
        throw new NullPointerException(MESSAGE);
    }

    @Override
    public Task runAsyncLater(long delay, Runnable task) {
        throw new NullPointerException(MESSAGE);
    }

    @Override
    public <T> Future<T> runCallable(Callable<T> callable) {
        throw new NullPointerException(MESSAGE);
    }

    @Override
    public ExecutorService getDatabaseExecutor() {
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

    @Override
    public ConfigWrapper getConfigWrapped() {
        throw new NullPointerException(MESSAGE);
    }
}
