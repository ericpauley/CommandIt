package org.zone.commandit.handler;

import java.io.IOException;
import java.util.Stack;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.zone.commandit.CommandIt;
import org.zone.commandit.python.util.PythonRunner;
import org.zone.commandit.util.PythonCode;

public class Executor {
    
    private final Action action;
    boolean isValid = false;
    
    private final Location location;
    
    private final Player player;
    
    private final CommandIt plugin;
    
    private final Stack<Boolean> restrictions = new Stack<Boolean>();
    
    private final PythonCode code;
    
    private double wait;
    
    public Executor(CommandIt plugin, Player player, Location location, Action action) {
        this.plugin = plugin;
        this.player = player;
        this.action = action;
        this.location = location;
        this.code = plugin.getCommandBlocks().get(location);
    }
    
    public Action getAction() {
        return action;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public CommandIt getPlugin() {
        return plugin;
    }
    
    public Stack<Boolean> getRestrictions() {
        return restrictions;
    }
    
    public PythonCode getCode() {
        return code;
    }
    
    public double getWait() {
        return wait;
    }
    
    public boolean run() {
        try {
			return PythonRunner.createRunner(code.toString(), "player", player).run();
		} catch (IOException e) {
			return false;
		}
    }
    
    public void setWait(double wait) {
        this.wait = wait;
    }
    
}