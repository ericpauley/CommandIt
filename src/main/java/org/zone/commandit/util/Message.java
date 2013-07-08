package org.zone.commandit.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.zone.commandit.config.Messages;

public class Message {
    
    private static Messages messages;
    private static JavaPlugin plugin;
    
    public static void init(JavaPlugin plugin, Messages messages) {
        Message.plugin = plugin;
        Message.messages = messages;
    }
    
    public static String parseMessage(String message, Object... replacements) {
        String raw = parseRaw(message, replacements);
        String prefix = messages.get("prefix");
        
        return ChatColor.translateAlternateColorCodes('&', ((prefix != null) ? prefix : "") + raw);
    }
    
    public static String parseRaw(String messageName, Object... replacements) {
        messageName = messageName.toLowerCase();
        String prefix = messages.get(messageName.split("\\.")[0] + ".prefix");
        String raw = messages.get(messageName);
        if (raw != null) {
            raw = raw.replaceAll("(?iu)\\{PREFIX\\}", ((prefix != null) ? prefix : ""));
            for (int i = 0; i < replacements.length; i++) {
                raw = raw.replaceFirst("(?iu)\\{[A-Z]+\\}", replacements[i].toString());
            }
            return ChatColor.translateAlternateColorCodes('&', ((prefix != null) ? prefix : "") + raw);
        } else {
            return "Could not find message " + messageName + ".";
        }
    }
    
    public static void sendMessage(CommandSender cs, String messageName, Object... replacements) {
        if (cs != null) {
            cs.sendMessage(parseMessage(messageName, replacements));
        }
    }
    
    public static void sendRaw(CommandSender cs, String messageName, Object... replacements) {
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
