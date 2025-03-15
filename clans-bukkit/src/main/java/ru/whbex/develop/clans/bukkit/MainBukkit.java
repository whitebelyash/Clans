package ru.whbex.develop.clans.bukkit;


import com.djaytan.bukkit.slf4j.BukkitLoggerFactory;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import ru.whbex.develop.clans.bukkit.cmd.ClanCommandBukkit;
import ru.whbex.develop.clans.bukkit.cmd.ClansPluginCommandBukkit;
import ru.whbex.develop.clans.bukkit.listener.ListenerBukkit;
import ru.whbex.develop.clans.bukkit.player.v2.PlayerManagerBukkit;
import ru.whbex.develop.clans.bukkit.conf.ConfigBukkit;
import ru.whbex.develop.clans.bukkit.task.TaskSchedulerBukkit;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.Constants;
import ru.whbex.develop.clans.common.cmd.exec.Command;
import ru.whbex.develop.clans.common.misc.DisabledPlugin;
import ru.whbex.develop.clans.common.misc.MiscUtils;
import ru.whbex.develop.clans.common.task.DatabaseService;
import ru.whbex.develop.clans.common.task.TaskScheduler;
import ru.whbex.develop.clans.common.clan.ClanManager;
import ru.whbex.develop.clans.common.player.v2.PlayerManager;
import ru.whbex.develop.clans.common.conf.Config;
import ru.whbex.lib.bukkit.cmd.CommandManager;
import ru.whbex.lib.bukkit.cmd.StubCommand;
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
import java.util.ArrayList;
import java.util.List;

public class MainBukkit extends JavaPlugin implements ClansPlugin {
    private java.util.logging.Logger LOG;
    private Config config;

    private ClanManager clanManager;
    private PlayerManager playerManager;
    private TaskScheduler taskScheduler;

    private Language lang;

    // TODO: disable in prod/release
    private static final boolean REPLACE_LOCALES = true;
    private List<Command> commandList = new ArrayList<>();

    private static final boolean DB_PROPERTIES_DEFINED = Boolean.getBoolean("clans.db-properties-defined");


    @Override
    public void onLoad(){
        setupLogging();

        Debug.print("hello");
        LogContext.log(Level.INFO, "=== Clans ===");
        LogContext.log(Level.INFO, "Starting on " + Bukkit.getName());

        this.taskScheduler = new TaskSchedulerBukkit();

        setupConfig();
        try {
            setupLocales();
        } catch (IOException e) {
            LogContext.log(Level.ERROR, "Failed to initialize main language. See below stacktrace for more info");
            e.printStackTrace();
        }
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
        setupPM();
        this.clanManager = new ClanManager();
        registerCommands();
        Bukkit.getPluginManager().registerEvents(new ListenerBukkit(), this);
        LogContext.log(Level.INFO, "Startup completed");
    }

    @Override
    public void onDisable() {
        LogContext.log(Level.INFO, "Shutting down");
        if(clanManager != null)
            clanManager.shutdown();
        DatabaseService.destroyService();
        taskScheduler.stopAll();
        LogContext.log(Level.INFO, "Bye!");
        Context.INSTANCE.plugin = DisabledPlugin.INSTANCE;
        LogContext.provideLogger(null);
    }
    private void setupDatabase() throws InvocationTargetException, SQLException, IllegalAccessException {
        Config.DatabaseType type = !DB_PROPERTIES_DEFINED ?
                config.getDatabaseBackend() :
                Config.DatabaseType.valueOf(System.getProperty("clans.db-backend"));
        ConnectionConfig data = !DB_PROPERTIES_DEFINED ? new ConnectionConfig(
                config.getDatabaseName(),
                // Create db in plugin folder if db is file-backed
                type.isFile() ? new File(getDataFolder(), config.getDatabaseAddress()).getAbsolutePath() : config.getDatabaseAddress(),
                config.getDatabaseUser(),
                config.getDatabasePassword()) :
                MiscUtils.systemConnectionConfig(type, getDataFolder());
      //  Debug.print("database address: {0}", data.dbAddress());
        ConnectionProvider prov = ConstructUtils.newInstance(type.provider(), data);
        if(prov == null){
            LogContext.log(Level.ERROR, "Failed to initialize connection provider instance!");
            return;
        }
        DatabaseService.initializeService(prov);
        if(!DatabaseService.isInitialized())
            LogContext.log(Level.ERROR, "Failed to initialize DatabaseService");
    }
    private void setupLogging(){
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
    private void setupLocales() throws IOException {
        // TODO: Implement multilocale - using single locale for now
        LogContext.log(Level.INFO, "Loading locales...");
        if(!(new File(getDataFolder(), Constants.LANGUAGE_FILE_NAME)).exists())
            this.saveResource(Constants.LANGUAGE_FILE_NAME, REPLACE_LOCALES);
        this.lang = new Language(new LanguageFile(new File(getDataFolder(), Constants.LANGUAGE_FILE_NAME)));
        // TODO: add escape support
        this.lang.setPhraseMapper(s -> s.replaceAll("&", "§"));
        this.lang.load();
    }
    private void registerCommands(){
        LogContext.log(Level.INFO, "Registering commands");
        commandList.add(new ClanCommandBukkit());
        commandList.add(new ClansPluginCommandBukkit());
        //commandList.add(new ClanChatCommandBukkit());
        //commandList.add(new AllyChatCommandBukkit());

        commandList.forEach(c -> CommandManager.registerCommand("clans", new StubCommand(c.name(), (CommandExecutor) c)));
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
        lang.reloadPhrases();
    }

    @Override
    public void reloadConfigs() throws Exception {
        this.config.reload();

    }

    public Config getConfigWrapped(){
        return config;
    }

}
