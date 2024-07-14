package ru.whbex.develop.common;

import ru.whbex.develop.common.clan.ClanManager;
import ru.whbex.develop.common.lang.Language;
import ru.whbex.develop.common.misc.DisabledPlugin;
import ru.whbex.develop.common.misc.StringUtils;
import ru.whbex.develop.common.wrap.ConsoleActor;
import ru.whbex.develop.common.player.PlayerActor;

import java.util.UUID;
import java.util.logging.Logger;


// Poorly written clans plugin
// Goal - copy VanillaCraft clans functionality with some additions (because original is private & proprietary)
public interface ClansPlugin {
    public enum Context {
        INSTANCE;

        public ClansPlugin plugin = new DisabledPlugin();
        public Logger logger;
        public static final boolean DEBUG = true;
        public void setContext(ClansPlugin plugin){
            this.plugin = plugin;
        }
        public void setLogger(Logger log){
            this.logger = log;
        }
    }
    Logger getLogger();
    ConsoleActor getConsoleActor();
    PlayerActor getPlayerActor(UUID id);
    PlayerActor getPlayerActor(String name);
    ClanManager getClanManager();
    Language getLanguage();

    void reloadLocales() throws Exception;
    void reloadConfigs() throws Exception;

    static void dbg(String m, Object... args){
        if(!Context.DEBUG)
            return;
        Context.INSTANCE.logger.info(StringUtils.simpleformat("DBG({0}): {1}",
                Thread.currentThread().getStackTrace()[2].getClassName(),
                StringUtils.simpleformat(m, args)));
    }
}
