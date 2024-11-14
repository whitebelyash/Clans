package ru.whbex.develop.clans.common.task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
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
    ExecutorService getDatabasePool();
    // Will implement others as needed

    void stopAll();
}
