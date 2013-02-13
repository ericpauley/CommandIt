package org.zone.commandit.config;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.zone.commandit.util.DataAdapter;
import org.zone.commandit.util.LuaCode;

public class CommandBlocks implements Map<Location, LuaCode> {
    
    private DataAdapter adapter;
    
    public CommandBlocks(DataAdapter adapter) {
        this.adapter = adapter;
    }
    
    @Override
    public void clear() {
        adapter.clear();
    }
    
    @Override
    public boolean containsKey(Object key) {
        return adapter.containsKey(key);
    }
    
    @Override
    public boolean containsValue(Object value) {
        return adapter.containsValue(value);
    }
    
    @Override
    public Set<java.util.Map.Entry<Location, LuaCode>> entrySet() {
        return adapter.entrySet();
    }
    
    @Override
    public LuaCode get(Object key) {
        return adapter.get(key);
    }
    
    /**
     * Load command blocks from disk
     */
    public void load() {
        adapter.load();
    }
    
    @Override
    public boolean isEmpty() {
        return adapter.isEmpty();
    }
    
    @Override
    public Set<Location> keySet() {
        return adapter.keySet();
    }
    
    @Override
    public LuaCode put(Location key, LuaCode value) {
        return adapter.put(key, value);
    }
    
    @Override
    public void putAll(Map<? extends Location, ? extends LuaCode> m) {
        adapter.putAll(m);
    }
    
    @Override
    public LuaCode remove(Object key) {
        return adapter.remove(key);
    }
    
    /**
     * Save command blocks to disk
     */
    public void save() {
        adapter.save();
    }
    
    @Override
    public int size() {
        return adapter.size();
    }
    
    @Override
    public Collection<LuaCode> values() {
        return adapter.values();
    }
    
}
