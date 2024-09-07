package ru.whbex.develop.clans.common.wrap;

public interface Task {

    void cancel();
    boolean cancelled();
    boolean sync();
    int id();

}
