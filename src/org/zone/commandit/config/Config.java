package org.zone.commandit.config;

import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;
import org.zone.commandit.io.YamlLoader;

public class Config extends ConfigStore {
    
    public Config(JavaPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public void load() {
        Configuration config = YamlLoader.loadResource(plugin, "config.yml");
        
        for (String k : config.getKeys(true)) {
            if (!config.isConfigurationSection(k)) {
                this.put(k, config.getString(k));
            }
        }
    }
    
}
