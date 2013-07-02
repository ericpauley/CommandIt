package org.zone.commandit.python.integration;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.zone.commandit.handler.CommandBlockInteractEvent;

public class IntegrationServer {
    
    protected CommandBlockInteractEvent event;
    protected Server server;
    
    public IntegrationServer(final CommandBlockInteractEvent e) {
        event = e;
        server = e.getPlugin().getServer();
    }
    
    /**
     * Broadcast a message to all players
     * @param message
     */
    
    public void broadcast(String message) {
        server.broadcastMessage(message);
    }
    
    /**
     * @param name
     * @return Player with the given name
     */
    
    public IntegrationPlayer getPlayer(String name) {
        return new IntegrationPlayer(event);
    }
    
    /**
     * @return List of all currently online players
     */
    
    public IntegrationPlayer[] getPlayers() {
        IntegrationPlayer[] players = new IntegrationPlayer[server.getOnlinePlayers().length];
        for (int i = 0; i < players.length; i++) {
            players[i] = new IntegrationPlayer(event, server.getOnlinePlayers()[i]);
        }
        return players;
    }
    
    /**
     * Send command as console
     * @param command
     */
    
    public void run(String command) {
        Bukkit.dispatchCommand(server.getConsoleSender(), command);
    }
    
    /**
     * Shut the server down
     */
    
    public void shutdown() {
        server.shutdown();
    }
    
}
