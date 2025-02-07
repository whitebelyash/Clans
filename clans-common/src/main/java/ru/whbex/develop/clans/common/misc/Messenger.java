package ru.whbex.develop.clans.common.misc;

import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.lib.string.StringUtils;

public interface Messenger {

    void sendMessage(String string);
    void sendMessage(String format, Object... args);
    void sendMessageT(String translatableString);
    void sendMessageT(String translatableFormat, Object... args);
}
