package ru.whbex.develop.clans.common.db;

import ru.whbex.develop.clans.common.ClansPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2SQLAdapter extends SQLAdapter {
    private final String path;
    public H2SQLAdapter(File file) throws ClassNotFoundException, NoClassDefFoundError, IOException {
        super(org.h2.Driver.class.getName());
        if(!file.exists())
            file.createNewFile();
        this.path = SQLAdapter.JDBC_PREFIX + ":h2:" + file.getAbsolutePath();
    }

    @Override
    public Connection getConnection() throws SQLException {
        ClansPlugin.dbg("connect to " + path);
        DriverManager.setLoginTimeout(SQLAdapter.LOGIN_TIMEOUT);
        return DriverManager.getConnection(path);
    }
}
