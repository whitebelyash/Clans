package ru.whbex.develop.common.db;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface SQLCallback {

    boolean execute(ResultSet set) throws SQLException;
}
