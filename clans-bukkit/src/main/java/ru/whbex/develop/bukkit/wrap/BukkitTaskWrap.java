package ru.whbex.develop.bukkit.wrap;

import org.bukkit.scheduler.BukkitTask;
import ru.whbex.develop.common.wrap.Task;

public class BukkitTaskWrap implements Task {
    private final BukkitTask task;
    public BukkitTaskWrap(BukkitTask task){
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
