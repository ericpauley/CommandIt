package org.zone.commandit;

import java.util.HashMap;
import java.util.Map;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.zone.commandit.config.CommandBlocks;
import org.zone.commandit.config.Config;
import org.zone.commandit.config.Messages;
import org.zone.commandit.io.DataAdapter;
import org.zone.commandit.io.FileAdapter;
import org.zone.commandit.io.SqlAdapter;
import org.zone.commandit.listener.CommandListener;
import org.zone.commandit.listener.EventListener;
import org.zone.commandit.thirdparty.Metrics;
import org.zone.commandit.util.Message;
import org.zone.commandit.util.MetricsLoader;
import org.zone.commandit.util.PlayerState;
import org.zone.commandit.util.Code;
import org.zone.commandit.util.Updater;

public class CommandIt extends JavaPlugin {
    
    // Listeners
    private EventListener listener = new EventListener(this);
    public CommandListener commandExecutor = new CommandListener(this);
    
    // Third-party
    public Metrics metrics;
    
    private Economy economy;
    private Permission permission;
    
    // Plugin variables
    private CommandBlocks blocks;
    private Map<OfflinePlayer, PlayerState> playerStates = new HashMap<OfflinePlayer, PlayerState>();
    private Map<OfflinePlayer, Code> playerCode = new HashMap<OfflinePlayer, Code>();
    
    private Config config = new Config(this);
    private Messages messages = new Messages(this);
    private Updater updater;
    
    // Class variables
    private BukkitTask updateTask;
    
    /**
     * Complements Vault to finding whether a player has a given permission
     * string
     * 
     * @param player
     * @param string
     *            Permission node in dotted format
     * @return
     */
    public boolean hasPermission(CommandSender player, String string) {
        return hasPermission(player, string, true);
    }
    
    /**
     * Complements Vault to finding whether a player has a given permission
     * string
     * 
     * @param player
     * @param string
     *            Permission node in dotted format
     * @param notify
     *            True if nag message should be sent on failure
     * @return
     */
    public boolean hasPermission(CommandSender player, String string, boolean notify) {
        boolean perm;
        if (permission == null) {
            perm = player.hasPermission(string);
        } else {
            perm = permission.has(player, string);
        }
        if (perm == false && notify) {
            Message.sendMessage(player, "failure.no_perms");
        }
        return perm;
    }
    
    /**
     * Load all required data for the plugin
     */
    public void load() {
        config.load();
        messages.load();
        Message.init(this, messages);
        setupPermissions();
        setupEconomy();
        
        DataAdapter adapter;
        if (config.getBoolean("sql.enabled"))
            adapter = new SqlAdapter(this);
        else
            adapter = new FileAdapter(this, "blocks.yml");
        
        blocks = new CommandBlocks(adapter);
        blocks.load();
        
        updater = new Updater(this, this.getFile());
        if (config.getBoolean("updater.auto-check") == true)
            updater.init();
        
        if (config.getBoolean("metrics.enable") == true)
            MetricsLoader.factory(this);
        else
            Message.info("metrics.opt_out");
    }
    
    @Override
    public void onDisable() {
        if (updateTask != null)
            updateTask.cancel();
        
        blocks.save();
    }
    
    @Override
    public void onEnable() {
        load();
        PluginManager pm = getServer().getPluginManager();
        getCommand("commandit").setExecutor(commandExecutor);
        pm.registerEvents(listener, this);
    }
    
    /**
     * Find the resident economy system and set up Vault to use it.
     * 
     * @return True if successful
     */
    public boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        return economy != null;
    }
    
    /**
     * Find the resident permission system and set up Vault to use it.
     * 
     * @return True if successful
     */
    public boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return permission != null;
    }
    
    /**
     * @return Plugin's configuration and settings handler
     */
    public Config getPluginConfig() {
        return config;
    }
    
    /**
     * @return Handler for the updater system
     */
    public Updater getUpdater() {
        return updater;
    }
    
    /**
     * @return All loaded code blocks on the server
     */
    public CommandBlocks getCommandBlocks() {
        return blocks;
    }
    
    /**
     * @return The states of all players on the server
     */
    public Map<OfflinePlayer, PlayerState> getPlayerStates() {
        return playerStates;
    }
    
    /**
     * @return The clipboards of all players on the server
     */
    public Map<OfflinePlayer, Code> getPlayerCode() {
        return playerCode;
    }
    
    /**
     * @return Vault's economy handler
     */
    public Economy getEconomy() {
        return economy;
    }
    
    /**
     * @return Vault's permission handler
     */
    public Permission getPermissionHandler() {
        return permission;
    }
}