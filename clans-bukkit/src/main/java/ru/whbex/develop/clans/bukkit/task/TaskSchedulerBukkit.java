package ru.whbex.develop.clans.bukkit.task;

import org.bukkit.Bukkit;
import org.slf4j.event.Level;
import ru.whbex.develop.clans.bukkit.MainBukkit;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.task.TaskScheduler;
import ru.whbex.develop.clans.common.task.Task;
import ru.whbex.lib.log.LogContext;

import java.util.concurrent.*;

public class TaskSchedulerBukkit implements TaskScheduler {
    private final ExecutorService db;
    public TaskSchedulerBukkit(){
        db = Executors.newSingleThreadExecutor();
    }
    private final MainBukkit plugin = (MainBukkit) ClansPlugin.Context.INSTANCE.plugin;
    @Override
    public Task run(Runnable run) {
        return new TaskBukkit(Bukkit.getScheduler().runTask(plugin, run));
    }



    @Override
    public Task runAsync(Runnable run) {
        return new TaskBukkit(Bukkit.getScheduler().runTaskAsynchronously(plugin, run));
    }



    @Override
    public Task runRepeating(Runnable run, long delay, long rate) {
        return new TaskBukkit(Bukkit.getScheduler().runTaskTimer(plugin, run, delay, rate));
    }


    @Override
    public Task runRepeatingAsync(Runnable run, long delay, long rate) {
        return new TaskBukkit(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, run, delay, rate));
    }

    @Override
    public <T> Future<T> runCallable(Callable<T> callable) {
        return db.submit(callable);
    }

    @Override
    public ExecutorService getDatabasePool() {
        return db;
    }


    @Override
    public void stopAll() {
        LogContext.log(Level.INFO, "Cancelling all tasks...");
        Bukkit.getScheduler().cancelTasks(plugin);
        LogContext.log(Level.INFO, "Closing database thread pool...");
        if(!db.isShutdown())
            db.shutdown();
        if(!db.isTerminated()){
            LogContext.log(Level.INFO, "Waiting for tasks to terminate...");
            try {
                if(!db.awaitTermination(5, TimeUnit.SECONDS)){
                    LogContext.log(Level.INFO, "Timed out waiting for terminate, ignoring");
                    db.shutdownNow();
                }
            } catch (InterruptedException e) {
                LogContext.log(Level.ERROR, "Interrupted task shutdown wait timeout");
            }
        }


    }
}
