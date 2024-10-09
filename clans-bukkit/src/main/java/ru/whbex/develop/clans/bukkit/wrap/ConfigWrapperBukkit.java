package ru.whbex.develop.clans.bukkit.wrap;

import org.bukkit.configuration.Configuration;
import org.slf4j.event.Level;
import ru.whbex.develop.clans.common.ClansPlugin;
import ru.whbex.develop.clans.common.wrap.ConfigWrapper;

import java.util.Objects;

public class ConfigWrapperBukkit implements ConfigWrapper {
    private final Configuration config;
    public ConfigWrapperBukkit(Configuration conf){
        this.config = conf;
    }
    @Override
    public boolean test() {
        return false;
    }

    public void reload(){
        throw new UnsupportedOperationException("WIP");
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
        return config.getLong("clan.flush-delay", 5000);
    }
}
