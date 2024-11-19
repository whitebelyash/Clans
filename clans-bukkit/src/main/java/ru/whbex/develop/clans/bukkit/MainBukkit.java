package ru.whbex.develop.clans.bukkit;


import com.djaytan.bukkit.slf4j.BukkitLoggerFactory;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import ru.whbex.develop.clans.bukkit.cmd.ClanCommandBukkit;
import ru.whbex.develop.clans.bukkit.cmd.ClansPluginCommandBukkit;
import ru.whbex.develop.clans.bukkit.cmd.TBD;
import ru.whbex.develop.clans.bukkit.listener.ListenerBukkit;
import ru.whbex.develop.clans.bukkit.player.PlayerManagerBukkit;
import ru.whbex.develop.clans.bukkit.conf.ConfigBukkit;
import ru.whbex.develop.clans.bukkit.task.TaskSchedulerBukkit;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.Constants;
import ru.whbex.develop.clans.common.misc.DisabledPlugin;
import ru.whbex.develop.clans.common.task.DatabaseService;
import ru.whbex.develop.clans.common.task.TaskScheduler;
import ru.whbex.develop.clans.common.clan.ClanManager;
import ru.whbex.develop.clans.common.clan.bridge.Bridge;
import ru.whbex.develop.clans.common.clan.bridge.NullBridge;
import ru.whbex.develop.clans.common.player.PlayerManager;
import ru.whbex.develop.clans.common.conf.Config;
import ru.whbex.lib.lang.LanguageFile;
import ru.whbex.lib.lang.Language;
import ru.whbex.lib.log.Debug;
import ru.whbex.lib.log.LogContext;
import ru.whbex.lib.reflect.ConstructUtils;
import ru.whbex.lib.sql.conn.ConnectionConfig;
import ru.whbex.lib.sql.conn.ConnectionProvider;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class MainBukkit extends JavaPlugin implements ClansPlugin {
    private java.util.logging.Logger LOG;
    private Config config;

    private ClanManager clanManager;
    private Bridge bridge;
    private PlayerManager playerManager;
    private TaskScheduler taskScheduler;

    private ConnectionProvider provider;
    private ConnectionConfig dbConfig;

    private Language lang;


    @Override
    public void onLoad(){
        setupLogging();

        Debug.print("hello");
        LogContext.log(Level.INFO, "=== Clans ===");
        LogContext.log(Level.INFO, "Starting on " + Bukkit.getName());

        this.taskScheduler = new TaskSchedulerBukkit();

        setupConfig();
        setupLocales();


        try {
            setupDatabase();
        } catch (InvocationTargetException e) {
            LogContext.log(Level.ERROR, "Failed to initialize database connection provider, contact developer");
        } catch (SQLException e) {
            LogContext.log(Level.ERROR, "Failed to connect to the database, message: {0}", e.getMessage());
        } catch (IllegalAccessException e) {
            LogContext.log(Level.ERROR, "Failed to initialize database service: {0}!", e.getMessage());
        }

        LogContext.log(Level.INFO, "=== Load complete ===");

    }
    @Override
    public void onEnable(){
        databaseEnable();
        setupPM();
        this.clanManager = new ClanManager(config, bridge);

        LogContext.log(Level.INFO, "Registering commands");
        this.getCommand("clans").setExecutor(new TBD());
        this.getCommand("clan").setExecutor(new ClanCommandBukkit());
        this.getCommand("clansplugin").setExecutor(new ClansPluginCommandBukkit());

        LogContext.log(Level.INFO, "Registering event listeners");
        Bukkit.getPluginManager().registerEvents(new ListenerBukkit(), this);

        LogContext.log(Level.INFO, "Registering ClanManager as a service");
        Bukkit.getServicesManager().register(ClanManager.class, clanManager, this, ServicePriority.Normal);

        LogContext.log(Level.INFO, "Startup completed");
    }

    @Override
    public void onDisable() {
        LogContext.log(Level.INFO, "Shutting down");
        // ClanManager
        if(clanManager != null)
            clanManager.shutdown();
        // DatabaseService
        DatabaseService.destroyService();
        // TaskScheduler
        taskScheduler.stopAll();
        LogContext.log(Level.INFO, "Bye!");
        Context.INSTANCE.plugin = DisabledPlugin.INSTANCE;
        LogContext.provideLogger(null);
    }
    private void setupDatabase() throws InvocationTargetException, SQLException, IllegalAccessException {
        Config.DatabaseType type = config.getDatabaseBackend();
        ConnectionConfig data = new ConnectionConfig(
                config.getDatabaseName(),
                // Create db in plugin folder if db is file-backed
                type.isFile() ? new File(getDataFolder(), config.getDatabaseAddress()).getAbsolutePath() : config.getDatabaseAddress(),
                config.getDatabaseUser(),
                config.getDatabasePassword());
        Debug.print("database address: {0}", data.dbAddress());
        ConnectionProvider prov = ConstructUtils.newInstance(config.getDatabaseBackend().provider(), data);
        if(prov == null){
            LogContext.log(Level.ERROR, "Failed to initialize connection provider!");
            return;
        }
        // This will hold database connection
        prov.newConnection();
        // Create global database service
        DatabaseService.initializeService(prov);
        if(!DatabaseService.isInitialized())
            LogContext.log(Level.ERROR, "DatabaseService init failed!");

    }
    private void setupLogging(){
        /* Logging setup */
        LOG = this.getLogger();
        BukkitLoggerFactory.provideBukkitLogger(LOG);
        LogContext.provideLogger(LoggerFactory.getLogger(this.getName()));
        Context.INSTANCE.setContext(this);

    }
    private void setupPM(){
        this.playerManager = new PlayerManagerBukkit();
    }
    private void setupConfig(){
        this.saveDefaultConfig();
        File configFile = new File(this.getDataFolder(), "config.yml");
        try {
            config = new ConfigBukkit(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            LogContext.log(Level.ERROR, "Failed to load configuration. Startup cannot be continued");
            throw new RuntimeException(e);
        }
    }
    private void setupLocales(){
        /* Language init */
        // TODO: Implement multilocale - using single locale for now
        LogContext.log(Level.INFO, "Loading locales...");
        if(!(new File(getDataFolder(), Constants.LANGUAGE_FILE_NAME)).exists())
            this.saveResource(Constants.LANGUAGE_FILE_NAME, false);
        LanguageFile lf = new LanguageFile(new File(getDataFolder(), Constants.LANGUAGE_FILE_NAME));
        lang = new Language(lf);
    }
    private void databaseEnable(){
        Debug.print("Creating ClanBridge...");
        if(DatabaseService.isInitialized()) {
            try {
                bridge = config.getDatabaseBackend().bridge().getConstructor().newInstance();
            } catch (InstantiationException | NoSuchMethodException | IllegalAccessException e) {
                LogContext.log(Level.ERROR, "Failed to create database bridge, contact developer");
                Debug.dbg_printStacktrace(e);
            } catch (InvocationTargetException e) {
                LogContext.log(Level.ERROR, "Failed to initialize database bridge");
                Debug.dbg_printStacktrace(e);
            }
        }
        if(bridge == null)
        {
            LogContext.log(Level.WARN, "!!! Database is not configured !!!");
            LogContext.log(Level.WARN, "Consider fixing this, using NullBridge for now. No clans will be loaded and saved on storage");
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
    public void reloadLangFiles() throws Exception {
        Debug.print("Locale reload not implemented");

    }

    @Override
    public void reloadConfigs() throws Exception {
        this.config.reload();

    }

    public Config getConfigWrapped(){
        return config;
    }

}
