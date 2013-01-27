package org.zone.commandit.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.zone.commandit.CommandIt;

public class CodeLoader {

	CommandIt plugin;

	public CodeLoader(CommandIt plugin) {
		this.plugin = plugin;
	}

	public void loadFile() {
		plugin.getCodeBlocks().clear();
		FileConfiguration config = YamlConfiguration
				.loadConfiguration(new File(plugin.getDataFolder(), "signs.yml"));
		ConfigurationSection data = config.getConfigurationSection("signs");
		if (data == null) {
			plugin.getLogger().info("No signs found.");
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

	public void saveFile() {
		FileConfiguration config = new YamlConfiguration();
		ConfigurationSection data = config.createSection("signs");
		for (Map.Entry<Location, LuaCode> sign : plugin.getCodeBlocks().entrySet()) {
			Location loc = sign.getKey();
			LuaCode cst = sign.getValue();
			cst.trim();
			String key = loc.getWorld().getName() + "," + loc.getBlockX() + ","
					+ loc.getBlockY() + "," + loc.getBlockZ();
			ConfigurationSection signData = data.createSection(key);
			signData.set("redstone", cst.isRedstone());
			signData.set("owner", cst.getOwner());
			signData.set("text", cst.getText());
			signData.set("active", cst.isEnabled());
			signData.createSection("cooldowns", cst.getTimeouts());
			/*
			 * data.set(key + ".lastuse", cst.getLastUse()); data.set(key +
			 * ".numuses", cst.getNumUses()); List<String> useData = new
			 * ArrayList<String>(cst.getUses().size()); for (OfflinePlayer user
			 * : cst.getTimeouts().keySet()) { useData.add(user.getName() + ","
			 * + cst.getLastUse(user) + "," + cst.getUses(user)); } data.set(key
			 * + ".usedata", useData);
			 */
			try {
				config.save(new File(plugin.getDataFolder(), "signs.yml"));
				plugin.getLogger().info(
						plugin.getCodeBlocks().size() + " signs saved");
			} catch (IOException e) {
				plugin.getLogger().severe("Failed to save CommandIt");
				e.printStackTrace();
			}
		}
	}

	public void saveOldFile() {
		try {
			File file = new File(plugin.getDataFolder(), "signs.dat");
			if (!file.exists()) {
				plugin.getDataFolder().mkdir();
				file.createNewFile();
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			Location csl = null;
			String sep = "\u001D";
			String line = "";
			String commands = "";
			boolean first = true;
			int signNumber = 0;

			writer.write("");
			for (Map.Entry<Location, LuaCode> entry : plugin.getCodeBlocks()
					.entrySet()) {
				try {
					signNumber++;
					entry.getValue().trim();
					commands = "";
					for (String command : entry.getValue()) {
						if (!first)
							commands += "\u001E";
						commands += command;
						first = false;
					}
					csl = entry.getKey();
					line = csl.getWorld().getName();
					line += sep;
					line += csl.getBlockX();
					line += sep;
					line += csl.getBlockY();
					line += sep;
					line += csl.getBlockZ();
					line += sep;
					line += entry.getValue().getOwner();
					line += sep;
					line += commands;
					line += sep;
					line += entry.getValue().isRedstone();
					writer.write(line + "\n");
				} catch (Exception ex) {
					if (csl != null)
						plugin.getLogger().warning(
								"Unable to save sign #" + signNumber + " at "
										+ csl.toString());
					else
						plugin.getLogger().warning(
								"Unable to save sign #" + signNumber);
				}
			}
			writer.close();
		} catch (Exception ex) {
			plugin.getLogger().severe("Failed to save signs!");
			ex.printStackTrace();
		}
	}
}
