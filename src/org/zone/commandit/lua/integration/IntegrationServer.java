package org.zone.commandit.lua.integration;

import org.bukkit.Server;
import org.zone.commandit.lua.util.LuaUtil;

import se.krka.kahlua.vm.KahluaTable;


public class IntegrationServer {
    
    private Server s;
    
    public IntegrationServer(Server s){
        this.s = s;
    }
    
    public void shutdown(){
        s.shutdown();
    }
    
    public void broadcast(String message){
        s.broadcastMessage(message);
    }
    
    public KahluaTable getPlayers(){
        IntegrationPlayer[] players = new IntegrationPlayer[s.getOnlinePlayers().length];
        for(int i = 0;i<players.length;i++){
            players[i] = new IntegrationPlayer(s.getOnlinePlayers()[i]);
        }
        return LuaUtil.expose(players);
    }
    
}
