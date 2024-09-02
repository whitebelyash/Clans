package ru.whbex.develop.common;

import ru.whbex.develop.common.clan.ClanManager;
import ru.whbex.develop.common.db.SQLAdapter;
import ru.whbex.develop.common.lang.Language;
import ru.whbex.develop.common.misc.DisabledPlugin;
import ru.whbex.develop.common.misc.StringUtils;
import ru.whbex.develop.common.wrap.ConfigWrapper;
import ru.whbex.develop.common.wrap.ConsoleActor;
import ru.whbex.develop.common.player.PlayerActor;
import ru.whbex.develop.common.wrap.Task;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;


// Poorly written clans plugin
// Goal - copy VanillaCraft clans functionality with some additions (because original is private & proprietary)
public interface ClansPlugin {
    public enum Context {
        INSTANCE;

        public ClansPlugin plugin = new DisabledPlugin();
        public Logger logger;
        public static final boolean DEBUG = true;
        public void setContext(ClansPlugin plugin){
            this.plugin = plugin;
        }
        public void setLogger(Logger log){
            this.logger = log;
        }
    }
    Logger getLogger();
    ConsoleActor getConsoleActor();
    PlayerActor getPlayerActor(UUID id);
    PlayerActor getPlayerActor(String name);
    PlayerActor getPlayerActorOrRegister(UUID id);
    Collection<PlayerActor> getOnlineActors();
    ClanManager getClanManager();
    Language getLanguage();
    SQLAdapter getSQLAdapter();

    Task run(Runnable task);
    Task runLater(long delay, Runnable task);
    Task runAsync(Runnable task);
    Task runAsyncLater(long delay, Runnable task);
    <T> Future<T> runCallable(Callable<T> callable);
    ExecutorService getDatabaseExecutor();

    void reloadLocales() throws Exception;
    void reloadConfigs() throws Exception;
    ConfigWrapper getConfigWrapped();



    static void dbg(String m, Object... args){
        if(!Context.DEBUG)
            return;
        Context.INSTANCE.logger.info(StringUtils.simpleformat("DBG({0}): {1}",
                Thread.currentThread().getStackTrace()[2].getFileName() +
                ':' +
                Thread.currentThread().getStackTrace()[2].getLineNumber(),
                StringUtils.simpleformat(m, args)));
    }
    static void dbg_printStacktrace(Throwable t){
        if(Context.DEBUG)
            t.printStackTrace();
    }

    static void log(Level level, String message){
        if(Context.INSTANCE.logger == null) return;
        Context.INSTANCE.logger.log(level, message);
    }
    static void log(Level level, String message, Object... args){
        if(Context.INSTANCE.logger == null) return;
        Context.INSTANCE.logger.log(level, message, args);

    }
}
