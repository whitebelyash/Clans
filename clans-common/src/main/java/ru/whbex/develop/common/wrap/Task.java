package ru.whbex.develop.common.wrap;

public interface Task {

    void cancel();
    boolean cancelled();
    boolean sync();
    int id();

}
