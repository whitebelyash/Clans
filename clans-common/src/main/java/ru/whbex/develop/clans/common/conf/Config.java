package ru.whbex.develop.clans.common.conf;

import ru.whbex.lib.sql.conn.ConnectionProvider;
import ru.whbex.lib.sql.conn.impl.H2Provider;
import ru.whbex.lib.sql.conn.impl.SQLiteProvider;

public interface Config {

    enum DatabaseType {
        H2(H2Provider.class, true),
        SQLITE(SQLiteProvider.class, true);

        private final Class<? extends ConnectionProvider> provider;
        private final boolean file;

        DatabaseType(Class<? extends ConnectionProvider> provider, boolean file){
            this.provider = provider;
            this.file = file;
        }

        public Class<? extends ConnectionProvider> provider(){
            return provider;
        }
        public boolean isFile() {
            return file;
        }
    }

    void reload() throws Exception;

    DatabaseType getDatabaseBackend();
    String getDatabaseName();
    String getDatabaseUser();
    String getDatabasePassword();
    String getDatabaseAddress();

    long getClanFlushDelay();

    boolean clanTransferAllowed();


}
