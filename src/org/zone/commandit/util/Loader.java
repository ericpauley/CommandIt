package org.zone.commandit.util;

import java.util.Map;

import org.bukkit.Location;

public interface Loader {
    
    public Map<Location, LuaCode> load(String filename);
    
    public void save(Map<Location, LuaCode> cache, String filename);
}
