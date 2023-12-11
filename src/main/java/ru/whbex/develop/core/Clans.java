package ru.whbex.develop.core;

import ru.whbex.develop.core.clan.ClanManager;
import ru.whbex.develop.core.lang.LangFile;
import ru.whbex.develop.core.player.CommandPerformer;
import ru.whbex.develop.core.player.PlayerManager;
import ru.whbex.develop.core.wrap.PlayerWrapper;
import ru.whbex.develop.core.storage.ClanStorage;
import ru.whbex.develop.core.storage.MemberStorage;
import ru.whbex.develop.core.storage.PlayerStorage;
import ru.whbex.develop.core.storage.impl.TBDClanStorage;
import ru.whbex.develop.core.storage.impl.TBDPlayerStorage;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;


// Poorly written clans plugin
// Goal - copy VanillaCraft clans functionality with some additions (because original is private & proprietary)
public final class Clans {

    public static final boolean DEBUG = true;
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
        try {
            this.language = new LangFile(new File(workDir, "default.lang"));
        } catch (IOException e) {
            LOGGER.info("Locale init fail");
            e.printStackTrace();
        }
        instance = this;
        LOGGER.info("=== Clans ===");
        dbg("Debug enabled");
        this.createManagers();
        LOGGER.info("Startup finished in " + (System.currentTimeMillis() - startTime) + " ms");
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
            LOGGER.info(String.format("DEBUG[%s]: %s",
                    Thread.currentThread().getStackTrace()[2].getClassName(),
                    msg));

    }
}
