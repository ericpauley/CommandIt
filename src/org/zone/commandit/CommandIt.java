package org.zone.commandit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandIt extends JavaPlugin {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return true;
    }
    
    @Override
    public void onDisable() {
        
    }
    
    @Override
    public void onEnable() {
        
    }
    
}
