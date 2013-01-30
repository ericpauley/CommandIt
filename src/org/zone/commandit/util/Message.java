package org.zone.commandit.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.zone.commandit.CommandIt;
import org.zone.commandit.config.Messages;

public class Message {

	private static Messages messagesInstance;
	private static CommandIt plugin;

	public static void init(CommandIt plugin) {
		Message.plugin = plugin;
	}

	private static Messages getMessagesInstance() {
		if (messagesInstance == null) {
			messagesInstance = new Messages(plugin);
		}
		return messagesInstance;
	}

	public static String parseMessage(String message, Object... replacements) {
		Messages messages = getMessagesInstance();
		String raw = parseRaw(message, replacements);
		String prefix = messages.get("prefix");
		if (prefix != null) {
			return ChatColor.translateAlternateColorCodes('&', prefix + raw);
		} else {
			return "Could not find message " + prefix + ".";
		}
	}

	public static String parseRaw(String messageName, Object... replacements) {
		Messages messages = getMessagesInstance();
		messageName = messageName.toLowerCase();
		String prefix = messages.get(messageName.split("\\.")[0] + ".prefix");
		String raw = messages.get(messageName);
		if (raw != null) {
			List<String> tags = new ArrayList<String>();
			for (int i = 0; i < replacements.length; i++) {
				raw = raw.replaceAll("(?iu)\\{" + tags.get(i) + "\\}",
						replacements[i].toString());
			}
			raw = raw.replaceAll("(?iu)\\{PREFIX\\}",
					((prefix != null) ? prefix : ""));
			return ChatColor.translateAlternateColorCodes('&',
					((prefix != null) ? prefix : "") + raw);
		} else {
			return "Could not find message " + messageName + ".";
		}
	}

	public static void sendMessage(CommandSender cs, String messageName,
			Object... replacements) {
		if (cs != null) {
			cs.sendMessage(parseMessage(messageName, replacements));
		}
	}

	public static void sendRaw(CommandSender cs, String messageName,
			Object... replacements) {
		if (cs != null) {
			cs.sendMessage(parseRaw(messageName, replacements));
		}
	}

	public static void info(String messageName, Object... replacements) {
		plugin.getLogger().info(parseRaw(messageName, replacements));
	}

	public static void warning(String messageName, Object... replacements) {
		plugin.getLogger().warning(parseRaw(messageName, replacements));
	}

	public static void severe(String messageName, Object... replacements) {
		plugin.getLogger().severe(parseRaw(messageName, replacements));
	}
}
