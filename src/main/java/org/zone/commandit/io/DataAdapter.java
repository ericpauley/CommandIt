package org.zone.commandit.io;

import java.util.Map;

import org.bukkit.Location;
import org.zone.commandit.util.Code;

public interface DataAdapter extends Map<Location, Code> {
    
    /**
     * Load command blocks from disk
     */
    public void load();
    
    /**
     * Save command blocks to disk
     */
    public void save();
    
}
