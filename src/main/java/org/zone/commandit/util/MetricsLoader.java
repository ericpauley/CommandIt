package org.zone.commandit.util;

import java.io.IOException;

import org.zone.commandit.CommandIt;
import org.zone.commandit.thirdparty.Metrics;
import org.zone.commandit.thirdparty.Metrics.Graph;
import org.zone.commandit.thirdparty.Metrics.Plotter;

public class MetricsLoader {
    
    public static Metrics factory(final CommandIt plugin) {
        try {
            Metrics metrics = new Metrics(plugin);
            
            Graph total = metrics.createGraph("Number of Lua Scripts");
            total.addPlotter(new Plotter() {
                
                @Override
                public int getValue() {
                    return plugin.getCommandBlocks().size();
                }
            });
            Graph commands = metrics.createGraph("Command Method Breakdown");
            commands.addPlotter(new Plotter("Regular") {
                
                @Override
                public int getValue() {
                    int number = 0;
                    for (Code code : plugin.getCommandBlocks().values()) {
                        if (code.toString().contains("run(")) {
                            number++;
                        }
                    }
                    return number;
                }
            });
            commands.addPlotter(new Plotter("Sudo") {
                
                @Override
                public int getValue() {
                    int number = 0;
                    for (Code code : plugin.getCommandBlocks().values()) {
                        if (code.toString().contains("sudo(")) {
                            number++;
                        }
                    }
                    return number;
                }
            });
            commands.addPlotter(new Plotter("Op") {
                
                @Override
                public int getValue() {
                    int number = 0;
                    for (Code code : plugin.getCommandBlocks().values()) {
                        if (code.toString().contains("op(")) {
                            number++;
                        }
                    }
                    return number;
                }
            });
            commands.addPlotter(new Plotter("Console") {
                
                @Override
                public int getValue() {
                    int number = 0;
                    for (Code code : plugin.getCommandBlocks().values()) {
                        if (code.toString().contains("console(")) {
                            number++;
                        }
                    }
                    return number;
                }
            });
            if (metrics.start()) {
                Message.info("metrics.success");
            } else {
                plugin.getLogger().info(Message.parseRaw("metrics.failure"));
            }
            
            return metrics;
        } catch (IOException e) {
            plugin.getLogger().warning(Message.parseRaw("metrics.failure"));
            return null;
        }
    }
}
