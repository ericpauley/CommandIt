package org.zone.commandit.lua.integration;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.zone.commandit.handler.CommandBlockInteractEvent;

import se.krka.kahlua.integration.annotations.LuaMethod;

public class IntegrationPlayer {
    
    protected CommandBlockInteractEvent event;
    protected Player player;
    protected Permission perms;
    public double x, y, z;
    
    /**
     * Create a new instance of a player for Lua interpretation.
     * @param e
     */
    public IntegrationPlayer(CommandBlockInteractEvent e) {
        event = e;
        player = e.getPlayer();
        perms = e.getPlugin().getPermissionHandler();
        x = player.getLocation().getX();
        y = player.getLocation().getY();
        z = player.getLocation().getZ();
    }
    
    /**
     * Create a new instance of a player for Lua interpretation.
     * @param e
     * @param player Player separate to the caller of the event
     */
    public IntegrationPlayer(CommandBlockInteractEvent e, Player player) {
        this(e);
        this.player = player;
    }
    
    /**
     * Send a message in chat from this player
     * @param message
     */
    @LuaMethod
    public void chat(String message) {
        player.chat(message);
    }
    
    /**
     * Non-Lua
     * @return Full location of the player
     */
    public Location getLocation() {
        return player.getLocation();
    }
    
    /**
     * Send command as Op
     * @param command
     */
    @LuaMethod
    public void op(String command) {
        if (perms.has(player, "commandit.use.op")) {
            if (!player.isOp()) {
                player.setOp(true);
                run(command);
                player.setOp(false);
            } else {
                run(command);
            }
        }
    }
    
    /**
     * Send standard command without permission modification
     * @param command
     */
    @LuaMethod
    public void run(String command) {
        if (perms.has(player, "commandit.use.regular")) {
            Bukkit.dispatchCommand(player, command);
        }
    }
    
    /**
     * Sends the player a message
     * 
     * @param message
     *            The message to send
     */
    @LuaMethod
    public void sendMessage(String message) {
        player.sendMessage(message);
    }
    
    /**
     * Send command with all permissions
     * @param command
     */
    @LuaMethod
    public void sudo(String command) {
        if (perms.has(player, "commandit.use.sudo")) {
            if (!perms.has(player, "*")) {
                perms.playerAddTransient(player, "*");
                run(command);
                perms.playerRemoveTransient(player, "*");
            } else {
                run(command);
            }
        }
    }
    
    /**
     * Teleports the player to a location
     * 
     * @param x
     *            The X coordinate to teleport the player to
     * @param y
     *            The Y coordinate to teleport the player to
     * @param z
     *            The Z coordinate to teleport the player to
     */
    @LuaMethod
    public void teleport(double x, double y, double z) {
        player.teleport(new Location(player.getWorld(), x, y, z));
    }
    
    /**
     * Teleports the player to a location
     * 
     * @param world
     *            The world to teleport the player to
     * @param x
     *            The X coordinate to teleport the player to
     * @param y
     *            The Y coordinate to teleport the player to
     * @param z
     *            The Z coordinate to teleport the player to
     */
    @LuaMethod
    public void teleport(String world, double x, double y, double z) {
        player.teleport(new Location(event.getPlugin().getServer().getWorld(world), x, y, z));
    }
}
