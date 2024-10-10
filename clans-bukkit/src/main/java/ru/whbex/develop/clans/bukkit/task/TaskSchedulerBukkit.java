package ru.whbex.develop.clans.bukkit.task;

import org.bukkit.Bukkit;
import org.slf4j.event.Level;
import ru.whbex.develop.clans.bukkit.MainBukkit;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.task.TaskScheduler;
import ru.whbex.develop.clans.common.task.Task;

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
    public void stopAll() {
        Bukkit.getScheduler().cancelTasks(plugin);
        if(db.isShutdown())
            return;
        try {
            boolean timeout = db.awaitTermination(5, TimeUnit.SECONDS);
            if(timeout){
                ClansPlugin.log(Level.ERROR, "Timed out waiting for tasks to stop. Shutting down anyway");
                db.shutdown();
            }
        } catch (InterruptedException e) {
            ClansPlugin.log(Level.ERROR, "Interrupted wait for database executor task termination, this is not ok!");
        }

    }
}
