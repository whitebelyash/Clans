package ru.whbex.develop.clans.common.db;

import java.sql.SQLException;

@FunctionalInterface
public interface SQLCallback<T> {

    boolean execute(T t) throws SQLException;
}
