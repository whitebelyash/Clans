package ru.whbex.develop.clans.common.wrap;

import java.util.concurrent.Future;

public interface Task {

    void cancel();
    boolean cancelled();
    boolean sync();
    int id();

}
