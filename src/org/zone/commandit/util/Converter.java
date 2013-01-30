package org.zone.commandit.util;

import java.io.File;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.zone.commandit.CommandIt;

public class Converter extends FileLoader {

	public Converter(CommandIt plugin) {
		super(plugin);
	}

	/**
	 * Load CommandSigns from file
	 */
	public Map<Location, LuaCode> load(String filename) {
		FileConfiguration config = YamlConfiguration
				.loadConfiguration(new File(plugin.getDataFolder() + filename));

		// Rename signs.* to blocks.* and .text to .code
		for (String key : config.getKeys(true)) {
			key = key.replace("signs", "blocks");
			key = key.replace("text", "code");
		}

		Message.info("Attempting import of " + filename + "...");

		Map<Location, LuaCode> loaded = load(config);

		for (Map.Entry<Location, LuaCode> entry : loaded.entrySet()) {
			entry.setValue(convertToLua(entry.getValue()));
		}

		return loaded;
	}

	/**
	 * Converts old CommandSigns text to Lua equivalent
	 * 
	 * @param cst
	 *            LuaCode text to be converted to Lua
	 * @return
	 */
	protected LuaCode convertToLua(LuaCode cst) {
		for (String s : cst) {
			// Misc
			s = s.replace("{", "\\{");
			s = s.replace("}", "\\}");
			if (s.startsWith("!")) {
				s = s.replace("!", "} else {");
			}

			// Directives
			if (s.startsWith("\\")) {
				s = s.substring(1);
				s = "text(\"" + s + "\")";
				continue;
			} else if (s.startsWith(".")) {
				s = s.substring(1);
				s = "player.say(\"" + s + "\")";
				continue;
			} else if (s.startsWith("%")) {
				s = s.substring(1);
				s = "delay(" + s + ")";
				continue;
			} else if (s.startsWith("`")) {
				s = s.substring(1);
				s = "delay(random(0, " + s + "))";
				continue;
			}

			/*
			 * These strings are added to the final replacement strings rather
			 * than just using booleans.
			 */
			String visible = ")";
			String not = "";

			while (s.contains("-")) {
				s = s.replaceFirst("-", "}");
			}
			while (s.contains("?")) {
				s = s.replaceFirst("\\?", "");
				visible = ", false)";
			}
			while (s.contains("!")) {
				s = s.replaceFirst("\\!", "");
				not = "!";
			}

			// Restrictions
			String format = "if (" + not + "#) {";
			if (s.startsWith("@")) {
				s = s.substring(1);
				// Split multiple entries using commas
				String[] groups = s.split(",");
				String replacement = "";
				for (int i = 0; i < groups.length; i++) {
					replacement += "player.inGroup(\"" + groups[i] + "\")";
					// If not last, add the ' or ' connective
					if (i < groups.length)
						replacement += " or ";
				}
				s = format.replaceFirst("#", replacement);
				// We're done here, continue
				continue;
			} else if (s.startsWith("~")) {
				s = s.substring(1);
				s = format.replaceFirst("#", "player.timeout > " + s);
				continue;
			} else if (s.startsWith("$")) {
				s = s.substring(1);
				s = format.replaceFirst("#", "player.balance > " + s);
				continue;
			} else if (s.startsWith(">>")) {
				s = s.substring(1);
				s = format.replaceFirst("#", "isRightClick()");
				continue;
			} else if (s.startsWith("<<")) {
				s = s.substring(1);
				s = format.replaceFirst("#", "isLeftClick()");
				continue;
			} else if (s.startsWith("&")) {
				s = s.substring(1);
				String[] perms = s.split(",");
				String replacement = "";
				for (int i = 0; i < perms.length; i++) {
					replacement += "player.hasPerm(\"" + perms[i] + "\")";
					if (i < perms.length)
						replacement += " or ";
				}
				s = format.replaceFirst("#", replacement);
				continue;
			}

			// Commands
			if (s.startsWith("/")) {
				String command = "run";
				s = s.substring(1);

				if (s.startsWith("#")) {
					command = "console";
					s = s.substring(1);
				} else if (s.startsWith("^")) {
					command = "op";
					s = s.substring(1);
				} else if (s.startsWith("*")) {
					command = "super";
					s = s.substring(1);
				}

				s = command + "(\"" + s + "\"" + visible;
			}

			// Variables
			s = s.replace("<x>", "{player.x}");
			s = s.replace("<y>", "{player.y}");
			s = s.replace("<z>", "{player.z}");
			s = s.replace("<blockx>", "{x}");
			s = s.replace("<blocky>", "{y}");
			s = s.replace("<blockz>", "{z}");
			s = s.replace("<world>", "{world}");
			s = s.replace("<name>", "{player.name}");
			s = s.replace("<player>", "{player.name}");
			s = s.replace("<randomname>", "{randomPlayer()}");
			s = s.replace("<near>", "{nearest.name}");
			s = s.replace("<display>", "{player.display}");
			s = s.replace("<money>", "{player.balance}");
			s = s.replace("<formatted>", "{player.money}");
			s = s.replace("<ip>", "{player.ip}");
		}
		return cst;
	}
}
