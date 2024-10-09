package ru.whbex.develop.clans.bukkit.wrap;

import org.bukkit.scheduler.BukkitTask;
import ru.whbex.develop.clans.common.wrap.Task;

public class TaskBukkit implements Task {
    private final BukkitTask task;
    public TaskBukkit(BukkitTask task){
        this.task = task;

    }
    @Override
    public void cancel() {
        task.cancel();

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
