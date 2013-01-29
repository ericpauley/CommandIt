package org.zone.commandit.handler;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.zone.commandit.CommandIt;
import org.zone.commandit.util.LuaCode;
import org.zone.commandit.util.PlayerState;

public class ClickHandler {
    
    private Location location;
    private Player player;
    private CommandIt plugin;
    
    public ClickHandler(CommandIt plugin, Player player, Block block) {
        this.plugin = plugin;
        this.player = player;
        location = block.getLocation();
    }
    
    public void copySign() {
        LuaCode code = plugin.getCodeBlocks().get(location);
        if (code == null) {
            plugin.getMessenger().sendMessage(player, "failure.not_a_sign");
            return;
        }
        LuaCode clone = plugin.getCodeBlocks().get(location).clone(player.getName());
        plugin.getPlayerCode().put(player, clone);
        readSign(true);
        plugin.getMessenger().sendMessage(player, "success.copied");
        plugin.getPlayerStates().put(player, PlayerState.ENABLE);
    }
    
    public void createSign(boolean batch) {
        if (plugin.getCodeBlocks().containsKey(location)) {
            plugin.getMessenger().sendMessage(player, "failure.already_enabled");
            return;
        }
        LuaCode code = plugin.getPlayerCode().get(player);
        
        try {
            code.trim();
            plugin.getCodeBlocks().put(location, code.clone(player.getName()));
            plugin.getMessenger().sendMessage(player, "success.enabled");
        } catch (Exception e) {
            plugin.getMessenger().sendMessage(player, "failure.wrong_syntax");
        }
        
        if (!batch) {
            plugin.getPlayerStates().remove(player);
            plugin.getPlayerCode().remove(player);
        }
    }
    
    public void editSign() {
        LuaCode code = plugin.getCodeBlocks().get(location);
        if (code == null) {
            plugin.getMessenger().sendMessage(player, "failure.not_a_sign");
            return;
        }
        plugin.getMessenger().sendMessage(player, "progress.edit_started");
        plugin.getPlayerCode().put(player, code);
        plugin.getPlayerStates().put(player, PlayerState.EDIT);
    }
    
    public void insert(boolean batch) {
        LuaCode currentText = plugin.getCodeBlocks().get(location);
        if (currentText == null) {
            plugin.getMessenger().sendMessage(player, "failure.not_a_sign");
            return;
        }
        LuaCode newText = plugin.getPlayerCode().get(player);
        
        // Insert lines from last to first - that way you don't overwrite stuff
        for (int i = newText.count(); i >= 1; i--) {
            // Move all lines after the current position up one place
            for (int j = currentText.count(); j >= i; j--) {
                currentText.setLine(j + 1, currentText.getLine(j));
            }
            currentText.setLine(i, newText.getLine(i));
        }
        currentText.trim();
        
        plugin.getMessenger().sendMessage(player, "success.done_editing");
        if (!batch) {
            plugin.getPlayerStates().remove(player);
            plugin.getPlayerCode().remove(player);
        }
    }
    
    public boolean onInteract(Action action) {
        PlayerState state = plugin.getPlayerStates().get(player);
        if (state != null) {
            switch (state) {
                case ENABLE:
                    createSign(false);
                    break;
                case BATCH_ENABLE:
                    createSign(true);
                    break;
                case INSERT:
                    insert(false);
                    break;
                case BATCH_INSERT:
                    insert(true);
                    break;
                case REMOVE:
                    removeSign(false);
                    break;
                case BATCH_REMOVE:
                    removeSign(true);
                    break;
                case READ:
                    readSign(false);
                    break;
                case BATCH_READ:
                    readSign(true);
                    break;
                case COPY:
                    copySign();
                    break;
                case EDIT_SELECT:
                    editSign();
                    readSign(true);
                    break;
                case TOGGLE:
                    toggleSign(false);
                    break;
                case BATCH_TOGGLE:
                    toggleSign(true);
                    break;
                case REDSTONE:
                    redstoneToggle(false);
                    break;
                case BATCH_REDSTONE:
                    redstoneToggle(true);
                    break;
                default:
                    return new Executor(plugin, player, location, action).run();
            }
            return true;
        } else {
            return new Executor(plugin, player, location, action).run();
        }
    }
    
    public void readSign(boolean batch) {
        LuaCode code = plugin.getCodeBlocks().get(location);
        if (code == null) {
            plugin.getMessenger().sendMessage(player, "failure.not_a_sign");
            return;
        }
        int i = 1;
        for (String line : code) {
            if (!line.equals("")) {
                plugin.getMessenger().sendRaw(player, "success.line_print", "" + i, line);
            }
            i++;
        }
        if (!batch)
            plugin.getPlayerStates().remove(player);
    }
    
    public void redstoneToggle(boolean batch) {
        if (!plugin.getCodeBlocks().containsKey(location)) {
            plugin.getMessenger().sendMessage(player, "failure.not_a_sign");
            return;
        }
        LuaCode code = plugin.getCodeBlocks().get(location);
        plugin.getCodeBlocks().remove(location);
        boolean enabled = code.isRedstone();
        if (enabled) {
            code.setRedstone(false);
            plugin.getCodeBlocks().put(location, code);
            plugin.getMessenger().sendMessage(player, "success.redstone_disabled");
        } else {
            code.setRedstone(true);
            plugin.getCodeBlocks().put(location, code);
            plugin.getMessenger().sendMessage(player, "success.redstone_enabled");
        }
        if (!batch)
            plugin.getPlayerStates().remove(player);
    }
    
    public void removeSign(boolean batch) {
        if (!plugin.getCodeBlocks().containsKey(location)) {
            plugin.getMessenger().sendMessage(player, "failure.not_a_sign");
            return;
        }
        plugin.getCodeBlocks().remove(location);
        plugin.getMessenger().sendMessage(player, "success.removed");
        if (!batch) {
            if (plugin.getPlayerCode().containsKey(player)) {
                plugin.getPlayerStates().put(player, PlayerState.ENABLE);
                plugin.getMessenger().sendMessage(player, "information.code_in_clipboard");
            } else {
                plugin.getPlayerStates().remove(player);
            }
        }
    }
    
    public void toggleSign(boolean batch) {
        if (!plugin.getCodeBlocks().containsKey(location)) {
            plugin.getMessenger().sendMessage(player, "failure.not_a_sign");
            return;
        }
        LuaCode code = plugin.getCodeBlocks().get(location);
        plugin.getCodeBlocks().remove(location);
        boolean enabled = code.isEnabled();
        if (enabled) {
            code.setEnabled(false);
            plugin.getCodeBlocks().put(location, code);
            plugin.getMessenger().sendMessage(player, "success.disabled");
        } else {
            code.setEnabled(true);
            plugin.getCodeBlocks().put(location, code);
            plugin.getMessenger().sendMessage(player, "success.enabled");
        }
        if (!batch)
            plugin.getPlayerStates().remove(player);
    }
}
