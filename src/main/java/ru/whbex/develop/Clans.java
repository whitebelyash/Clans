package ru.whbex.develop;

import org.bukkit.plugin.java.JavaPlugin;
import ru.whbex.develop.clan.ClanManager;
import ru.whbex.develop.player.CommandPerformer;
import ru.whbex.develop.player.PlayerManager;
import ru.whbex.develop.storage.ClanStorage;
import ru.whbex.develop.storage.PlayerStorage;
import ru.whbex.develop.storage.impl.TBDClanStorage;
import ru.whbex.develop.storage.impl.TBDPlayerStorage;

import java.util.logging.Logger;


// Poorly written clans plugin by young java coder (IS Student kekeke) (as i think maybe it is good lol)
// Goal - copy VanillaCraft clans functionality with some additions (because original is private & proprietary)
public final class Clans {

    public static final boolean DEBUG = true;
    public static Logger LOGGER;
    private ClanManager clanManager;
    private PlayerManager playerManager;
    private ClanStorage cstorage;
    private PlayerStorage pstorage;
    private CommandPerformer console;
    private static Clans instance;
    public Clans(Logger logger, CommandPerformer console){
        LOGGER = logger;
        this.console = console;
    }

    public void enable(){
        instance = this;
        LOGGER.info("=== Clans ===");
        dbg("Debug enabled");
        this.createManagers();
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
        clanManager = new ClanManager(cstorage);
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
    // Debug
    // TODO: remove

    public static void dbg(String msg){
        if(DEBUG)
            LOGGER.info(String.format("DEBUG[%s]: %s",
                    Thread.currentThread().getStackTrace()[2].getClassName(),
                    msg));

    }
}
