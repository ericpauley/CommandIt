package org.zone.commandit.lua.integration;

import org.bukkit.Server;

import se.krka.kahlua.integration.annotations.LuaMethod;

public class IntegrationServer {
    
    private Server s;
    
    public IntegrationServer(Server s) {
        this.s = s;
    }
    
    @LuaMethod
    public void shutdown() {
        s.shutdown();
    }
    @LuaMethod
    public void broadcast(String message) {
        s.broadcastMessage(message);
    }
    @LuaMethod
    public IntegrationPlayer[] getPlayers() {
        IntegrationPlayer[] players = new IntegrationPlayer[s.getOnlinePlayers().length];
        for (int i = 0; i < players.length; i++) {
            players[i] = new IntegrationPlayer(s.getOnlinePlayers()[i]);
        }
        return players;
    }
    @LuaMethod
    public IntegrationPlayer getPlayer(String name) {
        return new IntegrationPlayer(s.getPlayer(name));
    }
    
}
