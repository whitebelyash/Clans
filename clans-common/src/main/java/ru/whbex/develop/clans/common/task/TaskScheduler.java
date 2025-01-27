package ru.whbex.develop.clans.common.task;

/* Wrapped task scheduler */
public interface TaskScheduler {
    Task run(Runnable run);
    Task runAsync(Runnable run);

    // Scheduled
    Task runRepeating(Runnable run, long delay, long rate);
    Task runRepeatingAsync(Runnable run, long delay, long rate);
    // Later
    Task runLater(Runnable run, long delay);
    Task runLaterAsync(Runnable run, long delay);
    // Will implement others as needed

    void stopAll();
}
