package ru.whbex.develop.clans.common.db;

import java.io.File;
import java.io.IOException;

public class H2SQLAdapter extends SQLAdapter {
    public H2SQLAdapter(File file) throws ClassNotFoundException, IOException {
        super(org.h2.Driver.class.getName(), "jdbc:h2:" + file.getAbsolutePath());
        if(!file.exists())
            file.createNewFile();
    }
}
