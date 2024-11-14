package ru.whbex.develop.clans.common;

import org.slf4j.event.Level;
import ru.whbex.lib.log.Debug;
import ru.whbex.lib.log.LogContext;
import ru.whbex.lib.sql.SQLAdapter;
import ru.whbex.lib.sql.conn.ConnectionProvider;

import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Consumer;

public class DatabaseService {
    private static ConnectionProvider provider;
    public static void initializeService(ConnectionProvider provider) throws IllegalAccessException {
        Debug.print("Initializing DatabaseService...");
        if(DatabaseService.provider != null)
            throw new IllegalAccessException("Tried to initialize DatabaseService again");
        DatabaseService.provider = provider;

    }
    public static void destroyService(){
        Debug.print("Destroying DatabaseService...");
        if (provider != null) {
            try {
                provider.breakConnection();
            } catch (SQLException e) {
                LogContext.log(Level.ERROR, "Failed to disconnect from database, ignoring");
            }
            provider = null;
        }
        Debug.print("bye!");
    }
    public static boolean isInitialized(){
        return DatabaseService.provider != null;
    }

    public static <T> SQLAdapter<T>.Executor<T> getExecutor(Class<T> returnType, Consumer<SQLAdapter<T>> task){
        return SQLAdapter.executor(returnType, provider, task);
    }
    public static SQLAdapter<Void>.Executor<Void> getExecutor(Consumer<SQLAdapter<Void>> task){
        return SQLAdapter.executor(provider, task);
    }
}
