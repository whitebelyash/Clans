package ru.whbex.develop.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.C;
import ru.whbex.develop.bukkit.cmd.TBD;
import ru.whbex.develop.bukkit.listener.ListenerBukkit;
import ru.whbex.develop.bukkit.wrap.TaskBukkit;
import ru.whbex.develop.bukkit.wrap.ConfigWrapperBukkit;
import ru.whbex.develop.bukkit.wrap.ConsoleActorBukkit;
import ru.whbex.develop.bukkit.wrap.PlayerActorBukkit;
import ru.whbex.develop.common.ClansPlugin;
import ru.whbex.develop.common.clan.Clan;
import ru.whbex.develop.common.clan.ClanManager;
import ru.whbex.develop.common.clan.loader.SQLBridge;
import ru.whbex.develop.common.cmd.CommandActor;
import ru.whbex.develop.common.db.SQLAdapter;
import ru.whbex.develop.common.db.SQLiteAdapter;
import ru.whbex.develop.common.lang.LangFile;
import ru.whbex.develop.common.lang.Language;
import ru.whbex.develop.common.misc.StringUtils;

import ru.whbex.develop.common.wrap.ConfigWrapper;
import ru.whbex.develop.common.wrap.ConsoleActor;
import ru.whbex.develop.common.player.PlayerActor;
import ru.whbex.develop.common.wrap.Task;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainBukkit extends JavaPlugin implements ClansPlugin {
    private Logger LOG;
    private ConsoleActor console = new ConsoleActorBukkit();
    private ConfigWrapper config;

    private Map<UUID, PlayerActor> actors = new HashMap<>();
    private Map<String, PlayerActor> actorsN = new HashMap<>();
    private ExecutorService dbExecutor;
    private Future<Void> dbConnection;
    private ClanManager clanManager;

    private SQLAdapter ad = null;

    private Language lang;


    @Override
    public void onLoad(){
        LOG = getLogger();
        Context.INSTANCE.setLogger(LOG);
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
        this.clanManager = new ClanManager(config, br);
        LOG.info("Registering commands");
        this.getCommand("clans").setExecutor(new TBD());
        LOG.info("Registering event listeners");
        Bukkit.getPluginManager().registerEvents(new ListenerBukkit(), this);
        LOG.info("Registering services");
        Bukkit.getServicesManager().register(ClanManager.class, clanManager, this, ServicePriority.Normal);
        LOG.info("Stage 2 complete");
        LOG.info(StringUtils.simpleformat("{0} v{1} - enabled successfully", this.getName(), this.getDescription().getVersion()));
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
                ClansPlugin.log(Level.SEVERE, "Database disconnect failed, skipping");
            }
        }
        LOG.info("OK");
    }
    private void databaseInit(){
        /* Database init */
        try {
            ad = new SQLiteAdapter(new File(getDataFolder(), "clans.db"));
        } catch (ClassNotFoundException e) {
            ClansPlugin.log(Level.SEVERE, "Database init failed: no SQLite driver found in classpath!!! Shutting down");
            ClansPlugin.dbg_printStacktrace(e);
        } catch (IOException e) {
            ClansPlugin.log(Level.SEVERE, "Database init failed: couldn't create sqlite db file!!! Shutting down");
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
                dbConnection.get(3, TimeUnit.SECONDS);
            } catch (CancellationException | InterruptedException e){
                ClansPlugin.log(Level.SEVERE, "Database wait interrupted or cancelled!");
            } catch (TimeoutException e){
                ClansPlugin.log(Level.SEVERE, "Timed out waiting for database connection");
            } catch (ExecutionException e){
                ClansPlugin.log(Level.SEVERE, "Database connection failed: " + e.getLocalizedMessage());
                ClansPlugin.dbg_printStacktrace(e);
            }
        }
        else {
            try {
                ad.update("CREATE TABLE IF NOT EXISTS clans (id varchar(36), tag varchar(16), " +
                                "name varchar(24), " +
                                "description varchar(255), " +
                                "creationEpoch LONG, " + // TODO: fixxx
                                "leader varchar(36), " +
                                "deleted TINYINT(1), " +
                                "level INT, " +
                                "exp INT);");
            } catch (SQLException e) {
                ClansPlugin.log(Level.SEVERE, "Failed to execute initial SQL Update: " + e.getLocalizedMessage());
            }
        }
    }

    @Override
    public ConsoleActor getConsoleActor() {
        return console;
    }



    @Override
    public ClanManager getClanManager() {
        return clanManager;
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

    public PlayerActor registerOrGetActor(UUID id){
        if(actors.containsKey(id))
            return actors.get(id);
        registerActor(id);
        return actors.get(id);
    }
    public void registerActor(UUID id){
        PlayerActor pa = new PlayerActorBukkit(id);
        actors.put(id, pa);
        actorsN.put(pa.getName(), pa);
        ClansPlugin.dbg("Registered actor {0}", pa);
    }

    @Override
    public PlayerActor getPlayerActor(UUID id) {
        return actors.get(id);
    }

    @Override
    @Nullable
    public PlayerActor getPlayerActor(String name) {
        return actorsN.get(name);
    }

    @Override
    public PlayerActor getPlayerActorOrRegister(UUID id) {
        return registerOrGetActor(id);
    }

    @Override
    public Collection<PlayerActor> getOnlineActors() {
        return actors.values();
    }


    public CommandActor asCommandActor(CommandSender sender){
        return sender instanceof Player ? (CommandActor) registerOrGetActor(((Player) sender).getUniqueId()) : (CommandActor) console;
    }

    public ConfigWrapper getConfigWrapped(){
        return config;
    }
}
