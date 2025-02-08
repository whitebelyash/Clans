package ru.whbex.develop.clans.common.event;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEvent<T extends EventHandler> {
    protected List<T> handlerList = new ArrayList<>();
    public void register(T handler){
        handlerList.add(handler);
    }
    public void unregister(T handler){
        handlerList.remove(handler);
    }
    public void removeCallbacks(){
        handlerList.clear();
    }
}
