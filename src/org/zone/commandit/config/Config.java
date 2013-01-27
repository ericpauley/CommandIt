package org.zone.commandit.config;

import org.bukkit.configuration.Configuration;
import org.zone.commandit.CommandIt;
import org.zone.commandit.util.YamlLoader;

public class Config extends ConfigStore {

	public Config(CommandIt plugin) {
		super(plugin);
	}

	public void load() {
		Configuration config = YamlLoader.loadResource(plugin, "config.yml");

		for (String k : config.getKeys(true)) {
			if (!config.isConfigurationSection(k)) {
				this.put(k, config.getString(k));
			}
		}
	}

}
