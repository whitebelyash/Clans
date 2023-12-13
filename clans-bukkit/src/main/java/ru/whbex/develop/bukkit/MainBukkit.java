package ru.whbex.develop.bukkit;

import ru.whbex.develop.common.Clans;
import ru.whbex.develop.bukkit.cmd.TBD;
import ru.whbex.develop.bukkit.listener.MainListener;
import ru.whbex.develop.bukkit.wrap.ConsoleWrapperBukkit;
import ru.whbex.develop.bukkit.wrap.PlayerWrapperBukkit;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.whbex.develop.common.Constants;

import java.io.File;

public class MainBukkit extends JavaPlugin {
    private Clans instance;
    private static MainBukkit mInstance;

    @Override
    public void onEnable() {
        mInstance = this;
        this.getLogger().info("Starting up on " + Bukkit.getName());
        if(!(new File(this.getDataFolder(), Constants.LANGUAGE_FILE_NAME)).exists())
            this.saveResource(Constants.LANGUAGE_FILE_NAME, true);
        instance = new Clans(this.getLogger(), new ConsoleWrapperBukkit(), new PlayerWrapperBukkit(), this.getDataFolder());
        instance.enable();
        this.getCommand("clans").setExecutor(new TBD());
        Bukkit.getPluginManager().registerEvents(new MainListener(), this);
    }

    @Override
    public void onDisable() {
        if(instance != null)
            instance.disable();
        else
            this.getLogger().info("Instance is null, skipping shutdown");

    }

    public static MainBukkit getInstance() {
        return mInstance;
    }
}
