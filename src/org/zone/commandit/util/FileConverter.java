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

public class FileConverter extends FileAdapter {
    
    public FileConverter(CommandIt plugin, String filename) {
        super(plugin, filename);
    }
    
    /**
     * Load CommandSigns from file
     */
    @Override
    public void load() {
        /*
         * Load file as a normal YAML block file but with different keys
         */
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), filename));
        Map<Location, LuaCode> loaded = new HashMap<Location, LuaCode>();
        
        ConfigurationSection data = config.getConfigurationSection("signs");
        if (data == null) {
            plugin.getLogger().info("No CommandSigns found.");
            cache = new HashMap<Location, LuaCode>();
        } else {
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
                        throw new IllegalArgumentException("World does not exist: " + locText[0] + ".");
                    x = Integer.parseInt(locText[1]);
                    y = Integer.parseInt(locText[2]);
                    z = Integer.parseInt(locText[3]);
                    loc = new Location(world, x, y, z);
                    
                    // Throws exception for an invalid location AND if the
                    // location is air
                    block = loc.getBlock().getTypeId();
                    if (block == 0)
                        throw new IllegalArgumentException("Location not valid: " + loc.toString() + ".");
                    
                    // Get attributes
                    String owner = data.getString(key + ".owner", null);
                    
                    LuaCode code = new LuaCode(owner);
                    for (Object o : data.getList(key + ".text", new ArrayList<String>())) {
                        code.addLine(o.toString());
                    }
                    
                    code.setEnabled(data.getBoolean(key + ".active", true));
                    
                    // Cooldowns as Player => Expiry (UNIX timestamp)
                    Map<String, Long> timeouts = code.getTimeouts();
                    ConfigurationSection cooldowns = data.getConfigurationSection(key + ".cooldowns");
                    if (cooldowns == null) {
                        cooldowns = data.createSection(key + "cooldowns");
                    }
                    for (String player : cooldowns.getKeys(false)) {
                        timeouts.put(player, cooldowns.getLong(player));
                    }
                    
                    loaded.put(loc, code);
                } catch (Exception ex) {
                    plugin.getLogger().warning("Unable to load CommandSign " + attempts + ".\n" + ex.getMessage());
                }
            }
    
            /*
             * Convert old style to Lua
             */
            for (Map.Entry<Location, LuaCode> entry : loaded.entrySet()) {
                entry.setValue(convertToLua(entry.getValue()));
            }
            
            cache = loaded;
            plugin.getLogger().info("Successfully loaded " + cache.size() + " old CommandSigns");
        }
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
            s = s.replace("<near>", "{getNearest().name}");
            s = s.replace("<display>", "{player.display}");
            s = s.replace("<money>", "{player.balance}");
            s = s.replace("<formatted>", "{player.money}");
            s = s.replace("<ip>", "{player.ip}");
        }
        return cst;
    }
}
