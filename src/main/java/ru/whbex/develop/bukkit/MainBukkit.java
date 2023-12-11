package ru.whbex.develop.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.whbex.develop.Clans;
import ru.whbex.develop.clan.cmd.TBD;

public class MainBukkit extends JavaPlugin {
    private Clans instance;

    @Override
    public void onEnable() {
        this.getLogger().info("Starting up on " + Bukkit.getName());
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
}
