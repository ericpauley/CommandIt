package org.zone.commandit.listener;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.zone.commandit.CommandIt;
import org.zone.commandit.config.M;
import org.zone.commandit.handler.ClickHandler;
import org.zone.commandit.util.Updater;

public class EventListener implements Listener {
    
    private CommandIt plugin;
    
    public EventListener(CommandIt plugin) {
        this.plugin = plugin;
    }
    
    /*public void handleRedstone(Block b) {
        Location csl = b.getLocation();
        LuaCode text = plugin.getCodeBlocks().get(csl);
        if (text != null && text.isRedstone()) {
            new Executor(plugin, null, csl, null).run();
        }
    }*/
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Location location = event.getBlock().getLocation();
        if (plugin.getCodeBlocks().containsKey(location)) {
            M.sendMessage(event.getPlayer(), "failure.remove_first");
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        Block block = null;
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK || action == Action.PHYSICAL) {
            block = event.getClickedBlock();
            if (block != null) {
                final ClickHandler signClickEvent = new ClickHandler(plugin, event.getPlayer(), block);
                if (signClickEvent.onInteract(action) && action != Action.PHYSICAL) {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.hasPermission(event.getPlayer(), "CommandIt.update", false)) {
            Updater u = plugin.getUpdater();
            if (u.getAvailableVersion().getBuild() > u.getCurrentVersion().getBuild()) {
                M.sendMessage(event.getPlayer(), "update.notify", u.getAvailableVersion().toString());
            }
        }
    }
    
    /*@EventHandler
    public void onRedstoneChange(BlockRedstoneEvent event) {
        if (event.getNewCurrent() != 0 && event.getOldCurrent() == 0) {
            Block b = event.getBlock();
            handleRedstone(b);
            handleRedstone(b.getRelative(BlockFace.NORTH));
            handleRedstone(b.getRelative(BlockFace.SOUTH));
            handleRedstone(b.getRelative(BlockFace.EAST));
            handleRedstone(b.getRelative(BlockFace.WEST));
            handleRedstone(b.getRelative(BlockFace.UP));
            handleRedstone(b.getRelative(BlockFace.DOWN));
        }
    }*/
}
