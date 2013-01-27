package org.zone.commandit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.zone.commandit.config.Config;
import org.zone.commandit.config.Messages;
import org.zone.commandit.listener.CommandListener;
import org.zone.commandit.listener.EventListener;
import org.zone.commandit.thirdparty.Metrics;
import org.zone.commandit.util.CodeLoader;
import org.zone.commandit.util.LuaCode;
import org.zone.commandit.util.MetricsLoader;
import org.zone.commandit.util.PlayerState;
import org.zone.commandit.util.Updater;

public class CommandIt extends JavaPlugin {
    
		// Listeners
		private final EventListener listener = new EventListener(this);
		public CommandListener commandExecutor = new CommandListener(this);

		// Third-party
		public Metrics metrics;

		private Economy economy;
		private Permission permission;

		// Plugin variables
		private Map<Location, LuaCode> cache = new HashMap<Location, LuaCode>();
		private Map<OfflinePlayer, PlayerState> playerStates = new HashMap<OfflinePlayer, PlayerState>();
		private Map<OfflinePlayer, LuaCode> playerCode = new HashMap<OfflinePlayer, LuaCode>();

		private final CodeLoader loader = new CodeLoader(this);
		private final Config config = new Config(this);
		private final Messages messenger = new Messages(this);
		private final Updater updater = new Updater(this);

		// Class variables
		private BukkitTask updateTask;

		public File getUpdateFile() {
			return new File(getServer().getUpdateFolderFile().getAbsoluteFile(),
					super.getFile().getName());
		}

		public boolean hasPermission(CommandSender player, String string) {
			return hasPermission(player, string, true);
		}

		public boolean hasPermission(CommandSender player, String string,
				boolean notify) {
			boolean perm;
			if (permission == null) {
				perm = player.hasPermission(string);
			} else {
				perm = permission.has(player, string);
			}
			if (perm == false && notify) {
				messenger.sendMessage(player, "failure.no_perms");
			}
			return perm;
		}

		public void load() {
			config.load();
			messenger.load();
			loader.loadFile();
			setupPermissions();
			setupEconomy();

			if (config.getBoolean("updater.auto-check") == true)
				startUpdateCheck();

			if (config.getBoolean("metrics.enable") == true)
				MetricsLoader.factory(this);
			else
				getLogger().info(messenger.parseRaw("metrics.opt_out"));
		}

		@Override
		public void onDisable() {
			if (updateTask != null)
				updateTask.cancel();
			loader.saveFile();
		}

		@Override
		public void onEnable() {
			load();
			PluginManager pm = getServer().getPluginManager();
			getCommand("commandsigns").setExecutor(commandExecutor);
			pm.registerEvents(listener, this);
		}

		public boolean setupEconomy() {
			RegisteredServiceProvider<Economy> economyProvider = getServer()
					.getServicesManager().getRegistration(
							net.milkbowl.vault.economy.Economy.class);
			if (economyProvider != null) {
				economy = economyProvider.getProvider();
			}
			return economy != null;
		}

		public boolean setupPermissions() {
			RegisteredServiceProvider<Permission> permissionProvider = getServer()
					.getServicesManager().getRegistration(
							net.milkbowl.vault.permission.Permission.class);
			if (permissionProvider != null) {
				permission = permissionProvider.getProvider();
			}
			return permission != null;
		}

		public void startUpdateCheck() {
			Runnable checker = getUpdater().new Checker();
			updateTask = getServer().getScheduler().runTaskTimer(this, checker, 0,
					1728000L);
		}
		
		public Config getPluginConfig() {
			return config;
		}
		public CodeLoader getCodeLoader() {
			return loader;
		}
		public Messages getMessenger() {
			return messenger;
		}
		public Updater getUpdater() {
			return updater;
		}
		
		public Map<Location, LuaCode> getCodeBlocks() {
			return cache;
		}
		public Map<OfflinePlayer, PlayerState> getPlayerStates() {
			return playerStates;
		}
		public Map<OfflinePlayer, LuaCode> getPlayerCode() {
			return playerCode;
		}
		
		public Economy getEconomy() {
			return economy;
		}
		public Permission getPermissionHandler() {
			return permission;
		}
}