package ru.whbex.develop.common;

import ru.whbex.develop.common.clan.ClanManager;
import ru.whbex.develop.common.lang.LangFile;
import ru.whbex.develop.common.misc.StringUtils;
import ru.whbex.develop.common.storage.PlayerStorage;
import ru.whbex.develop.common.player.CommandPerformer;
import ru.whbex.develop.common.player.PlayerManager;
import ru.whbex.develop.common.wrap.PlayerWrapper;
import ru.whbex.develop.common.storage.ClanStorage;
import ru.whbex.develop.common.storage.MemberStorage;
import ru.whbex.develop.common.storage.impl.TBDClanStorage;
import ru.whbex.develop.common.storage.impl.TBDPlayerStorage;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;


// Poorly written clans plugin
// Goal - copy VanillaCraft clans functionality with some additions (because original is private & proprietary)
public final class Clans {

    public static final boolean DEBUG = true;
    private static final String LANG_PATH = Constants.LANGUAGE_FILE_NAME; // idk why
    public static Logger LOGGER;
    private ClanManager clanManager;
    private PlayerManager playerManager;
    private ClanStorage cstorage;
    private PlayerStorage pstorage;
    private CommandPerformer console;
    private PlayerWrapper playerWrapper;
    private File workDir;
    private LangFile language;
    private static Clans instance;
    public Clans(Logger logger, CommandPerformer console, PlayerWrapper playerWrapper, File workingDir){
        LOGGER = logger;
        if(!workingDir.exists() || !workingDir.isDirectory()){
            LOGGER.warning("Working dir not found, creating new");
            workingDir.mkdir();
        }
        this.workDir = workingDir;
        this.console = console;
        this.playerWrapper = playerWrapper;
    }

    public void enable(){
        long startTime = System.currentTimeMillis();
        instance = this;
        LOGGER.info("=== Clans ===");
        dbg("Debug enabled");
        this.createManagers();
        this.setupLocales();
        LOGGER.info(StringUtils.simpleformat("Startup finished in {0} ms", System.currentTimeMillis() - startTime));
    }
    public void disable(){
        LOGGER.info("Disabling...");
        clanManager.saveAll();
        playerManager.saveProfiles();
        cstorage.close();
        pstorage.close();
    }
    private void createManagers(){
        cstorage = new TBDClanStorage();
        pstorage = new TBDPlayerStorage();
        clanManager = new ClanManager(cstorage, (MemberStorage) cstorage);
        playerManager = new PlayerManager(pstorage, console);

    }
    public boolean setupLocales(){
        this.language = new LangFile(new File(workDir, LANG_PATH));
        return this.language.isEmpty();
    }
    public static Clans instance(){
        return instance;
    }

    // Getters
    public ClanManager getClanManager(){
        return clanManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public PlayerWrapper getPlayerWrapper() {
        return playerWrapper;
    }

    public LangFile getLanguage() {
        return language;
    }
    // Debug
    // TODO: remove

    public static void dbg(String msg){
        if(DEBUG)
            LOGGER.info(StringUtils.simpleformat("DEBUG[{0}]: {1}",
                    Thread.currentThread().getStackTrace()[2].getClassName(),
                    msg));

    }
}
