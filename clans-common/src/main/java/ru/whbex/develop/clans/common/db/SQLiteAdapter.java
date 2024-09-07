package ru.whbex.develop.clans.common.db;

import java.io.File;
import java.io.IOException;

public class SQLiteAdapter extends SQLAdapter {
    public SQLiteAdapter(File db) throws ClassNotFoundException, IOException {
        super(org.sqlite.JDBC.class.getName(), "jdbc:sqlite:" + db.getAbsolutePath());
        if(!db.exists())
            db.createNewFile();
    }
}
