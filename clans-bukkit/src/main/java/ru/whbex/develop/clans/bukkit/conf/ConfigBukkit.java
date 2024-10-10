package ru.whbex.develop.clans.bukkit.conf;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.event.Level;
import ru.whbex.develop.clans.bukkit.MainBukkit;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.conf.Config;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ConfigBukkit implements Config {
    private final YamlConfiguration config;
    private final File configFile;
    public ConfigBukkit(File configFile) throws IOException, InvalidConfigurationException {
        if(!configFile.exists()){
            ((MainBukkit) ClansPlugin.Context.INSTANCE.plugin).saveResource("config.yml", true);
        }
        this.configFile = configFile;
        this.config = new YamlConfiguration();
        this.load();
    }
    private void load() throws IOException, InvalidConfigurationException {
        config.load(configFile);
    }
    @Override
    public boolean test() {
        return false;
    }

    public void reload() throws IOException, InvalidConfigurationException {
        ClansPlugin.log(Level.INFO, "Reloading configuration...");
        this.load();
        ClansPlugin.log(Level.INFO, "Reloaded configuration");
    }

    @Override
    public DatabaseType getDatabaseBackend() {
        String val = config.getString("database.type");
        DatabaseType ret;
        try {
            ret = DatabaseType.valueOf(val.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e){
            ClansPlugin.log(Level.ERROR, "Invalid database type {0}! Falling back to H2", val);
            ret = DatabaseType.H2;
        }
        return ret;
    }

    @Override
    public String getDatabaseName() {
        return Objects.requireNonNull(config.getString("database.dbname"));
    }

    @Override
    public String getDatabaseUser() {
        return config.getString("database.user");
    }

    @Override
    public String getDatabasePassword() {
        return config.getString("database.password");
    }

    @Override
    public String getDatabaseAddress() {
        return config.getString("database.address", "");
    }

    @Override
    public long getClanFlushDelay() {
        return config.getLong("clan.flush-delay", 300);
    }
}
