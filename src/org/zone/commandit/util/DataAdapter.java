package org.zone.commandit.util;

import java.util.Map;

import org.bukkit.Location;


public interface DataAdapter extends Map<Location, LuaCode> {
    
    /**
     * Load command blocks from disk
     */
    public void load();
    
    /**
     * Save command blocks to disk
     */
    public void save();
    
}
