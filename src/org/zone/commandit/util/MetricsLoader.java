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

			Graph g = metrics.createGraph("Number of CommandSigns");
			g.addPlotter(new Plotter() {

				@Override
				public int getValue() {
					return plugin.getCodeBlocks().size();
				}
			});
			Graph g3 = metrics.createGraph("CommandSigns Version");
			g3.addPlotter(new Plotter(plugin.getDescription().getVersion()) {

				@Override
				public int getValue() {
					return 1;
				}
			});
			Graph g2 = metrics.createGraph("Super Signs Used");
			g2.addPlotter(new Plotter("Permission") {

				@Override
				public int getValue() {
					int number = 0;
					for (LuaCode cst : plugin.getCodeBlocks().values()) {
						for (String s : cst) {
							if (s.startsWith("/*") || s.startsWith("!/*")) {
								number++;
							}
						}
					}
					return number;
				}
			});
			g2.addPlotter(new Plotter("Op") {

				@Override
				public int getValue() {
					int number = 0;
					for (LuaCode cst : plugin.getCodeBlocks().values()) {
						for (String s : cst) {
							if (s.startsWith("/^") || s.startsWith("!/^")) {
								number++;
							}
						}
					}
					return number;
				}
			});
			g2.addPlotter(new Plotter("Console") {

				@Override
				public int getValue() {
					int number = 0;
					for (LuaCode cst : plugin.getCodeBlocks().values()) {
						for (String s : cst) {
							if (s.startsWith("/#") || s.startsWith("!/#")) {
								number++;
							}
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
