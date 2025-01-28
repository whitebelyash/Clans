package ru.whbex.develop.clans.bukkit.task;

import org.bukkit.scheduler.BukkitTask;
import ru.whbex.develop.clans.common.task.Task;

public class TaskBukkit implements Task {
    private final BukkitTask task;
    private final Runnable run;
    public TaskBukkit(BukkitTask task, Runnable run){
        this.task = task;
        this.run = run;

    }
    @Override
    public void cancel() {
        task.cancel();

    }

    @Override
    public void run() {
        run.run();
    }

    @Override
    public boolean cancelled() {
        return task.isCancelled();
    }

    @Override
    public boolean sync() {
        return task.isSync();
    }

    @Override
    public int id() {
        return task.getTaskId();
    }


}
