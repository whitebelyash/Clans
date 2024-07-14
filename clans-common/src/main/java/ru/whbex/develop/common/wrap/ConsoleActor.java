package ru.whbex.develop.common.wrap;

import ru.whbex.develop.common.cmd.CommandActor;

public interface ConsoleActor {
    void sendMessage(String s);
    String getName();
}
