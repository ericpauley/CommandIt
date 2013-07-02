package org.zone.commandit.python.integration;

import org.bukkit.World;

public class IntegrationTime {
    
    private long time;
    
    public IntegrationTime(World world) {
        time = world.getTime();
    }
    
    
    public long asTicks() {
        return time * 20;
    }
    
    
    public String toString() {
        long hours = Math.round(Math.floor(time / 1000));
        long minutes = Math.round(time % 1000);
        return hours + ":" + minutes;
    }
}
