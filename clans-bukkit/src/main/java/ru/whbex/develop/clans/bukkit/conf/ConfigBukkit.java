package ru.whbex.develop.clans.bukkit.conf;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.event.Level;
import ru.whbex.develop.clans.bukkit.MainBukkit;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.conf.Config;
import ru.whbex.lib.log.LogContext;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ConfigBukkit implements Config {
    private final YamlConfiguration config;
    private final File configFile;

    /* Settings */
    private DatabaseType dbType;
    private String dbAddress;
    private String dbName;
    private String dbUser;
    private String dbPassword;
    private long flushDelay;
    private boolean allowTransfer;
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

        String val = config.getString("database.type");
        try {
               dbType = DatabaseType.valueOf(val.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e){
            LogContext.log(Level.ERROR, "Invalid database type {0}! Falling back to H2", val);
            dbType = DatabaseType.H2;
        }
        dbAddress = config.getString("database.address", ".");
        dbName = config.getString("database.name", "clans");
        dbUser = config.getString("database.user", "root");
        dbPassword = config.getString("database.password", "12345");
        flushDelay = config.getLong("clan.flush-delay", 300);
        allowTransfer = config.getBoolean("clan.allow-transfer", true);
    }
    @Override
    public boolean test() {
        return false;
    }

    public void reload() throws IOException, InvalidConfigurationException {
        LogContext.log(Level.INFO, "Reloading configuration...");
        this.load();
        LogContext.log(Level.INFO, "Reloaded configuration");
    }

    @Override
    public DatabaseType getDatabaseBackend() {
        return dbType;
    }

    @Override
    public String getDatabaseName() {
        return dbName;
    }

    @Override
    public String getDatabaseUser() {
        return dbUser;
    }

    @Override
    public String getDatabasePassword() {
        return dbPassword;
    }

    @Override
    public String getDatabaseAddress() {
        return dbAddress;
    }

    @Override
    public long getClanFlushDelay() {
        return flushDelay;
    }

    @Override
    public boolean clanTransferAllowed() {
        return allowTransfer;
    }
}
