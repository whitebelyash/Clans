package ru.whbex.develop.clans.common.task;

public interface Task {

    void cancel();
    void run();
    boolean cancelled();
    boolean sync();
    int id();

}
