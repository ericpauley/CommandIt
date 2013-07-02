package org.zone.commandit.lua.integration;

import java.util.HashMap;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.zone.commandit.handler.CommandBlockInteractEvent;
import org.zone.commandit.util.Message;

import se.krka.kahlua.integration.annotations.LuaMethod;

public class IntegrationPlayer {
    
    protected CommandBlockInteractEvent event;
    protected Player player;
    protected Economy econ;
    protected Permission perms;
    public String name;
    public String display;
    public String ip;
    public double x, y, z;
    public String world;
    
    /**
     * Create a new instance of a player for Lua interpretation.
     * 
     * @param e
     */
    public IntegrationPlayer(final CommandBlockInteractEvent e) {
        event = e;
        player = e.getPlayer();
        econ = e.getPlugin().getEconomy();
        perms = e.getPlugin().getPermissionHandler();
        x = player.getLocation().getX();
        y = player.getLocation().getY();
        z = player.getLocation().getZ();
    }
    
    /**
     * Create a new instance of a player for Lua interpretation.
     * 
     * @param e
     * @param player Player separate to the caller of the event
     */
    public IntegrationPlayer(CommandBlockInteractEvent e, Player player) {
        this(e);
        this.player = player;
    }
    
    /**
     * Non-Lua
     * @return Full location of the player
     */
    public Location getLocation() {
        return player.getLocation();
    }
    
    /**
     * Gives item(s) to the player's inventory
     * 
     * @param item Item name
     * @param quanity Amount to give
     */
    @LuaMethod
    public void giveItem(String item, int quantity) {
        // TODO Implement giveItem
    }
    
    /**
     * Checks if the player has a given item
     * 
     * @param item Item name
     * @return Quantity in inventory
     */
    @LuaMethod
    public int hasItem(String item) {
        int count = 0;
        for (ItemStack invItem : player.getInventory()) {
            if (invItem.getType().name().equals(item)) {
                count += invItem.getAmount();
            }
        }
        return count;
    }
    
    /**
     * Checks if the player has a given permission
     * 
     * @param permission Permission node in dotted format
     */
    @LuaMethod
    public boolean hasPerm(String permission) {
        return perms.has(player, permission);
    }
    
    /**
     * Checks if the player is in the given group
     * 
     * @param group Group name
     */
    @LuaMethod
    public boolean inGroup(String group) {
        return inGroup(group, false);
    }
    
    /**
     * Checks if the player is in the given group, inheritance sensitive
     * 
     * @param group Group name
     * @param inherited True if inherting a group counts as being in the group
     */
    @LuaMethod
    public boolean inGroup(String group, boolean inherited) {
        // TODO Implement inheritance once tested
        return perms.playerInGroup(player, group);
    }
    
    /**
     * Send command as Op
     * 
     * @param command
     */
    @LuaMethod
    public void op(String command) {
        op(command, true);
    }
    
    /**
     * Send command as Op
     * 
     * @param command
     * @param visible Set to false if output from the command shouldn't be sent back to the user
     */
    @LuaMethod
    public void op(String command, boolean visible) {
        if (perms.has(player, "commandit.use.op")) {
            if (!player.isOp()) {
                player.setOp(true);
                run(command, visible);
                player.setOp(false);
            } else {
                run(command, visible);
            }
        }
    }
    
    /**
     * Send standard command without permission modification
     * 
     * @param command
     */
    @LuaMethod
    public void run(String command) {
        run(command, true);
    }
    
    /**
     * Send standard command without permission modification
     * 
     * @param command
     * @param visible Set to false if output from the command shouldn't be sent back to the user
     */
    @LuaMethod
    public void run(String command, boolean visible) {
        try {
            if (perms.has(player, "commandit.use.regular")) {
                CommandSender sender = player;
                if (!visible) sender = new SilentCommandSender().emulate(player);
                Bukkit.dispatchCommand(sender, command);
            }
        } catch (Exception ex) {
            Message.severe("command_error", command, ex.getMessage());
        }
    }
    
    /**
     * Send a message in chat from this player
     * 
     * @param message
     */
    @LuaMethod
    public void say(String message) {
        player.chat(message);
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
     * 
     * @param command
     */
    @LuaMethod
    public void sudo(String command) {
        sudo(command, true);
    }
    
    /**
     * Send command with all permissions
     * 
     * @param command
     * @param visible Set to false if output from the command shouldn't be sent back to the user
     */
    @LuaMethod
    public void sudo(String command, boolean visible) {
        if (perms.has(player, "commandit.use.sudo")) {
            if (!perms.has(player, "*")) {
                perms.playerAddTransient(player, "*");
                run(command, visible);
                perms.playerRemoveTransient(player, "*");
            } else {
                run(command, visible);
            }
        }
    }
    
    /**
     * Removes item(s) from player's inventory
     * 
     * @param item Item name
     * @param quanity Amount to take
     */
    @LuaMethod
    public void takeItem(String item, int quantity) {
        // Get all items with the given name
        HashMap<Integer, ? extends ItemStack> items = player.getInventory().all(Material.getMaterial(item));
        int i = 0;
        int taken = 0;
        int toTake = quantity;
        // Keep looping through the inventory until enough has been taken
        while (taken < quantity && i < items.size()) {
            int amount = items.get(i).getAmount();
            if (toTake > amount) {
                items.get(i).setAmount(0);
                toTake -= amount;
            } else {
                items.get(i).setAmount(toTake - amount);
                toTake = 0;
            }
        }
    }
    
    /**
     * Take money from the player's balance
     * 
     * @param amount
     */
    @LuaMethod
    public void takeMoney(int amount) {
        econ.withdrawPlayer(player.getName(), amount);
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
