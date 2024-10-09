package ru.whbex.develop.clans.common;

import ru.whbex.develop.clans.common.wrap.Task;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/* Task scheduler */
public interface TaskScheduler {
    Task run(Runnable run);
    Task runAsync(Runnable run);

    // Scheduled
    Task runRepeating(Runnable run, long delay, long rate);
    Task runRepeatingAsync(Runnable run, long delay, long rate);

    // Will execute on database executorservice
    <T> Future<T> runCallable(Callable<T> callable);
    // Will implement others as needed

    void stopAll();
}
