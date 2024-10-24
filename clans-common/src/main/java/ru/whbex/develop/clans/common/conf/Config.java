package ru.whbex.develop.clans.common.conf;

import ru.whbex.develop.clans.common.clan.bridge.sql.H2Bridge;
import ru.whbex.develop.clans.common.clan.bridge.sql.SQLBridge;
import ru.whbex.develop.clans.common.clan.bridge.sql.SQLiteBridge;
import ru.whbex.lib.sql.SQLAdapter;
import ru.whbex.lib.sql.impl.H2SQLAdapter;
import ru.whbex.lib.sql.impl.SQLiteAdapter;

/* Simple config wrapper, i won't write overengineered shit here, don't even ask me */
public interface Config {

    enum DatabaseType {
        H2(H2SQLAdapter.class, H2Bridge.class),
        SQLITE(SQLiteAdapter.class, SQLiteBridge.class);

        private final Class<? extends SQLAdapter> adapter;
        private final Class<? extends SQLBridge> bridge;

        DatabaseType(Class<? extends SQLAdapter> adapter, Class<? extends SQLBridge> bridge){
            this.adapter = adapter;
            this.bridge = bridge;
        }

        public Class<? extends SQLAdapter> adapter() {
            return adapter;
        }

        public Class<? extends SQLBridge> bridge() {
            return bridge;
        }
    }
    boolean test();

    void reload() throws Exception;

    DatabaseType getDatabaseBackend();
    String getDatabaseName();
    String getDatabaseUser();
    String getDatabasePassword();
    String getDatabaseAddress();

    long getClanFlushDelay();


}
