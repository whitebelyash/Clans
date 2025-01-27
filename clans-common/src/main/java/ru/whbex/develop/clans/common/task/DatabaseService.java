package ru.whbex.develop.clans.common.task;

import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.conf.Config;
import ru.whbex.lib.log.Debug;
import ru.whbex.lib.log.LogContext;
import ru.whbex.lib.sql.SQLAdapter;
import ru.whbex.lib.sql.conn.ConnectionProvider;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DatabaseService {
    private static ConnectionProvider provider;
    private static ExecutorService pool;

    public static void initializeService(ConnectionProvider provider) throws IllegalAccessException {
        Debug.print("Initializing DatabaseService...");
        if (DatabaseService.provider != null)
            throw new IllegalAccessException("Tried to initialize DatabaseService again");
        DatabaseService.provider = provider;
        try {
            provider.newConnection();
        } catch (SQLException e) {
            LogContext.log(Level.ERROR, "Cannot initialize database connection, {0}", e.getMessage());
            Debug.dbg_printStacktrace(e);
            return;
        }
        LogContext.log(Level.INFO, "Starting database executor pool");
        pool = Executors.newSingleThreadExecutor();
    }

    public static void destroyService() {
        Debug.print("Destroying DatabaseService...");
        if (provider != null) {
            try {
                provider.breakConnection();
            } catch (SQLException e) {
                // This is right, yes?
                LogContext.log(Level.ERROR, "Failed to disconnect from the database, ignoring");
            }
            provider = null;
        }
        LogContext.log(Level.INFO, "Closing database executor pool...");
        if (!pool.isShutdown())
            pool.shutdown();
        if (!pool.isTerminated()) {
            LogContext.log(Level.INFO, "Waiting for tasks to terminate...");
            try {
                if (!pool.awaitTermination(5, TimeUnit.SECONDS)) pool.shutdownNow();
            } catch (InterruptedException ignored) {
            }
        }
    }
    public static boolean isInitialized(){
        return DatabaseService.provider != null;
    }

    public static Config.DatabaseType getDatabaseBackend(){
        return ClansPlugin.Context.INSTANCE.plugin.getConfigWrapped().getDatabaseBackend();
    }

    public static <T> SQLAdapter<T>.Executor<T> getExecutor(Class<T> returnType, Consumer<SQLAdapter<T>> task){
        return SQLAdapter.executor(returnType, provider, task);
    }
    public static SQLAdapter<Void>.Executor<Void> getExecutor(Consumer<SQLAdapter<Void>> task){
        return SQLAdapter.executor(provider, task);
    }
    // TODO: Implement multithreaded connection pooling - or not?
    // Do we actually need multiple database workers?
    public static <T> SQLAdapter<T>.Executor<T> getAsyncExecutor(Class<T> returnType, Consumer<SQLAdapter<T>> task){
        return SQLAdapter.executor(returnType, provider, task).executorService(pool);
    }
    public static SQLAdapter<Void>.Executor<Void> getAsyncExecutor(Consumer<SQLAdapter<Void>> task){
        return SQLAdapter.executor(provider, task).executorService(pool);
    }
}
