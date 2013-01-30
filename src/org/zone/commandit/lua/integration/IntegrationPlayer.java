package org.zone.commandit.lua.integration;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import se.krka.kahlua.integration.annotations.LuaMethod;

public class IntegrationPlayer {

	private Player p;

	public IntegrationPlayer(Player p) {
		this.p = p;
	}

	/**
	 * Sends the player a message
	 * 
	 * @param message
	 *            The message to send
	 */
	@LuaMethod
	public void sendMessage(String message) {
		p.sendMessage(message);
	}

	/**
	 * Teleports the player to a location
	 * 
	 * @param x
	 *            The X coordinate to teleport the player to
	 * @param y
	 *            The Y coordinate to teleport the player to
	 * @param z
	 *            The Z coordinate to teleport the player to
	 */
	@LuaMethod
	public void teleport(double x, double y, double z) {
		p.teleport(new Location(p.getWorld(), x, y, z));
	}

	@LuaMethod
	public void teleport(String world, double x, double y, double z) {
		p.teleport(new Location(p.getServer().getWorld(world), x, y, z));
	}

	@LuaMethod
	public void chat(String message) {
		p.chat(message);
	}

	@LuaMethod
	public void runCommand(String message) {
		chat(message);
	}

	@LuaMethod
	public IntegrationServer getServer() {
		return new IntegrationServer(p.getServer());
	}

}
