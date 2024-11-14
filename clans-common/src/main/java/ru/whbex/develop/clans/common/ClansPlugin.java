package ru.whbex.develop.clans.common;

import org.slf4j.Logger;
import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.clan.ClanManager;
import ru.whbex.develop.clans.common.misc.DisabledPlugin;
import ru.whbex.develop.clans.common.player.PlayerManager;
import ru.whbex.develop.clans.common.task.TaskScheduler;
import ru.whbex.develop.clans.common.conf.Config;
import ru.whbex.lib.lang.Language;
import ru.whbex.lib.sql.SQLAdapter;
import ru.whbex.lib.string.StringUtils;

// Poorly written clans plugin
// Goal - copy VanillaCraft clans functionality with some additions (because original is private & proprietary)
public interface ClansPlugin {
    enum Context {
        INSTANCE;

        public ClansPlugin plugin = DisabledPlugin.INSTANCE;
        public static final String NAME = "clans";
        public void setContext(ClansPlugin plugin){
            this.plugin = plugin;
        }
    }

    String _getName();
    String _getDescription();
    String _getVersionString();

    PlayerManager getPlayerManager();
    ClanManager getClanManager();
    TaskScheduler getTaskScheduler();
    // returns system language
    Language getLanguage();

    void reloadLangFiles() throws Exception;
    void reloadConfigs() throws Exception;
    Config getConfigWrapped();
}
