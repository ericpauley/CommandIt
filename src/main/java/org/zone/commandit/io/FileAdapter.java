package org.zone.commandit.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.zone.commandit.CommandIt;
import org.zone.commandit.util.Code;

public class FileAdapter implements DataAdapter {
    
    protected CommandIt plugin;
    protected Map<Location, Code> cache;
    protected String filename;
    
    public FileAdapter(CommandIt plugin, String filename) {
        this.plugin = plugin;
        this.filename = filename;
    }
    
    @Override
    public void clear() {
        if (cache == null)
            load();
        cache.clear();
    }
    
    @Override
    public boolean containsKey(Object key) {
        if (cache == null)
            load();
        return cache.containsKey(key);
    }
    
    @Override
    public boolean containsValue(Object value) {
        if (cache == null)
            load();
        return cache.containsValue(value);
    }
    
    @Override
    public Set<java.util.Map.Entry<Location, Code>> entrySet() {
        if (cache == null)
            load();
        return cache.entrySet();
    }
    
    @Override
    public Code get(Object key) {
        if (cache == null)
            load();
        return cache.get(key);
    }
    
    @Override
    public boolean isEmpty() {
        if (cache == null)
            load();
        return cache.isEmpty();
    }
    
    @Override
    public Set<Location> keySet() {
        if (cache == null)
            load();
        return cache.keySet();
    }
    
    @Override
    public Code put(Location key, Code value) {
        if (cache == null)
            load();
        return cache.put(key, value);
    }
    
    @Override
    public void putAll(Map<? extends Location, ? extends Code> m) {
        if (cache == null)
            load();
        cache.putAll(m);
    }
    
    @Override
    public Code remove(Object key) {
        if (cache == null)
            load();
        return cache.remove(key);
    }
    
    @Override
    public int size() {
        if (cache == null)
            load();
        return cache.size();
    }
    
    @Override
    public Collection<Code> values() {
        if (cache == null)
            load();
        return cache.values();
    }
    
    @Override
    public void load() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), filename));
        Map<Location, Code> loaded = new HashMap<Location, Code>();
        
        ConfigurationSection data = config.getConfigurationSection("blocks");
        if (data == null) {
            plugin.getLogger().info("No command blocks found.");
            cache = new HashMap<Location, Code>();
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
                    
                    Code code = new Code(owner);
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
                    
                    loaded.put(loc, code);
                } catch (Exception ex) {
                    plugin.getLogger().warning("Unable to load command block " + attempts + ".\n" + ex.getMessage());
                }
            }
            cache = loaded;
            plugin.getLogger().info("Successfully loaded " + cache.size() + " command blocks");
        }
    }
    
    @Override
    public void save() {
        FileConfiguration config = new YamlConfiguration();
        ConfigurationSection data = config.createSection("blocks");
        
        for (Map.Entry<Location, Code> entry : cache.entrySet()) {
            Location loc = entry.getKey();
            Code code = entry.getValue();
            code.trim();
            
            String key = loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
            
            ConfigurationSection block = data.createSection(key);
            block.set("owner", code.getOwner());
            block.set("code", code.getLines());
            block.set("active", code.isEnabled());
            block.createSection("cooldowns", code.getTimeouts());
            
        }
        
        try {
            config.save(new File(plugin.getDataFolder(), filename));
            plugin.getLogger().info(cache.size() + " command blocks saved");
        } catch (IOException ex) {
            plugin.getLogger().severe("Failed to save CommandIt");
            ex.printStackTrace();
        }
    }
}
