package ru.whbex.develop.clans.bukkit.wrap;

import org.bukkit.configuration.Configuration;
import ru.whbex.develop.clans.common.wrap.ConfigWrapper;

public class ConfigWrapperBukkit implements ConfigWrapper {
    private final Configuration config;
    public ConfigWrapperBukkit(Configuration conf){
        this.config = conf;
    }
    @Override
    public boolean test() {
        return false;
    }
}
