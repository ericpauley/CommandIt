package org.zone.commandit.config;

import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;
import org.zone.commandit.io.YamlLoader;

public class Messages extends ConfigStore {
    
    public Messages(JavaPlugin plugin) {
        super(plugin);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void load() {
        Configuration config = YamlLoader.loadResource(plugin, "messages.yml");
        
        for (String k : config.getKeys(true)) {
            if (!config.isConfigurationSection(k)) {
                this.put(k, config.getString(k));
            }
        }
    }
}
