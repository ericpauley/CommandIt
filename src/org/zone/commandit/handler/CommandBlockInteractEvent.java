package org.zone.commandit.handler;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.zone.commandit.CommandIt;

/**
 * An event that's triggered when a command block is clicked
 */
public class CommandBlockInteractEvent extends PlayerInteractEvent {
    
    protected CommandIt plugin;
    
    public CommandBlockInteractEvent(Player who, CommandIt plugin, Action action, ItemStack item, Block clickedBlock, BlockFace clickedFace) {
        super(who, action, item, clickedBlock, clickedFace);
        // TODO Auto-generated constructor stub
        // TODO Redstone event support
        this.plugin = plugin;
    }
    
    public CommandIt getPlugin() {
        return plugin;
    }
    
    @Override
    public HandlerList getHandlers() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
