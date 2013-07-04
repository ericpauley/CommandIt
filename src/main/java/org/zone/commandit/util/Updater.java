package org.zone.commandit.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.zone.commandit.CommandIt;

public class Updater {
    
    private static final String url = "http://api.bukget.org/3/plugins/bukkit/command-signs/latest?fields=versions.version,versions.link";
    
    public void init() {
        if (task == null && active) {
            task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new UpdateTask(), 0, 20 * 60 * 15);
        }
    }
    
    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
    
    private File pluginFile;
    private CommandIt plugin;
    boolean active = false;
    boolean auto = false;
    String toFetch;
    private BukkitTask task;
    private Version currentVersion;
    private Version availableVersion;
    
    public Updater(CommandIt plugin, File pluginFile) {
        this.plugin = plugin;
        this.pluginFile = pluginFile;
        currentVersion = Version.parse(plugin.getDescription().getVersion());
        availableVersion = currentVersion;
        if (currentVersion.build == 0) {
            plugin.getLogger().warning("Running an in-house dev build! Auto-update disabled!");
            return;
        }
        active = plugin.getPluginConfig().getBoolean("updater.auto-check");
        auto = plugin.getPluginConfig().getBoolean("updater.auto-install");
    }
    
    public void installUpdate(CommandSender cs, Version newVersion, String fetch) {
        URL dl = null;
        File fl = null;
        long t = System.nanoTime() / 1000000;
        Message.sendMessage(cs, "update.start", newVersion);
        try {
            fl = new File(plugin.getServer().getUpdateFolderFile(), pluginFile.getName());
            dl = new URL(fetch);
            FileUtils.copyURLToFile(dl, fl);
            Message.sendMessage(cs, "update.finished", ((double) System.nanoTime() / 1000000 - t) / 1000);
        } catch (Exception e) {
            Message.sendMessage(cs, "update_fetch_failed", e);
        }
        this.currentVersion = newVersion;
    }
    
    private class UpdateTask implements Runnable {
        
        @Override
        public void run() {
            URL source;
            try {
                source = new URL(url);
            } catch (MalformedURLException e) {
                Message.warning("check_error", "the checking URL is malformed.");
                return;
            }
            try {
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(new InputStreamReader(source.openStream()));
                
                JSONArray versions = (JSONArray) json.get("versions");
                JSONObject latest = (JSONObject) versions.get(0);
                
                Version available = Version.parse((String) latest.get("version"));
                if (available.getBuild() > currentVersion.getBuild()) {
                    /*if (auto) {
                        installUpdate(plugin.getServer().getConsoleSender(), available, (String) latest.get("link"));
                        availableVersion = available;
                    } else {
                        availableVersion = available;
                        toFetch = (String) latest.get("link");
                    }*/
                    availableVersion = available;
                    toFetch = (String) latest.get("link");
                }
            } catch (IOException ex) {
                Message.warning("check_error", "could not connect to plugin repository.");
            } catch (ParseException e) {
                Message.warning("check_error", "update data is not in the correct format.");
            }
        }
        
    }
    
    public static class Version {
        
        int major, minor, revision, build;
        
        private Version(int major, int minor, int revision, int build) {
            this.major = major;
            this.minor = minor;
            this.revision = revision;
            this.build = build;
        }
        
        @Override
        public String toString() {
            return String.format("%d.%d.%d-%d", major, minor, revision, build);
        }
        
        public static Version parse(String s) {
            String[] parts = s.split("-");
            String build = parts[1];
            parts = parts[0].split("\\.");
            if (build.equalsIgnoreCase("DEV"))
                return new Version(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), 0);
            else
                return new Version(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(build));
        }
        
        public int getMajor() {
            return major;
        }
        
        public int getMinor() {
            return minor;
        }
        
        public int getRevision() {
            return revision;
        }
        
        public int getBuild() {
            return build;
        }
        
    }
    
    public Version getCurrentVersion() {
        return currentVersion;
    }
    
    public Version getAvailableVersion() {
        return availableVersion;
    }
    
}
