package org.zone.commandit.util;

import java.io.File;

import org.bukkit.scheduler.BukkitTask;
import org.zone.commandit.CommandIt;

public class Updater {
    
    private static final String url = "http://dev.thechalkpot.com:8080/job/CommandIt/";
    
    public enum ReleaseType {
        RELEASE("Release"),
        BETA("Beta"),
        DEV("lastSuccessfulBuild");
        
        private final String dir;
        
        ReleaseType(String dir) {
            this.dir = dir;
        }
        
        public String getUrl() {
            return url+dir;
        }
    }
    
    public void init(){
        if(task == null && type != null){
            task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new UpdateTask(), 0, 20*60*15);
        }
    }
    
    public void stop(){
        if(task != null){
            task.cancel();
            task = null;
        }
    }
    
    private File pluginFile;
    private CommandIt plugin;
    private ReleaseType type;
    private BukkitTask task;
    private Version currentVersion;
    
    public Updater(CommandIt plugin, File pluginFile) {
        this.plugin = plugin;
        this.pluginFile = pluginFile;
        currentVersion = Version.parse(plugin.getDescription().getVersion());
        if(currentVersion.build == -1){
            plugin.getLogger().warning("Running an in-house dev build! Auto-update disabled!");
        }
        String typeID = plugin.getPluginConfig().get("updater.revision");
        if (typeID.equalsIgnoreCase("release"))
            this.type = ReleaseType.RELEASE;
        else if (typeID.equalsIgnoreCase("beta"))
            this.type = ReleaseType.BETA;
        else if (typeID.equalsIgnoreCase("dev"))
            this.type = ReleaseType.DEV;
        else {
            this.type = null;
            plugin.getLogger().warning("Auto-update disabled! Re-enable in config.");
        }
    }
    
    private class UpdateTask implements Runnable {
        
        @Override
        public void run() {
            // TODO Auto-generated method stub
            
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
        
        public static Version parse(String s) {
            String[] parts = s.split("-");
            String build = parts[1];
            parts = parts[0].split("\\.");
            if (build.equalsIgnoreCase("DEV"))
                return new Version(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), -1);
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
    
}
