package ru.whbex.develop.bukkit.wrap;

import org.bukkit.configuration.Configuration;
import ru.whbex.develop.common.wrap.ConfigWrapper;

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
