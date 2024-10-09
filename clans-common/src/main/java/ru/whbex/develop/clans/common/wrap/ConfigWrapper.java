package ru.whbex.develop.clans.common.wrap;

import ru.whbex.develop.clans.common.db.H2SQLAdapter;
import ru.whbex.develop.clans.common.db.SQLAdapter;
import ru.whbex.develop.clans.common.db.SQLiteAdapter;

/* Simple config wrapper, i won't write overengineered shit here, don't even ask me */
public interface ConfigWrapper {

    enum DatabaseType {
        H2(H2SQLAdapter.class),
        SQLITE(SQLiteAdapter.class);

        private final Class<? extends SQLAdapter> clazz;

        DatabaseType(Class<? extends SQLAdapter> clazz){
            this.clazz = clazz;
        }

        public Class<? extends SQLAdapter> getImpl() {
            return clazz;
        }
    }
    boolean test();

    void reload();

    DatabaseType getDatabaseBackend();
    String getDatabaseName();
    String getDatabaseUser();
    String getDatabasePassword();
    String getDatabaseAddress();

    long getClanFlushDelay();


}
