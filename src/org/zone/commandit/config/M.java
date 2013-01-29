package org.zone.commandit.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.zone.commandit.CommandIt;
import org.zone.commandit.util.YamlLoader;

public class M extends ConfigStore {
    
    private static Map<String, String> messages = new ConcurrentHashMap<String, String>();
    private static Map<String, String> defaults = new ConcurrentHashMap<String, String>();
    
    public M(CommandIt plugin) {
        super(plugin);
    }
    
    /**
     * {@inheritDoc}
     */
    public void load() {
        Configuration config = YamlLoader.loadResource(plugin, "messages.yml");
        
        for (String k : config.getKeys(true)) {
            if (!config.isConfigurationSection(k)) {
                messages.put(k, config.getString(k));
            }
        }
        
        Configuration defaults = YamlConfiguration.loadConfiguration(plugin.getResource("messages.yml"));
        
        for (String k : defaults.getKeys(true)) {
            if (!defaults.isConfigurationSection(k)) {
                M.defaults.put(k, defaults.getString(k));
            }
        }
    }
    
    public String parseMessage(String messageName) {
        return parseMessage(messageName, null, null);
    }
    
    public String parseMessage(String message, String... replacements) {
        String raw = parseRaw(message, replacements);
        String prefix = messages.get("prefix");
        if (prefix != null) {
            return ChatColor.translateAlternateColorCodes('&', prefix + raw);
        } else {
            return "Could not find message " + prefix + ".";
        }
    }
    
    public String parseRaw(String messageName, String... replacements) {
        messageName = messageName.toLowerCase();
        String prefix = messages.get(messageName.split("\\.")[0] + ".prefix");
        String raw = messages.get(messageName);
        String defaultValue = defaults.get(messageName);
        if (raw != null) {
            List<String> allMatches = new ArrayList<String>();
            Matcher m = Pattern.compile("(\\{\\w*\\})").matcher(defaultValue);
            while (m.find()) {
                allMatches.add(m.group(1));
            }
            if (allMatches.size() != replacements.length) {
                return "Method doesn't supply enough params.";
            }
            for (int i = 0; i < replacements.length; i++) {
                raw = raw.replaceAll("(?iu)\\{" + allMatches.get(i) + "\\}", replacements[i]);
            }
            raw = raw.replaceAll("(?iu)\\{PREFIX\\}", ((prefix != null) ? prefix : ""));
            return ChatColor.translateAlternateColorCodes('&', ((prefix != null) ? prefix : "") + raw);
        } else {
            return "Could not find message " + messageName + ".";
        }
    }
    
    public void sendMessage(CommandSender cs, String messageName) {
        sendMessage(cs, messageName, null, null);
    }
    
    public void sendMessage(CommandSender cs, String messageName, String... replacements) {
        if (cs != null) {
            cs.sendMessage(parseMessage(messageName, replacements));
        }
    }
    
    public void sendRaw(CommandSender cs, String messageName) {
        sendRaw(cs, messageName, null, null);
    }
    
    public void sendRaw(CommandSender cs, String messageName, String... replacements) {
        if (cs != null) {
            cs.sendMessage(parseRaw(messageName, replacements));
        }
    }
}
