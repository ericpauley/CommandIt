package org.zone.commandit.config;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.zone.commandit.util.LuaCode;

public class SqlBlocks implements Map<Location, LuaCode> {
    
    @Override
    public void clear() {
        /*
         * TRUNCATE TABLE blocks, code, cooldowns;
         */
    }
    
    @Override
    public boolean containsKey(Object key) {
        /*
         * SELECT * FROM blocks
         * WHERE location = 'key'
         * LIMIT 1
         */
        return false;
    }
    
    @Override
    public boolean containsValue(Object value) {
        /*
         * Too hideous
         */
        return false;
    }
    
    @Override
    public Set<java.util.Map.Entry<Location, LuaCode>> entrySet() {
        /*
         * SELECT * FROM blocks
         * INNER JOIN code
         *      ON blocks.location = code.location
         * INNER JOIN cooldowns
         *      ON blocks.location = cooldowns.location
         */
        return null;
    }
    
    @Override
    public LuaCode get(Object key) {
        /*
         * SELECT * FROM blocks
         * WHERE blocks.location = 'key'
         * INNER JOIN code
         *      ON blocks.location = code.location
         * INNER JOIN cooldowns
         *      ON blocks.location = cooldowns.location
         */
        return null;
    }
    
    @Override
    public boolean isEmpty() {
        /*
         * SELECT * FROM blocks
         */
        return false;
    }
    
    @Override
    public Set<Location> keySet() {
        /*
         * SELECT location FROM blocks
         * INNER JOIN code
         *      ON blocks.location = code.location
         * INNER JOIN cooldowns
         *      ON blocks.location = cooldowns.location
         */
        return null;
    }
    
    @Override
    public LuaCode put(Location key, LuaCode value) {
        /*
         * INSERT INTO blocks VALUES(
         *      key,
         *      value.getOwner,
         *      value.isEnabled
         * );
         * for (String line : value.getLines()) {
         *      INSERT INTO code VALUES(
         *          key,
         *          line
         *      );
         * }
         * for (Entry<String, Long> timeout : value.getTimeouts()) {
         *      INSERT INTO code VALUES(
         *          key,
         *          timeout.getLey(),
         *          timeout.getValue()
         *      );
         * }
         */
        return null;
    }
    
    @Override
    public void putAll(Map<? extends Location, ? extends LuaCode> m) {
        for (Entry<? extends Location, ? extends LuaCode> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }
    
    @Override
    public LuaCode remove(Object key) {
        /*
         * DELETE FROM blocks
         * WHERE location = key;
         */
        return null;
    }
    
    @Override
    public int size() {
        /*
         * SELECT COUNT(location) FROM blocks
         */
        return 0;
    }
    
    @Override
    public Collection<LuaCode> values() {
        /*
         * SELECT * FROM blocks
         * WHERE blocks.location = 'key'
         * INNER JOIN code
         *      ON blocks.location = code.location
         * INNER JOIN cooldowns
         *      ON blocks.location = cooldowns.location
         */
        return null;
    }
    
}