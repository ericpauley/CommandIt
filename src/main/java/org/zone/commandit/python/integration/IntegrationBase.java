package org.zone.commandit.python.integration;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.block.Action;
import org.zone.commandit.handler.CommandBlockInteractEvent;

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
    public IntegrationBase(final CommandBlockInteractEvent e) {
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
     * 
     * @param command
     */
    
    public void console(String command) {
        server.run(command);
    }
    
    /**
     * Delay script execution
     * 
     * @param delay
     */
    
    public void delay(int delay) {
        // TODO Implement delay
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
    
    public IntegrationPlayer getPlayer() {
        return player;
    }
    
    /**
     * @return Server instance on which the interaction occurred
     */
    
    public IntegrationServer getServer() {
        return server;
    }
    
    /**
     * @return Current time in this world
     */
    
    public IntegrationTime getTime() {
        return new IntegrationTime(Bukkit.getWorld(world));
    }
    
    /**
     * @return True if block was left-clicked
     */
    
    public boolean isLeftClick() {
        return event.getAction() == Action.LEFT_CLICK_BLOCK;
    }
    
    /**
     * @return True if block was activated by physical interaction
     */
    
    public boolean isPhysical() {
        return event.getAction() == Action.PHYSICAL;
    }
    
    /**
     * @return True if block was left-clicked
     */
    
    public boolean isRedstone() {
        // TODO Implement isRedstone
        // return event.getAction() == CommandBlockAction.REDSTONE;
        return false;
    }
    
    /**
     * @return True if block was right-clicked
     */
    
    public boolean isRightClick() {
        return event.getAction() == Action.RIGHT_CLICK_BLOCK;
    }
    
    /**
     * Send command as Op
     * 
     * @param command
     */
    
    public void op(String command) {
        op(command, true);
    }
    
    /**
     * Send command as Op
     * 
     * @param command
     * @param visible Set to false if output from the command shouldn't be sent back to the user
     */
    
    public void op(String command, boolean visible) {
        player.op(command, visible);
    }
    
    /**
     * Get a random number between 0 and 1
     */
    
    public double random() {
        return Math.random();
    }
    
    /**
     * Get a random number between min and max
     * 
     * @param min
     * @param max
     */
    
    public double random(double min, double max) {
        return (Math.random() * (max - min)) + min;
    }
    
    /**
     * Get a random integer between min and max
     * @param min
     * @param max
     */
    
    public int random(int min, int max) {
        return (int) (Math.round(random((double) min, (double) max)));
    }
    
    /**
     * Get a random location on the map
     */
    
    public String randomLoc() {
        int maxX = 2000;
        int maxY = 255;
        int maxZ = 2000;
        
        // If WorldBorder is installed, use it to get the maximum world size
        /*
        if (Bukkit.getPluginManager().isPluginEnabled("WorldBorder")) {
            WorldBorder wb = new WorldBorder();
            BorderData b = wb.GetWorldBorder(world);
            maxX = (int) b.getX();
            maxZ = (int) b.getZ();
        }
        */
        
        return randomLoc(maxX, maxY, maxZ);
    }
    
    /**
     * Get a random location on the map within a sphere of the player
     * 
     * @param distance Farthest magnitude of distance from player
     */
    
    public String randomLoc(double distance) {
        /*
         * Warning: here be real maths - it takes a bit longer to compute Note:
         * x and z take precedence over y in terms of randomness probability
         */
        
        // Equation of Sphere is (x-a)^2 + (y-b)^2 + (z-c)^2 = r^2
        // Take random x, a-r <= x <= a+r
        double x = random(player.x - distance, player.x + distance);
        // (y-b)^2 + (z-c)^2 = r^2 - (x-a)^2 = s^2
        distance = Math.sqrt(Math.pow(distance, 2) - Math.pow(x - player.x, 2));
        // Take random z, c-s <= z <= c+s
        double z = random(player.z - distance, player.z + distance);
        // (y-b)^2 = s^2 - (z-c)^2 = t^2
        distance = Math.sqrt(Math.pow(distance, 2) - Math.pow(z - player.z, 2));
        // Take random y, b-t <= y <= b+t
        double y = random(player.y - distance, player.y + distance);
        
        return x + " " + y + " " + z;
    }
    
    /**
     * Get a random location on the map
     * 
     * @param maxX Maximum x coordinate (minimum = -x)
     * @param maxY Maximum y coordinate
     * @param maxZ Maximum z coordinate (minimum = -z)
     */
    
    public String randomLoc(int maxX, int maxY, int maxZ) {
        return randomLoc(-maxX, maxX, 0, maxY, -maxZ, maxZ);
    }
    
    /**
     * Get a random location on the map
     * 
     * @param minX Minimum x coordinate
     * @param maxX Maximum x coordinate
     * @param minY Minimum y coordinate
     * @param maxY Maximum y coordinate
     * @param maxZ Minimum z coordinate
     * @param maxZ Maximum z coordinate
     */
    
    public String randomLoc(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        int x = random(minX, maxX);
        int y = random(minY, maxY);
        int z = random(minZ, maxZ);
        
        return x + " " + y + " " + z;
    }
    
    /**
     * Get a random player on the server
     */
    
    public IntegrationPlayer randomPlayer() {
        IntegrationPlayer[] p = server.getPlayers();
        int r = random(0, p.length - 1);
        return p[r];
    }
    
    /**
     * Send standard command without permission modification
     * 
     * @param command
     */
    
    public void run(String command) {
        run(command, true);
    }
    
    /**
     * Send standard command without permission modification
     * 
     * @param command
     * @param visible Set to false if output from the command shouldn't be sent back to the user
     */
    
    public void run(String command, boolean visible) {
        player.run(command, visible);
    }
    
    /**
     * Send command with all permissions
     * 
     * @param command
     */
    
    public void sudo(String command) {
        sudo(command, true);
    }
    
    /**
     * Send command with all permissions
     * 
     * @param command
     * @param visible Set to false if output from the command shouldn't be sent back to the user
     */
    
    public void sudo(String command, boolean visible) {
        player.sudo(command, visible);
    }
    
    /**
     * Send text to the player
     * 
     * @param message
     */
    
    public void text(String message) {
        player.sendMessage(message);
    }
    
}
