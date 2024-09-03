package ru.whbex.develop.common.db;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface SQLCallback<T> {

    boolean execute(T t) throws SQLException;
}
