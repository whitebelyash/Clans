package ru.whbex.develop.clans.common;

import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.clan.ClanManager;
import ru.whbex.develop.clans.common.db.H2SQLAdapter;
import ru.whbex.develop.clans.common.db.SQLAdapter;
import ru.whbex.develop.clans.common.lang.Language;
import ru.whbex.develop.clans.common.misc.DisabledPlugin;
import ru.whbex.develop.clans.common.misc.StringUtils;
import ru.whbex.develop.clans.common.player.PlayerActor;
import ru.whbex.develop.clans.common.player.PlayerManager;
import ru.whbex.develop.clans.common.wrap.ConfigWrapper;
import ru.whbex.develop.clans.common.wrap.ConsoleActor;
import ru.whbex.develop.clans.common.wrap.Task;

import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;

// Poorly written clans plugin
// Goal - copy VanillaCraft clans functionality with some additions (because original is private & proprietary)
public interface ClansPlugin {
    public enum Context {
        INSTANCE;

        public ClansPlugin plugin = new DisabledPlugin();
        public Logger logger;
        public java.util.logging.Logger jlogger;
        public static final boolean DEBUG = true;
        public static final String NAME = "clans";
        public void setContext(ClansPlugin plugin){
            this.plugin = plugin;
        }
        public void setLogger(Logger log){
            this.logger = log;
        }
        public void setJavaLogger(java.util.logging.Logger log){
            this.jlogger = log;
        }
    }

    String _getName();
    String _getDescription();
    String _getVersionString();

    PlayerManager getPlayerManager();
    ClanManager getClanManager();
    // returns system language
    Language getLanguage();
    SQLAdapter getSQLAdapter();

    <T extends SQLAdapter> T newSQLAdapter(Class<T> clazz); // this will init new adapter with ConnectionData from main config

    /* Task management */
    // TODO: Move to TaskManager/TaskScheduler/TaskDispatcher interface?
    Task run(Runnable task);
    Task runLater(long delay, Runnable task);
    Task runAsync(Runnable task);
    Task runAsyncLater(long delay, Runnable task);
    <T> Future<T> runCallable(Callable<T> callable);
    ExecutorService getDatabaseExecutor();


    void reloadLangFiles() throws Exception;
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
        switch(level){
            case INFO -> Context.INSTANCE.logger.info(message);
            case WARN -> Context.INSTANCE.logger.warn(message);
            case ERROR -> Context.INSTANCE.logger.error(message);
            case DEBUG -> Context.INSTANCE.logger.debug(message);
            case TRACE -> Context.INSTANCE.logger.trace(message);
        }
    }
    static void log(Level level, String message, Object... args){
        if(Context.INSTANCE.logger == null) return;
        switch(level){
            case INFO -> Context.INSTANCE.logger.info(StringUtils.simpleformat(message, args));
            case WARN -> Context.INSTANCE.logger.warn(StringUtils.simpleformat(message, args));
            case ERROR -> Context.INSTANCE.logger.error(StringUtils.simpleformat(message, args));
            case DEBUG -> Context.INSTANCE.logger.debug(StringUtils.simpleformat(message, args));
            case TRACE -> Context.INSTANCE.logger.trace(StringUtils.simpleformat(message, args));
        }
    }
}
