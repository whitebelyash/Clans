package ru.whbex.develop.common.clan.loader;

import ru.whbex.develop.common.db.SQLAdapter;

import java.sql.SQLException;

public class SQLBridge implements Bridge {
    private final SQLAdapter adapter;

    public SQLBridge(SQLAdapter adapter){
        this.adapter = adapter;
    }
    @Override
    public boolean valid() {
        try {
            return adapter.isClosed() && adapter.isValid();
        } catch (SQLException e) {
            return false;
        }
    }
}
