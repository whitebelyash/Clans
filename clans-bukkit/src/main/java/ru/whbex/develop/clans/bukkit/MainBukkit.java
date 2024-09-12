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
import ru.whbex.develop.clans.common.clan.loader.SQLBridge;
import ru.whbex.develop.clans.common.cmd.CommandActor;
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
import java.sql.SQLException;
import java.util.concurrent.*;

public class MainBukkit extends JavaPlugin implements ClansPlugin {
    private java.util.logging.Logger LOG;
    private ConsoleActor console = new ConsoleActorBukkit();
    private ConfigWrapper config;


    private ExecutorService dbExecutor;
    private Future<Void> dbConnection;

    private ClanManager clanManager;
    private PlayerManager playerManager;

    private SQLAdapter ad = null;

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
        LOG.info("=== Clans ===");
        LOG.info("Starting on " + Bukkit.getName());

        this.saveDefaultConfig();
        config = new ConfigWrapperBukkit(this.getConfig());

        LOG.info("Starting database executor");
        dbExecutor = Executors.newSingleThreadExecutor();


        /* Language init */
        // TODO: Implement multilocale - using single locale for now
        if(!(new File(getDataFolder(), "messages.lang")).exists())
            this.saveResource("messages.lang", false);
        LangFile lf = new LangFile(new File(getDataFolder(), "messages.lang"));
        lang = new Language(lf);
        databaseInit();


        LOG.info("Stage 1 complete");

    }
    @Override
    public void onEnable(){
        databaseEnable();
        SQLBridge br = new SQLBridge(ad);
        this.playerManager = new PlayerManagerBukkit(br);
        this.clanManager = new ClanManager(config, br);
        LOG.info("Registering commands");
        this.getCommand("clans").setExecutor(new TBD());
        LOG.info("Registering event listeners");
        Bukkit.getPluginManager().registerEvents(new ListenerBukkit(), this);
        LOG.info("Registering services");
        Bukkit.getServicesManager().register(ClanManager.class, clanManager, this, ServicePriority.Normal);
        LOG.info("Stage 2 complete");
        LOG.info(StringUtils.simpleformat("{0} v{1} - enabled successfully", this.getDescription().getName(), this.getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        LOG.info("Shutting down");
        clanManager.shutdown();
        if(ad != null){
            try {
                if(!ad.isClosed())
                    ad.disconnect();
            } catch (SQLException e) {
                ClansPlugin.log(Level.ERROR, "Database disconnect failed, skipping");
            }
        }
        LOG.info("OK");
    }
    private void databaseInit(){
        /* Database init */
        try {
            // ad = new SQLiteAdapter(new File(getDataFolder(), "clans.db"));
            ad = new H2SQLAdapter(new File(getDataFolder(), "clans.h2"));
        } catch (ClassNotFoundException e) {
            ClansPlugin.log(Level.ERROR, "Database init failed: no H2 driver found in classpath!!! Shutting down");
            ClansPlugin.dbg_printStacktrace(e);
        } catch (IOException e) {
            ClansPlugin.log(Level.ERROR, "Database init failed: couldn't create sqlite db file!!! Shutting down");
            ClansPlugin.dbg_printStacktrace(e);
        } finally {
            if(ad == null)
                this.getPluginLoader().disablePlugin(this);
        }
        dbConnection = ad.connectAsynchronously(dbExecutor);
    }
    private void databaseEnable(){
        if(!dbConnection.isDone()){
            ClansPlugin.log(Level.INFO, "Waiting for database...");
            try {
                dbConnection.get(SQLAdapter.LOGIN_TIMEOUT, TimeUnit.SECONDS);
            } catch (CancellationException | InterruptedException e){
                ClansPlugin.log(Level.ERROR, "Database wait interrupted or cancelled!");
            } catch (TimeoutException e){
                ClansPlugin.log(Level.ERROR, "Timed out waiting for database connection");
            } catch (ExecutionException e){
                ClansPlugin.log(Level.ERROR, "Database connection failed: " + e.getLocalizedMessage());
                ClansPlugin.dbg_printStacktrace(e);
            }
        }
        else {
            try {
                /*
                ID, TAG, NAME, DESCRIPTION, CREATIONEPOCH, LEADER, DELETED, LEVEL, EXP
                 */
                ad.update("CREATE TABLE IF NOT EXISTS clans (id varchar(36), tag varchar(16), " +
                                "name varchar(24), " +
                                "description varchar(255), " +
                                "creationEpoch LONG, " + // TODO: fixxx
                                "leader varchar(36), " +
                                "deleted TINYINT, " +
                                "level INT, " +
                                "exp INT);");
            } catch (SQLException e) {
                ClansPlugin.log(Level.ERROR, "Failed to execute initial SQL Update: " + e.getLocalizedMessage());
            }
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
    public void reloadLocales() throws Exception {
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
