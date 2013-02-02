package org.zone.commandit.util;

import java.io.File;
import java.io.IOException;
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

public class FileLoader {
    
    protected CommandIt plugin;
    
    public FileLoader(CommandIt plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Load command blocks from files
     */
    public Map<Location, LuaCode> load(String filename) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + filename));
        return load(config);
    }
    
    /**
     * Save command blocks to file
     */
    public void save(Map<Location, LuaCode> cache, String filename) {
        FileConfiguration config = new YamlConfiguration();
        ConfigurationSection data = config.createSection("blocks");
        
        for (Map.Entry<Location, LuaCode> entry : cache.entrySet()) {
            Location loc = entry.getKey();
            LuaCode code = entry.getValue();
            code.trim();
            
            String key = loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
            
            ConfigurationSection block = data.createSection(key);
            block.set("owner", code.getOwner());
            block.set("code", code.getLines());
            block.set("active", code.isEnabled());
            block.createSection("cooldowns", code.getTimeouts());
            
            try {
                config.save(new File(plugin.getDataFolder(), filename));
                plugin.getLogger().info(plugin.getCodeBlocks().size() + " command blocks saved");
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to save CommandIt");
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Worker for loading to files
     */
    protected Map<Location, LuaCode> load(FileConfiguration file) {
        Map<Location, LuaCode> loaded = new HashMap<Location, LuaCode>();
        
        ConfigurationSection data = file.getConfigurationSection("blocks");
        if (data == null) {
            plugin.getLogger().info("No command blocks found.");
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
                for (Object o : data.getList(key + ".code", new ArrayList<String>())) {
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
                
                plugin.getCodeBlocks().put(loc, code);
            } catch (Exception ex) {
                plugin.getLogger().warning("Unable to load command block " + attempts + ". " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        plugin.getLogger().info("Successfully loaded " + plugin.getCodeBlocks().size() + " command blocks");
        return loaded;
    }
}
