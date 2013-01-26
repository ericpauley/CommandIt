package org.zone.commandit.lua.integration;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.zone.commandit.lua.util.LuaUtil;

import se.krka.kahlua.vm.KahluaTable;

public class IntegrationPlayer {
    
    private Player p;
    
    public IntegrationPlayer(Player p) {
        this.p = p;
    }
    
    public void sendMessage(String message) {
        p.sendMessage(message);
    }
    
    public void teleport(double x, double y, double z) {
        p.teleport(new Location(p.getWorld(), x, y, z));
    }
    
    public void teleport(String world, double x, double y, double z) {
        p.teleport(new Location(p.getServer().getWorld(world), x, y, z));
    }
    
    public void chat(String message) {
        p.chat(message);
    }
    
    public void runCommand(String message) {
        chat(message);
    }
    
    public KahluaTable getServer(){
        return LuaUtil.expose(new IntegrationServer(p.getServer()));
    }
    
}
