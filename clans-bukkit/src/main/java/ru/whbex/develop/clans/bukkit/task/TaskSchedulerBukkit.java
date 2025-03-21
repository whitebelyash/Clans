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

    public TaskSchedulerBukkit() {
        db = Executors.newSingleThreadExecutor();
    }

    private final MainBukkit plugin = (MainBukkit) ClansPlugin.Context.INSTANCE.plugin;

    @Override
    public Task run(Runnable run) {
        return new TaskBukkit(Bukkit.getScheduler().runTask(plugin, run), run);
    }


    @Override
    public Task runAsync(Runnable run) {
        return new TaskBukkit(Bukkit.getScheduler().runTaskAsynchronously(plugin, run), run);
    }


    @Override
    public Task runRepeating(Runnable run, long delay, long rate) {
        return new TaskBukkit(Bukkit.getScheduler().runTaskTimer(plugin, run, delay, rate), run);
    }


    @Override
    public Task runRepeatingAsync(Runnable run, long delay, long rate) {
        return new TaskBukkit(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, run, delay, rate), run);
    }

    @Override
    public Task runLater(Runnable run, long delay) {
        return new TaskBukkit(Bukkit.getScheduler().runTaskLater(plugin, run, delay), run);
    }

    @Override
    public Task runLaterAsync(Runnable run, long delay) {
        return new TaskBukkit(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, run, delay), run);
    }


    @Override
    public void stopAll() {
        LogContext.log(Level.INFO, "Cancelling all tasks...");
        Bukkit.getScheduler().cancelTasks(plugin);
    }
}

