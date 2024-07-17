package ru.whbex.develop.common.db;

import org.bukkit.Bukkit;
import ru.whbex.develop.common.ClansPlugin;

import java.sql.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;

public abstract class SQLAdapter {
    private final String path;
    private Connection con = null;

    public SQLAdapter(String driver, String url) throws ClassNotFoundException {
        Class.forName(driver);
        this.path = url;
    }


    public final boolean isConnected() throws SQLException {
        return con != null && !con.isClosed();
    }
    public final boolean isConnectedChecked(){
        try {
            return con != null && !con.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public final void connect() throws SQLException {
        ClansPlugin.log(Level.INFO, "Connecting to the database...");
        con = DriverManager.getConnection(path);
        ClansPlugin.Context.INSTANCE.logger.info("Connected");
    }
    public final Future<Void> connectAsynchronously(ExecutorService executor){
        ClansPlugin.log(Level.INFO, "Connecting to the database");
        Callable<Void> connector = () -> {
            con = DriverManager.getConnection(path);
            return null;
        };
        return executor.submit(connector);
    }
    public final void disconnect() throws SQLException {
        if(isConnected()) {
            ClansPlugin.log(Level.INFO, "Disconnecting from the database...");
            con.close();
            ClansPlugin.log(Level.INFO, "Disconnected");
        }
    }
    public final void query(String sql, Consumer<ResultSet> callback) throws SQLException {
        if (!isConnected())
            throw new IllegalStateException("Database is not connected!");
        try (
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            callback.accept(rs);
        } catch (SQLException e) {
            // TODO: better message
            ClansPlugin.log(Level.SEVERE, "SQL Failure: " + e.getLocalizedMessage() + " !!!");
            ClansPlugin.dbg("SQLState: {0}", e.getSQLState());
            ClansPlugin.dbg_printStacktrace(e);
        }
    }
    public final void update(String sql, Consumer<Integer> callback) throws SQLException {
        if (!isConnected())
            throw new IllegalStateException("Database is not connected!");
        try (Statement st = con.createStatement()) {
            callback.accept(st.executeUpdate(sql));
        } catch (SQLException e) {
            // TODO: better message
            ClansPlugin.log(Level.SEVERE, "SQL Failure: " + e.getLocalizedMessage() + " !!!");
            ClansPlugin.dbg("SQLState: {0}", e.getSQLState());
            ClansPlugin.dbg_printStacktrace(e);
        }
    }
}
