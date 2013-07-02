package org.zone.commandit.lua.integration;

import org.bukkit.World;

import se.krka.kahlua.integration.annotations.LuaMethod;

public class IntegrationTime {
    
    private long time;
    
    public IntegrationTime(World world) {
        time = world.getTime();
    }
    
    @LuaMethod
    public long asTicks() {
        return time * 20;
    }
    
    @LuaMethod
    public String toString() {
        long hours = Math.round(Math.floor(time / 1000));
        long minutes = Math.round(time % 1000);
        return hours + ":" + minutes;
    }
}
