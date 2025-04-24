package ru.whbex.develop.clans.common;

import ru.whbex.develop.clans.common.clan.ClanManager;
import ru.whbex.develop.clans.common.misc.DisabledPlugin;
import ru.whbex.develop.clans.common.player.PlayerManager;
import ru.whbex.develop.clans.common.task.TaskScheduler;
import ru.whbex.develop.clans.common.conf.Config;
import ru.whbex.lib.lang.Language;

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

    // Static getters
    static ClanManager clanManager(){
        return Context.INSTANCE.plugin.getClanManager();
    }
    static PlayerManager playerManager(){
        return Context.INSTANCE.plugin.getPlayerManager();
    }
    static Config config(){
        return Context.INSTANCE.plugin.getConfigWrapped();
    }
    static TaskScheduler taskScheduler(){
        return Context.INSTANCE.plugin.getTaskScheduler();
    }
    static Language mainLanguage(){
        return Context.INSTANCE.plugin.getLanguage();
    }
}
