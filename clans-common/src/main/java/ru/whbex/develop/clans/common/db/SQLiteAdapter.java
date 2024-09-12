package ru.whbex.develop.clans.common.db;

import ru.whbex.develop.clans.common.ClansPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteAdapter extends SQLAdapter {
    private final String path;
    public SQLiteAdapter(File db) throws ClassNotFoundException, NoClassDefFoundError, IOException {
        super(org.sqlite.JDBC.class.getName());
        if(!db.exists())
            db.createNewFile();
        this.path = SQLAdapter.JDBC_PREFIX + ":sqlite:" + db.getAbsolutePath();
    }

    @Override
    public Connection getConnection() throws SQLException {
        ClansPlugin.dbg("connect to " + path);
        DriverManager.setLoginTimeout(SQLAdapter.LOGIN_TIMEOUT);
        return DriverManager.getConnection(path);
    }
}
