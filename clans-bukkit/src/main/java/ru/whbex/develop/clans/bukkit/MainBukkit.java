package ru.whbex.develop.clans.bukkit;


import com.djaytan.bukkit.slf4j.BukkitLoggerFactory;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import ru.whbex.develop.clans.bukkit.cmd.TBD;
import ru.whbex.develop.clans.bukkit.listener.ListenerBukkit;
import ru.whbex.develop.clans.bukkit.player.PlayerManagerBukkit;
import ru.whbex.develop.clans.bukkit.wrap.TaskBukkit;
import ru.whbex.develop.clans.bukkit.wrap.ConfigWrapperBukkit;
import ru.whbex.develop.clans.bukkit.player.ConsoleActorBukkit;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.clan.ClanManager;
import ru.whbex.develop.clans.common.clan.loader.Bridge;
import ru.whbex.develop.clans.common.clan.loader.NullBridge;
import ru.whbex.develop.clans.common.clan.loader.SQLBridge;
import ru.whbex.develop.clans.common.cmd.CommandActor;
import ru.whbex.develop.clans.common.db.ConnectionData;
import ru.whbex.develop.clans.common.db.H2SQLAdapter;
import ru.whbex.develop.clans.common.db.SQLAdapter;
import ru.whbex.develop.clans.common.lang.LangFile;
import ru.whbex.develop.clans.common.lang.Language;
import ru.whbex.develop.clans.common.misc.StringUtils;

import ru.whbex.develop.clans.common.player.PlayerManager;
import ru.whbex.develop.clans.common.wrap.ConfigWrapper;
import ru.whbex.develop.clans.common.wrap.ConsoleActor;
import ru.whbex.develop.clans.common.wrap.Task;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.concurrent.*;

public class MainBukkit extends JavaPlugin implements ClansPlugin {
    private java.util.logging.Logger LOG;
    private ConsoleActor console = new ConsoleActorBukkit();
    private ConfigWrapper config;


    private ExecutorService dbExecutor;

    private ClanManager clanManager;
    private PlayerManager playerManager;

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

        this.saveDefaultConfig();
        config = new ConfigWrapperBukkit(this.getConfig());

        dbExecutor = Executors.newSingleThreadExecutor();


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
        ClansPlugin.log(Level.INFO, "Registering event listeners");
        Bukkit.getPluginManager().registerEvents(new ListenerBukkit(), this);
        ClansPlugin.log(Level.INFO, "Registering ClanManager as a service");
        Bukkit.getServicesManager().register(ClanManager.class, clanManager, this, ServicePriority.Normal);
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
        ConfigWrapper.DatabaseType type = config.getDatabaseBackend();
        Constructor<? extends SQLAdapter> cst = type.getImpl().getConstructor(ConnectionData.class);
        this.ad = cst.newInstance(data);
        if(this.ad != null)
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
    public Task run(Runnable task) {
        return new TaskBukkit(Bukkit.getScheduler().runTask(this, task));
    }

    @Override
    public Task runLater(long delay, Runnable task) {
        return new TaskBukkit(Bukkit.getScheduler().runTaskLater(this, task, delay));
    }

    @Override
    public Task runAsync(Runnable task) {
        return new TaskBukkit(Bukkit.getScheduler().runTaskAsynchronously(this, task));
    }

    @Override
    public Task runAsyncLater(long delay, Runnable task) {
        return new TaskBukkit(Bukkit.getScheduler().runTaskLaterAsynchronously(this, task, delay));
    }

    @Override
    public <T> Future<T> runCallable(Callable<T> callable) {
        return dbExecutor.submit(callable);
    }

    @Override
    public ExecutorService getDatabaseExecutor() {
        return dbExecutor;
    }

    @Override
    public void reloadLangFiles() throws Exception {
        ClansPlugin.dbg("Locale reload not implemented");

    }

    @Override
    public void reloadConfigs() throws Exception {
        ClansPlugin.dbg("Config reload not implemented");

    }

    // Actor management




    public CommandActor asCommandActor(CommandSender sender){
        return sender instanceof Player ? (CommandActor) playerManager.getOrRegisterPlayerActor(((Player) sender).getUniqueId()) : (CommandActor) console;
    }

    public ConfigWrapper getConfigWrapped(){
        return config;
    }
}
