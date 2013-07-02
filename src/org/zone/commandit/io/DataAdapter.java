package org.zone.commandit.io;

import java.util.Map;

import org.bukkit.Location;
import org.zone.commandit.util.PythonCode;

public interface DataAdapter extends Map<Location, PythonCode> {
    
    /**
     * Load command blocks from disk
     */
    public void load();
    
    /**
     * Save command blocks to disk
     */
    public void save();
    
}
