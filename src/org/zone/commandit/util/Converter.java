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

	/**
	 * Load command blocks from file
	 * @return
	 */
	public Map<Location, LuaCode> loadFile(String filename) {
		Map<Location, LuaCode> loaded = new HashMap<Location, LuaCode>();
		
		FileConfiguration config = YamlConfiguration
				.loadConfiguration(new File(filename));
		ConfigurationSection data = config.getConfigurationSection("signs");
		if (data == null) {
			plugin.getLogger().info("No old CommandSigns found.");
			return null;
		}
		
		String[] locText;
		World world;
		int x, y, z, block;
		Location loc;
		int attempts = 0;
		
		for (String key : data.getKeys(false)) {
			try {
				// Attempts to count the number of entries in the file
				attempts++;
				
				// Decode location
				locText = key.split(",");
				world = Bukkit.getWorld(locText[0]);
				if (world == null)
					throw new IllegalArgumentException("World does not exist: "
							+ locText[0] + ".");
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

				// Get attributes
				String owner = data.getString(key + ".owner", null);

				LuaCode code = new LuaCode(owner);
				for (Object o : data.getList(key + ".text",
						new ArrayList<String>())) {
					code.addLine(o.toString());
				}
				code = convertToLua(code);
				
				code.setEnabled(data.getBoolean(key + ".active", true));
				
				// Cooldowns as Player => Expiry (UNIX timestamp)
				Map<String, Long> timeouts = code.getTimeouts();
				ConfigurationSection cooldowns = data
						.getConfigurationSection(key + ".cooldowns");
				if (cooldowns == null) {
					cooldowns = data.createSection(key + "cooldowns");
				}
				for (String player : cooldowns.getKeys(false)) {
					timeouts.put(player, cooldowns.getLong(player));
				}

				plugin.getCodeBlocks().put(loc, code);
			} catch (Exception ex) {
				plugin.getLogger().warning(
						"Unable to load CommandSign " + attempts + ". "
								+ ex.getMessage());
				ex.printStackTrace();
			}
		}
		plugin.getLogger().info(
				"Successfully loaded " + plugin.getCodeBlocks().size() + " old CommandSigns");
		return loaded;
	}
	
	/**
	 * Converts old CommandSigns text to Lua equivelant
	 * @param cst Text to be converted in a LuaCode object
	 * @return
	 */
	protected LuaCode convertToLua(LuaCode cst) {
		for (String s : cst) {
			// Restrictions
			
			// Commands
			
			// Variables
			s.replace("<player>", "getPlayer()");
		}
		return cst;
	}
}
