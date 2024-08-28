package ru.whbex.develop.common.misc;

import ru.whbex.develop.common.ClansPlugin;
import ru.whbex.develop.common.clan.ClanManager;
import ru.whbex.develop.common.db.SQLAdapter;
import ru.whbex.develop.common.lang.Language;
import ru.whbex.develop.common.wrap.ConfigWrapper;
import ru.whbex.develop.common.wrap.ConsoleActor;
import ru.whbex.develop.common.player.PlayerActor;
import ru.whbex.develop.common.wrap.Task;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
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
    public Collection<PlayerActor> getOnlineActors() {
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
