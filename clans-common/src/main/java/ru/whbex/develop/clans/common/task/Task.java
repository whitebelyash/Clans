package ru.whbex.develop.clans.common.task;

public interface Task {

    void cancel();
    boolean cancelled();
    boolean sync();
    int id();

}
