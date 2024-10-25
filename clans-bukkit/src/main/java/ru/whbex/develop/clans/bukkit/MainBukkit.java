package ru.whbex.develop.clans.bukkit;


import com.djaytan.bukkit.slf4j.BukkitLoggerFactory;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.N;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import ru.whbex.develop.clans.bukkit.cmd.ClanCommandBukkit;
import ru.whbex.develop.clans.bukkit.cmd.ClansPluginCommandBukkit;
import ru.whbex.develop.clans.bukkit.cmd.TBD;
import ru.whbex.develop.clans.bukkit.listener.ListenerBukkit;
import ru.whbex.develop.clans.bukkit.player.ConsoleActorBukkit;
import ru.whbex.develop.clans.bukkit.player.PlayerManagerBukkit;
import ru.whbex.develop.clans.bukkit.conf.ConfigBukkit;
import ru.whbex.develop.clans.bukkit.task.TaskSchedulerBukkit;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.Constants;
import ru.whbex.develop.clans.common.task.TaskScheduler;
import ru.whbex.develop.clans.common.clan.ClanManager;
import ru.whbex.develop.clans.common.clan.bridge.Bridge;
import ru.whbex.develop.clans.common.clan.bridge.NullBridge;
import ru.whbex.develop.clans.common.clan.bridge.sql.SQLBridge;
import ru.whbex.develop.clans.common.player.PlayerManager;
import ru.whbex.develop.clans.common.conf.Config;
import ru.whbex.develop.clans.common.player.ConsoleActor;
import ru.whbex.lib.lang.LangFile;
import ru.whbex.lib.lang.Language;
import ru.whbex.lib.log.LogContext;
import ru.whbex.lib.log.LogDebug;
import ru.whbex.lib.sql.ConnectionData;
import ru.whbex.lib.sql.SQLAdapter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class MainBukkit extends JavaPlugin implements ClansPlugin {
    private java.util.logging.Logger LOG;
    private Config config;

    private ClanManager clanManager;
    private Bridge bridge;
    private PlayerManager playerManager;
    private TaskScheduler taskScheduler;

    private SQLAdapter ad = null;
    private ConnectionData dbConfig;

    private Language lang;


    @Override
    public void onLoad(){
        setupLogging();

        ClansPlugin.dbg("hello");
        ClansPlugin.log(Level.INFO, "=== Clans ===");
        ClansPlugin.log(Level.INFO, "Starting on " + Bukkit.getName());

        this.taskScheduler = new TaskSchedulerBukkit();

        setupConfig();
        setupLocales();
        setupDatabase();

        ClansPlugin.log(Level.INFO, "=== Load complete ===");

    }
    @Override
    public void onEnable(){
        databaseEnable();
        setupPM();
        this.clanManager = new ClanManager(config, bridge);

        ClansPlugin.log(Level.INFO, "Registering commands");
        this.getCommand("clans").setExecutor(new TBD());
        this.getCommand("clan").setExecutor(new ClanCommandBukkit());
        this.getCommand("clansplugin").setExecutor(new ClansPluginCommandBukkit());

        ClansPlugin.log(Level.INFO, "Registering event listeners");
        Bukkit.getPluginManager().registerEvents(new ListenerBukkit(), this);

        ClansPlugin.log(Level.INFO, "Registering ClanManager as a service");
        Bukkit.getServicesManager().register(ClanManager.class, clanManager, this, ServicePriority.Normal);

        ClansPlugin.log(Level.INFO, "Startup completed");
    }

    @Override
    public void onDisable() {
        ClansPlugin.log(Level.INFO, "Shutting down");
        if(clanManager != null)
            clanManager.shutdown();
        if(ad != null){
            try {
                if(!ad.isClosed())
                    ad.disconnect();
            } catch (SQLException e) {
                ClansPlugin.log(Level.ERROR, "Database disconnect failed, skipping");
            }
        }
    }
    private void setupDatabase(){
        Config.DatabaseType type = config.getDatabaseBackend();
        ConnectionData data = new ConnectionData(
                config.getDatabaseName(),
                // Create db in plugin folder if db is file-backed
                type.isFile() ? new File(getDataFolder(), config.getDatabaseAddress()).getAbsolutePath() : config.getDatabaseAddress(),
                config.getDatabaseUser(),
                config.getDatabasePassword());
        ClansPlugin.dbg("database address: {0}", data.dbAddress());
        this.dbConfig = data;
        try {
            Constructor<? extends SQLAdapter> cst = type.adapter().getConstructor(ConnectionData.class);
            this.ad = cst.newInstance(data);
            ad.connect();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException e){
            ClansPlugin.log(Level.ERROR, "Failed to create database adapter, contact plugin developer");
            e.printStackTrace();
        } catch (InvocationTargetException e){
            ClansPlugin.log(Level.ERROR, "Failed to initialize database adapter, reason: " + e.getTargetException().getLocalizedMessage());
            e.printStackTrace();
        } catch (SQLException e){
            ClansPlugin.log(Level.ERROR, "Failed to initialize database connection, reason: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
    private void setupLogging(){
        /* Logging setup */
        LOG = this.getLogger();
        BukkitLoggerFactory.provideBukkitLogger(LOG);
        LogContext.provideLogger(LoggerFactory.getLogger(this.getName()));
        // TODO: Use WholesomeLib logger
        Context.INSTANCE.setLogger(LoggerFactory.getLogger(this.getName()));
        Context.INSTANCE.setJavaLogger(LOG);
        Context.INSTANCE.setContext(this);

    }
    private void setupPM(){
        try {
            this.playerManager = new PlayerManagerBukkit(ad);
        } catch (SQLException e) {
            ClansPlugin.log(Level.ERROR, "Failed to initialize PlayerManager!");
            throw new RuntimeException(e);
        }
    }
    private void setupConfig(){
        this.saveDefaultConfig();
        File configFile = new File(this.getDataFolder(), "config.yml");
        try {
            config = new ConfigBukkit(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            ClansPlugin.log(Level.ERROR, "Failed to load configuration. Startup cannot be continued");
            throw new RuntimeException(e);
        }
    }
    private void setupLocales(){
        /* Language init */
        // TODO: Implement multilocale - using single locale for now
        ClansPlugin.log(Level.INFO, "Loading locales...");
        if(!(new File(getDataFolder(), Constants.LANGUAGE_FILE_NAME)).exists())
            this.saveResource(Constants.LANGUAGE_FILE_NAME, false);
        LangFile lf = new LangFile(new File(getDataFolder(), Constants.LANGUAGE_FILE_NAME));
        lang = new Language(lf);
    }
    private void databaseEnable(){
        if(ad != null) {
            try {
                bridge = config.getDatabaseBackend().bridge().getConstructor(SQLAdapter.class).newInstance(ad);
            } catch (InstantiationException | NoSuchMethodException | IllegalAccessException e) {
                ClansPlugin.log(Level.ERROR, "Failed to create database bridge, contact developer");
                ClansPlugin.dbg_printStacktrace(e);
            } catch (InvocationTargetException e) {
                ClansPlugin.log(Level.ERROR, "Failed to initialize database bridge");
                e.printStackTrace();
            }
        }
        if(bridge == null)
        {
            ClansPlugin.log(Level.WARN, "!!! Database is not configured !!!");
            ClansPlugin.log(Level.WARN, "Consider fixing this, using NullBridge for now. No clans will be loaded and saved on storage");
            bridge = new NullBridge();
        }
    }


    @Override
    public ClanManager getClanManager() {
        return clanManager;
    }

    @Override
    public TaskScheduler getTaskScheduler() {
        return new TaskSchedulerBukkit();
    }

    @Override
    public String _getVersionString() {
        return getDescription().getVersion();
    }
    public String _getDescription(){
        // real shit
        return getDescription().getDescription();
    }
    @Override
    public String _getName(){
        return getName();
    }

    @Override
    public PlayerManager getPlayerManager(){
        return playerManager;
    }

    @Override
    public Language getLanguage() {
        return lang;
    }

    @Override
    public SQLAdapter getSQLAdapter() {
        return ad;
    }

    // TODO: Add exception signature to the method on interface. Im lazy, sorry.
    @Override
    public <T extends SQLAdapter> T newSQLAdapter(Class<T> clazz) {
        try {
            return clazz.getConstructor(ConnectionData.class).newInstance(dbConfig);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e){
            ClansPlugin.log(Level.ERROR, "Failed to create SQLAdapter " + clazz.getName());
            throw new RuntimeException(e);
        }
    }
    @Override
    public void reloadLangFiles() throws Exception {
        ClansPlugin.dbg("Locale reload not implemented");

    }

    @Override
    public void reloadConfigs() throws Exception {
        this.config.reload();

    }

    public Config getConfigWrapped(){
        return config;
    }

}
