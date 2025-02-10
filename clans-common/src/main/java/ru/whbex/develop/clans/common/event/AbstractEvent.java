package ru.whbex.develop.clans.common.event;

import ru.whbex.lib.log.Debug;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEvent<T extends EventHandler> {
    protected List<T> handlerList = new ArrayList<>();
    public void register(T handler){
        Debug.tprint("EventSystem", "Registered event handler (caller: " + new Throwable().getStackTrace()[2].getFileName() + ")");
        handlerList.add(handler);
    }
    public void unregister(T handler){
        Debug.tprint("EventSystem", "Unegistered event handler (caller: " + new Throwable().getStackTrace()[2].getFileName() + ")");
        handlerList.remove(handler);
    }
    public void removeCallbacks(){
        Debug.tprint("EventSystem", "Removed event handler callbacks (caller: " + new Throwable().getStackTrace()[2].getFileName() + ")");
        handlerList.clear();
    }
}
