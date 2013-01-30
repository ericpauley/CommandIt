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
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(filename));
        
        // Rename signs.* to blocks.* and .text to .code
        for (String key : config.getKeys(true)) {
        	key = key.replace("signs", "blocks");
        	key = key.replace("text", "code");
        }
        
        return load(config);
    }
    
    /**
     * Converts old CommandSigns text to Lua equivalent
     * 
     * @param cst LuaCode text to be converted to Lua
     * @return
     */
    protected LuaCode convertToLua(LuaCode cst) {
        for (String s : cst) {
            // Restrictions
            
            // Commands
            
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
