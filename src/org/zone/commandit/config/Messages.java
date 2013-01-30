package org.zone.commandit.config;

import org.bukkit.configuration.Configuration;
import org.zone.commandit.CommandIt;
import org.zone.commandit.util.YamlLoader;

public class Messages extends ConfigStore {

	public Messages(CommandIt plugin) {
		super(plugin);
	}

	/**
	 * {@inheritDoc}
	 */
	public void load() {
		Configuration config = YamlLoader.loadResource(plugin, "messages.yml");

		for (String k : config.getKeys(true)) {
			if (!config.isConfigurationSection(k)) {
				this.put(k, config.getString(k));
			}
		}
	}
}
