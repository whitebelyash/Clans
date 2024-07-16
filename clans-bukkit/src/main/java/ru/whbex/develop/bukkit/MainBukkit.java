package ru.whbex.develop.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.whbex.develop.bukkit.cmd.TBD;
import ru.whbex.develop.bukkit.listener.MainListener;
import ru.whbex.develop.bukkit.wrap.BukkitTaskWrap;
import ru.whbex.develop.bukkit.wrap.ConsoleActorBukkit;
import ru.whbex.develop.bukkit.wrap.PlayerActorBukkit;
import ru.whbex.develop.common.ClansPlugin;
import ru.whbex.develop.common.clan.ClanManager;
import ru.whbex.develop.common.cmd.CommandActor;
import ru.whbex.develop.common.lang.LangFile;
import ru.whbex.develop.common.lang.Language;
import ru.whbex.develop.common.misc.StringUtils;
import ru.whbex.develop.common.player.PlayerManager;
import ru.whbex.develop.common.storage.ClanStorage;
import ru.whbex.develop.common.storage.MemberStorage;
import ru.whbex.develop.common.storage.PlayerStorage;
import ru.whbex.develop.common.storage.impl.TBDClanStorage;
import ru.whbex.develop.common.storage.impl.TBDPlayerStorage;
import ru.whbex.develop.common.wrap.ConsoleActor;
import ru.whbex.develop.common.player.PlayerActor;
import ru.whbex.develop.common.wrap.Task;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public class MainBukkit extends JavaPlugin implements ClansPlugin {
    private Logger LOG;
    private ConsoleActor console = new ConsoleActorBukkit();

    private Map<UUID, PlayerActor> actors = new HashMap<>();
    private Map<String, PlayerActor> actorsN = new HashMap<>();
    private ClanManager clanManager;
    private PlayerManager playerManager = null;

    private ClanStorage stor;
    private PlayerStorage pstor;

    private Language lang;


    @Override
    public void onLoad(){
        LOG = getLogger();
        Context.INSTANCE.setLogger(LOG);
        Context.INSTANCE.setContext(this);

        ClansPlugin.dbg("hello");
        LOG.info("=== Clans ===");
        LOG.info("Starting on " + Bukkit.getName());

        this.saveResource("messages.lang", false);

        LangFile lf = new LangFile(new File(getDataFolder(), "messages.lang"));
        lang = new Language(lf);
        LOG.info("Load complete");

    }
    @Override
    public void onEnable(){

        LOG.info("Registering commands");
        this.getCommand("clans").setExecutor(new TBD());
        LOG.info("Registering event listeners");
        Bukkit.getPluginManager().registerEvents(new MainListener(), this);

        LOG.info(StringUtils.simpleformat("{0} v{1} - enabled successfully", this.getName(), this.getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        LOG.info("Shutting down");
        stor.close();
        pstor.close();
        LOG.info("OK");


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
    public Task run(Runnable task) {
        return new BukkitTaskWrap(Bukkit.getScheduler().runTask(this, task));
    }

    @Override
    public Task runLater(long delay, Runnable task) {
        return new BukkitTaskWrap(Bukkit.getScheduler().runTaskLater(this, task, delay));
    }

    @Override
    public Task runAsync(Runnable task) {
        return new BukkitTaskWrap(Bukkit.getScheduler().runTaskAsynchronously(this, task));
    }

    @Override
    public Task runAsyncLater(long delay, Runnable task) {
        return new BukkitTaskWrap(Bukkit.getScheduler().runTaskLaterAsynchronously(this, task, delay));
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
    public Collection<PlayerActor> getOnlineActors() {
        return actors.values();
    }


    public CommandActor asCommandActor(CommandSender sender){
        return sender instanceof Player ? (CommandActor) registerOrGetActor(((Player) sender).getUniqueId()) : (CommandActor) console;
    }
}
