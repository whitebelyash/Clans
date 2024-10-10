package ru.whbex.develop.clans.bukkit;


import com.djaytan.bukkit.slf4j.BukkitLoggerFactory;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import ru.whbex.develop.clans.bukkit.cmd.ClanCommandBukkit;
import ru.whbex.develop.clans.bukkit.cmd.TBD;
import ru.whbex.develop.clans.bukkit.listener.ListenerBukkit;
import ru.whbex.develop.clans.bukkit.player.ConsoleActorBukkit;
import ru.whbex.develop.clans.bukkit.player.PlayerManagerBukkit;
import ru.whbex.develop.clans.bukkit.conf.ConfigBukkit;
import ru.whbex.develop.clans.bukkit.task.TaskSchedulerBukkit;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.task.TaskScheduler;
import ru.whbex.develop.clans.common.clan.ClanManager;
import ru.whbex.develop.clans.common.clan.bridge.Bridge;
import ru.whbex.develop.clans.common.clan.bridge.NullBridge;
import ru.whbex.develop.clans.common.clan.bridge.SQLBridge;
import ru.whbex.develop.clans.common.db.ConnectionData;
import ru.whbex.develop.clans.common.db.SQLAdapter;
import ru.whbex.develop.clans.common.lang.LangFile;
import ru.whbex.develop.clans.common.lang.Language;
import ru.whbex.develop.clans.common.player.PlayerManager;
import ru.whbex.develop.clans.common.conf.Config;
import ru.whbex.develop.clans.common.player.ConsoleActor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainBukkit extends JavaPlugin implements ClansPlugin {
    private java.util.logging.Logger LOG;
    private final ConsoleActor console = new ConsoleActorBukkit();
    private Config config;

    private ClanManager clanManager;
    private PlayerManager playerManager;
    private TaskScheduler taskScheduler;

    private SQLAdapter ad = null;
    private ConnectionData dbConfig;

    private Language lang;


    @Override
    public void onLoad(){
        /* Logging setup */
        LOG = this.getLogger();
        BukkitLoggerFactory.provideBukkitLogger(LOG);
        Context.INSTANCE.setLogger(LoggerFactory.getLogger(this.getName()));
        Context.INSTANCE.setJavaLogger(LOG);
        Context.INSTANCE.setContext(this);

        ClansPlugin.dbg("hello");
        ClansPlugin.log(Level.INFO, "=== Clans ===");
        ClansPlugin.log(Level.INFO, "Starting on " + Bukkit.getName());

        this.taskScheduler = new TaskSchedulerBukkit();
        File configFile = new File(this.getDataFolder(), "config.yml");

        try {
            config = new ConfigBukkit(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            ClansPlugin.log(Level.ERROR, "Config load failed: " + e.getLocalizedMessage());
            e.printStackTrace();
        }


        /* Language init */
        // TODO: Implement multilocale - using single locale for now
        ClansPlugin.log(Level.INFO, "Loading locales...");
        if(!(new File(getDataFolder(), "messages.lang")).exists())
            this.saveResource("messages.lang", false);
        LangFile lf = new LangFile(new File(getDataFolder(), "messages.lang"));
        lang = new Language(lf);
        try {
            databaseInit();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 SQLException e) {
            ClansPlugin.log(Level.INFO, "Database init failed: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        ClansPlugin.log(Level.INFO, "=== Load complete ===");

    }
    @Override
    public void onEnable(){
        databaseEnable();
        Bridge bridge = ad == null ? new NullBridge() : new SQLBridge(ad);
        this.playerManager = new PlayerManagerBukkit(bridge);
        this.clanManager = new ClanManager(config, bridge);

        ClansPlugin.log(Level.INFO, "Registering commands");
        this.getCommand("clans").setExecutor(new TBD());
        this.getCommand("clan").setExecutor(new ClanCommandBukkit());

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
    private void databaseInit() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        ConnectionData data = new ConnectionData(
                config.getDatabaseName(),
                config.getDatabaseAddress(),
                config.getDatabaseUser(),
                config.getDatabasePassword());
        this.dbConfig = data;
        Config.DatabaseType type = config.getDatabaseBackend();
        Constructor<? extends SQLAdapter> cst = type.getImpl().getConstructor(ConnectionData.class);
        this.ad = cst.newInstance(data);
        ad.connect();

    }
    private void databaseEnable(){
        if(ad == null){
            ClansPlugin.log(Level.WARN, "Database is not configured, skipping");
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
