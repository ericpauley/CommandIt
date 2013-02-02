package org.zone.commandit.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.zone.commandit.util.LuaCode;

/**
 * This class is pretty useless for the minute
 * but it might come in handy later if sorcery
 * is required.
 */
public class YamlBlocks implements Map<Location, LuaCode> {
    
    private Map<Location, LuaCode> data = new HashMap<Location, LuaCode>();
    
    @Override
    public void clear() {
        data.clear();
    }
    
    @Override
    public boolean containsKey(Object key) {
        return data.containsKey(key);
    }
    
    @Override
    public boolean containsValue(Object value) {
        return data.containsValue(value);
    }
    
    @Override
    public Set<java.util.Map.Entry<Location, LuaCode>> entrySet() {
        return data.entrySet();
    }
    
    @Override
    public LuaCode get(Object key) {
        return data.get(key);
    }
    
    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }
    
    @Override
    public Set<Location> keySet() {
        return data.keySet();
    }
    
    @Override
    public LuaCode put(Location key, LuaCode value) {
        return data.put(key, value);
    }
    
    @Override
    public void putAll(Map<? extends Location, ? extends LuaCode> m) {
        data.putAll(m);
    }
    
    @Override
    public LuaCode remove(Object key) {
        return data.remove(key);
    }
    
    @Override
    public int size() {
        return data.size();
    }
    
    @Override
    public Collection<LuaCode> values() {
        return data.values();
    }
    
}
