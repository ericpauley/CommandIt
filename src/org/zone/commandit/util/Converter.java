package org.zone.commandit.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.zone.commandit.CommandIt;

public class Converter extends CodeLoader {
	
	public Converter(CommandIt plugin) {
		super(plugin);
	}

	public void loadFile(String filename) {
		Map<Location, LuaCode> loaded = new HashMap<Location, LuaCode>();
		FileConfiguration config = YamlConfiguration
				.loadConfiguration(new File(filename));
		ConfigurationSection data = config.getConfigurationSection("signs");
		if (data == null) {
			plugin.getLogger().info("No old signs found.");
			return;
		}
		String[] locText;
		World world;
		int x, y, z, block;
		Location loc;
		int attempts = 0;
		for (String key : data.getKeys(false)) {
			try {
				attempts++;
				locText = key.split(",");
				world = Bukkit.getWorld(locText[0]);
				if (world == null)
					continue;
				x = Integer.parseInt(locText[1]);
				y = Integer.parseInt(locText[2]);
				z = Integer.parseInt(locText[3]);
				loc = new Location(world, x, y, z);

				// Throws exception for an invalid location AND if the
				// location is air
				block = loc.getBlock().getTypeId();
				if (block == 0)
					throw new IllegalArgumentException("Location not valid: "
							+ loc.toString() + ".");

				boolean redstone = data.getBoolean(key + ".redstone", false);
				String owner = data.getString(key + ".owner", null);
				LuaCode cst = new LuaCode(owner, redstone);
				for (Object o : data.getList(key + ".text",
						new ArrayList<String>())) {
					cst.addLine(o.toString());
				}
				cst.setEnabled(data.getBoolean(key + ".active", true));
				Map<String, Long> timeouts = cst.getTimeouts();
				ConfigurationSection cooldowns = data
						.getConfigurationSection(key + ".cooldowns");
				if (cooldowns == null) {
					cooldowns = data.createSection(key + "cooldowns");
				}
				for (String subKey : cooldowns.getKeys(false)) {
					timeouts.put(subKey, cooldowns.getLong(subKey));
				}
				/*
				 * cst.setLastUse(data.getLong(key + ".lastuse", 0));
				 * cst.setNumUses(data.getLong(key + ".numuses", 0)); for
				 * (Object useData : data.getList(key + ".usedata", new
				 * ArrayList<String>())) { String[] sections =
				 * useData.toString().split(","); OfflinePlayer user =
				 * Bukkit.getOfflinePlayer(sections[0]); long lastUse =
				 * Long.parseLong(sections[1]); long numUses =
				 * Long.parseLong(sections[2]); cst.getTimeouts().put(user,
				 * lastUse); cst.getUses().put(user, numUses); }
				 */
				plugin.getCodeBlocks().put(loc, cst);
			} catch (Exception ex) {
				plugin.getLogger().warning(
						"Unable to load sign " + attempts + " in signs.yml. "
								+ ex.getMessage());
				ex.printStackTrace();
			}
		}
		plugin.getLogger().info(
				"Successfully loaded " + plugin.getCodeBlocks().size() + " signs");
	}
}
