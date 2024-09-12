package ru.whbex.develop.clans.common.db;

import ru.whbex.develop.clans.common.ClansPlugin;

import java.sql.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.slf4j.event.Level;

/* JDBC adapter. Supports query/update, preparedstatement query/update, batched update */
// TODO: Redo connection logic - maybe, connect only on db query/update?
public abstract class SQLAdapter {
    private volatile Connection con;
    private boolean conFailed = false;

    public static final String JDBC_PREFIX = "jdbc";
    public static final int LOGIN_TIMEOUT = 3;

    public SQLAdapter(String driverClass) throws ClassNotFoundException {
        Class.forName(driverClass);
    }


    public final boolean isClosed() throws SQLException {
        return con == null || con.isClosed();
    }
    public final boolean isValid() throws SQLException {
        return con != null && con.isValid(5000); // TODO: move timeout to constants?
    }

    public abstract Connection getConnection() throws SQLException;

    // TODO: Implement this another way - connect on query/update, not on plugin startup
    public final void connect() throws SQLException {
        if(!isClosed())
            throw new IllegalStateException("Already connected!");
        ClansPlugin.log(Level.INFO, "Connecting to the database...");
        con = getConnection();
        ClansPlugin.dbg("con is: " + con);
        ClansPlugin.dbg("conn fail!!");
        ClansPlugin.log(Level.INFO, "Connected");
    }
    public final Future<Void> connectAsynchronously(ExecutorService executor){
        ClansPlugin.log(Level.INFO, "Connecting to the database");
        Callable<Void> connector = () -> {
            con = getConnection();
            ClansPlugin.dbg("con is: " + con);
            if(con == null)
                ClansPlugin.dbg("conn fail!!");
            ClansPlugin.log(Level.INFO, "Connected");
            return null;
        };
        return executor.submit(connector);
    }
    public final void disconnect() throws SQLException {
        if(!isClosed()) {
            ClansPlugin.log(Level.INFO, "Disconnecting from the database...");
            con.close();
            ClansPlugin.log(Level.INFO, "Disconnected");
        }
    }
    public final boolean query(String sql, SQLCallback<ResultSet> callback) throws SQLException {
        boolean ret;
        try (
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            ClansPlugin.dbg("sql query" +
                    ": {0}", sql);
            ret = callback.execute(rs);
        } catch (SQLException e) {
            // TODO: better message
            ClansPlugin.log(Level.ERROR, "SQL Failure: " + e.getLocalizedMessage() + " !!!");
            throw new SQLException(e);
        }
        return ret;
    }

    /**
     * Execute update on database
     * @param sql sql
     * @return affected rows if success, -1 otherwise
     * @throws SQLException
     */
    public final int update(String sql) throws SQLException {
        try (Statement st = con.createStatement()) {
            ClansPlugin.dbg("sql update: {0}", sql);
            return st.executeUpdate(sql);
        } catch (SQLException e) {
            // TODO: better message
            ClansPlugin.log(Level.ERROR, "SQL Failure: " + e.getLocalizedMessage() + " !!!");
            throw new SQLException(e);
        }
    }
    public final boolean queryPrepared(String sql, SQLCallback<PreparedStatement> ps, SQLCallback<ResultSet> callback) throws SQLException{
        boolean ret;
        try(
                PreparedStatement s = con.prepareStatement(sql)
                ){
            ps.execute(s);
            ret = callback.execute(s.executeQuery());
        } catch(SQLException e){
            ClansPlugin.log(Level.ERROR, "SQL Failure: " + e.getLocalizedMessage() + " !!!");
            throw new SQLException(e);
        }
        return ret;
    }
    public final int updatePrepared(String sql, SQLCallback<PreparedStatement> ps) throws SQLException {
        try(
                PreparedStatement s = con.prepareStatement(sql)
                ){
            ps.execute(s);
            return s.executeUpdate();
        } catch (SQLException e){
            ClansPlugin.log(Level.ERROR, "SQL Failure: " + e.getLocalizedMessage() + " !!!");
            throw new SQLException(e);
        }
    }
    public final int[] updateBatched(String sql, SQLCallback<PreparedStatement> ps) throws SQLException {
        try(
                PreparedStatement s = con.prepareStatement(sql)
        ){
            ps.execute(s);
            return s.executeBatch();
        } catch (SQLException e){
            ClansPlugin.log(Level.ERROR, "SQL Failure: " + e.getLocalizedMessage() + " !!!");
            throw new SQLException(e);
        }
    }
}
