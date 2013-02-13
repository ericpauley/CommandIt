package org.zone.commandit.io;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.zone.commandit.CommandIt;
import org.zone.commandit.util.LuaCode;

public class SqlAdapter implements DataAdapter {
    
    protected CommandIt plugin;
    protected FileConfiguration config;
    protected Connection connection;
    
    public SqlAdapter(CommandIt plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }
    
    @Override
    public void clear() {
        if (connection == null)
            load();
        /*
         * TRUNCATE TABLE blocks, code, cooldowns;
         */
    }
    
    @Override
    public boolean containsKey(Object key) {
        if (connection == null)
            load();
        /*
         * SELECT * FROM blocks WHERE location = 'key' LIMIT 1
         */
        return false;
    }
    
    @Override
    public boolean containsValue(Object value) {
        if (connection == null)
            load();
        /*
         * Too hideous
         */
        return false;
    }
    
    @Override
    public Set<java.util.Map.Entry<Location, LuaCode>> entrySet() {
        if (connection == null)
            load();
        /*
         * SELECT * FROM blocks INNER JOIN code ON blocks.location =
         * code.location INNER JOIN cooldowns ON blocks.location =
         * cooldowns.location
         */
        return null;
    }
    
    @Override
    public LuaCode get(Object key) {
        if (connection == null)
            load();
        /*
         * SELECT * FROM blocks WHERE blocks.location = 'key' INNER JOIN code ON
         * blocks.location = code.location INNER JOIN cooldowns ON
         * blocks.location = cooldowns.location
         */
        return null;
    }
    
    @Override
    public boolean isEmpty() {
        if (connection == null)
            load();
        /*
         * SELECT * FROM blocks
         */
        return false;
    }
    
    @Override
    public Set<Location> keySet() {
        if (connection == null)
            load();
        /*
         * SELECT location FROM blocks INNER JOIN code ON blocks.location =
         * code.location INNER JOIN cooldowns ON blocks.location =
         * cooldowns.location
         */
        return null;
    }
    
    @Override
    public LuaCode put(Location key, LuaCode value) {
        if (connection == null)
            load();
        /*
         * INSERT INTO blocks VALUES( key, value.getOwner, value.isEnabled );
         * for (String line : value.getLines()) { INSERT INTO code VALUES( key,
         * line ); } for (Entry<String, Long> timeout : value.getTimeouts()) {
         * INSERT INTO code VALUES( key, timeout.getLey(), timeout.getValue() );
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
        if (connection == null)
            load();
        /*
         * DELETE FROM blocks WHERE location = key;
         */
        return null;
    }
    
    @Override
    public int size() {
        if (connection == null)
            load();
        /*
         * SELECT COUNT(location) FROM blocks
         */
        return 0;
    }
    
    @Override
    public Collection<LuaCode> values() {
        if (connection == null)
            load();
        /*
         * SELECT * FROM blocks WHERE blocks.location = 'key' INNER JOIN code ON
         * blocks.location = code.location INNER JOIN cooldowns ON
         * blocks.location = cooldowns.location
         */
        return null;
    }
    
    @Override
    public void load() {
        if (connection == null) {
            try {
                Properties connectionProps = new Properties();
                connectionProps.put("user", config.get("database.username"));
                connectionProps.put("password", config.get("database.password"));
                
                connection = DriverManager.getConnection("jdbc:" + config.get("database.url"), connectionProps);
            } catch (SQLException ex) {
                plugin.getLogger().severe("Unable to connect to database: " + ex.getMessage());
            }
        }
    }
    
    @Override
    public void save() {
        // No save required
    }
    
}
