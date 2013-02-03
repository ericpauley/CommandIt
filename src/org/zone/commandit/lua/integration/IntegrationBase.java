package org.zone.commandit.lua.integration;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.zone.commandit.handler.CommandBlockInteractEvent;

import se.krka.kahlua.integration.annotations.LuaMethod;

public class IntegrationBase {
    
    protected CommandBlockInteractEvent event;
    public IntegrationPlayer player;
    public IntegrationServer server;
    public int x, y, z;
    public String world;
    
    /**
     * Create a new base class for Lua interpretation.
     * @param e Event triggered by interaction with a command block
     */
    public IntegrationBase(CommandBlockInteractEvent e) {
        event = e;
        player = new IntegrationPlayer(e);
        server = new IntegrationServer(e);
        x = e.getClickedBlock().getX();
        y = e.getClickedBlock().getY();
        z = e.getClickedBlock().getZ();
        world = e.getClickedBlock().getWorld().getName();
    }
    
    /**
     * Send command as console
     * @param command
     */
    @LuaMethod
    public void console(String command) {
        server.run(command);
    }
    
    /**
     * Non-Lua
     * @return Location of the command block
     */
    public Location getLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }
    
    /**
     * @return Nearest player to command block
     */
    @LuaMethod
    public IntegrationPlayer getNearest() {
        // Get all players
        IntegrationPlayer[] online = server.getPlayers();
        
        // Set up holders for temporary data
        IntegrationPlayer nearest = null;
        double nearestDistance = 0;
        double currentDistance;
        
        // Get the current location
        Location here = getLocation();
        for (IntegrationPlayer p : online) {
            // See if the distance of this player is closer than the current
            // record
            if ((currentDistance = here.distanceSquared(p.getLocation())) < nearestDistance) {
                nearest = p;
                nearestDistance = currentDistance;
            }
        }
        
        return nearest;
    }
    
    /**
     * @return Player who interacted with the command block
     */
    @LuaMethod
    public IntegrationPlayer getPlayer() {
        return player;
    }
    
    /**
     * @return Server instance on which the interaction occurred
     */
    @LuaMethod
    public IntegrationServer getServer() {
        return server;
    }
    
    /**
     * @return Current time in this world
     */
    @LuaMethod
    public IntegrationTime getTime() {
        return new IntegrationTime(Bukkit.getWorld(world));
    }
    
    /**
     * Send command as Op
     * @param command
     */
    @LuaMethod
    public void op(String command) {
        player.op(command);
    }
    
    /**
     * Send standard command without permission modification
     * @param command
     */
    @LuaMethod
    public void run(String command) {
        player.run(command);
    }
    
    /**
     * Send command with all permissions
     * @param command
     */
    @LuaMethod
    public void sudo(String command) {
        player.sudo(command);
    }
    
}
