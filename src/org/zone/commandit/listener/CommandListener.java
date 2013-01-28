package org.zone.commandit.listener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.zone.commandit.CommandIt;
import org.zone.commandit.util.PlayerState;
import org.zone.commandit.util.LuaCode;

public class CommandListener implements CommandExecutor {

	private CommandIt plugin;

	public CommandListener(CommandIt plugin) {
		this.plugin = plugin;
	}

	protected boolean add(final CommandSender sender, Player player,
			int lineNumber, String[] args) {
		if (player == null) {
			plugin.getMessenger().sendMessage(sender, "failure.player_only");
		}
		if (plugin.hasPermission(player, "CommandIt.create.regular")) {
			clipboard(sender, player, lineNumber, 1, args);
			if (plugin.getPlayerStates().get(player) != PlayerState.EDIT) {
				plugin.getPlayerStates().put(player, PlayerState.ENABLE);
				plugin.getMessenger().sendMessage(player, "progress.add");
			}
		} else {
			plugin.getMessenger().sendMessage(player, "failure.no_perms");
		}
		return true;
	}

	protected boolean batch(final CommandSender sender, Player player,
			String[] args) {
		PlayerState ps = plugin.getPlayerStates().get(player);
		if (ps == null) {
			plugin.getMessenger().sendMessage(player, "failure.not_in_mode");
			return false;
		}
		switch (ps) {
		case REMOVE:
			player.sendMessage("Switched to batch remove mode.");
			ps = PlayerState.BATCH_REMOVE;
			break;
		case BATCH_REMOVE:
			player.sendMessage("Switched to single remove mode.");
			ps = PlayerState.REMOVE;
			break;
		case ENABLE:
			player.sendMessage("Switched to batch enable mode.");
			ps = PlayerState.BATCH_ENABLE;
			break;
		case BATCH_ENABLE:
			player.sendMessage("Switched to single enable mode.");
			ps = PlayerState.ENABLE;
			break;
		case READ:
			player.sendMessage("Switched to batch read mode.");
			ps = PlayerState.BATCH_READ;
			break;
		case BATCH_READ:
			player.sendMessage("Switched to single read mode.");
			ps = PlayerState.READ;
			break;
		case TOGGLE:
			player.sendMessage("Switched to batch toggle mode.");
			ps = PlayerState.BATCH_TOGGLE;
			break;
		case BATCH_TOGGLE:
			player.sendMessage("Switched to single toggle mode.");
			ps = PlayerState.TOGGLE;
			break;
		case REDSTONE:
			player.sendMessage("Switched to batch redstone mode.");
			ps = PlayerState.BATCH_REDSTONE;
			break;
		case BATCH_REDSTONE:
			player.sendMessage("Switched to single redstone mode.");
			ps = PlayerState.REDSTONE;
			break;
		default:
			plugin.getMessenger().sendMessage(player, "failure.no_batch");
		}
		plugin.getPlayerStates().put(player, ps);
		return true;
	}

	protected boolean clear(final CommandSender sender, Player player,
			String[] args) {
		if (player == null) {
			plugin.getMessenger().sendMessage(sender, "failure.player_only");
		}
		if (plugin.hasPermission(player, "CommandIt.remove")) {
			PlayerState ps = plugin.getPlayerStates().get(player);
			if (ps == PlayerState.EDIT || ps == PlayerState.EDIT_SELECT) {
				finishEditing(player);
			}
			plugin.getPlayerStates().remove(player);
			plugin.getPlayerCode().remove(player);
			plugin.getMessenger().sendMessage(player, "success.cleared");
		} else {
			plugin.getMessenger().sendMessage(player, "failure.no_perms");
		}
		return true;
	}

	private void clipboard(final CommandSender sender, Player player,
			int lineNumber, int textStart, String[] args) {
		if (lineNumber < 1) {
			plugin.getMessenger().sendMessage(player, "failure.invalid_line");
		} else {
			if (plugin.getPlayerStates().get(player) == PlayerState.EDIT_SELECT) {
				plugin.getMessenger().sendMessage(player, "failure.must_select");
			}
			LuaCode text = plugin.getPlayerCode().get(player);
			if (text == null) {
				text = new LuaCode(player.getName(), false);
				plugin.getPlayerCode().put(player, text);
			}
			String line = StringUtils.join(args, " ", textStart, args.length);
			if (line.startsWith("/*")
					&& !plugin.hasPermission(player,
							"CommandIt.create.super", false)) {
				plugin.getMessenger().sendMessage(player, "failure.no_super");
			}
			if ((line.startsWith("/^") || line.startsWith("/#"))
					&& !plugin.hasPermission(player, "CommandIt.create.op",
							false)) {
				plugin.getMessenger().sendMessage(player, "failure.no_op");
			}
			text.setLine(lineNumber, line);
			plugin.getMessenger().sendRaw(player, "success.line_print",
					new String[] { "NUMBER", "LINE" }, new String[] {
							"" + lineNumber, line });
		}
	}

	protected boolean copy(final CommandSender sender, Player player,
			String[] args) {
		if (player == null) {
			plugin.getMessenger().sendMessage(sender, "failure.player_only");
		}
		if (plugin.hasPermission(player, "CommandIt.create.regular")) {
			PlayerState ps = plugin.getPlayerStates().get(player);
			if (ps == PlayerState.EDIT || ps == PlayerState.EDIT_SELECT) {
				finishEditing(player);
			}
			plugin.getPlayerStates().put(player, PlayerState.COPY);
			plugin.getMessenger().sendMessage(player, "progress.copy");
		} else {
			plugin.getMessenger().sendMessage(player, "failure.no_perms");
		}
		return true;
	}

	protected boolean edit(final CommandSender sender, Player player,
			String[] args) {
		if (plugin.hasPermission(sender, "CommandIt.edit", false)) {
			PlayerState ps = plugin.getPlayerStates().get(player);
			if (ps == PlayerState.EDIT_SELECT || ps == PlayerState.EDIT) {
				finishEditing(player);
			} else {
				plugin.getPlayerStates().put(player, PlayerState.EDIT_SELECT);
				plugin.getPlayerCode().remove(player);
				plugin.getMessenger().sendMessage(player, "progress.select_sign");
			}
		}
		return true;
	}

	public void finishEditing(Player player) {
		plugin.getPlayerStates().remove(player);
		plugin.getPlayerCode().remove(player);
		plugin.getMessenger().sendMessage(player, "success.done_editing");
	}

	protected boolean insert(final CommandSender sender, Player player,
			int lineNumber, String[] args) {
		if (player == null) {
			plugin.getMessenger().sendMessage(sender, "failure.player_only");
		}
		if (plugin.hasPermission(player, "CommandIt.create.regular")) {
			clipboard(sender, player, lineNumber, 2, args);
			if (plugin.getPlayerStates().get(player) != PlayerState.EDIT) {
				plugin.getPlayerStates().put(player, PlayerState.INSERT);
				plugin.getMessenger().sendMessage(player, "progress.add");
			}
		} else {
			plugin.getMessenger().sendMessage(player, "failure.no_perms");
		}
		return true;
	}

	@Override
	public boolean onCommand(final CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("CommandIt")) {
			if (args.length < 1 || args[0].equalsIgnoreCase("help")) {
				// Messaging.sendMessage(sender, "usage");
				return false;
			}
			Player tp = null;
			if (sender instanceof Player) {
				tp = (Player) sender;
			}
			final Player player = tp;
			String command = args[0].toLowerCase();
			Pattern pattern = Pattern.compile("(line|l)?(\\d+)");
			Matcher matcher = pattern.matcher(command);
			if (matcher.matches()) {
				return add(sender, player, Integer.parseInt(matcher.group(2)),
						args);
			} else if (command.equals("batch")) {
				return batch(sender, player, args);
			} else if (command.equals("clear")) {
				return clear(sender, player, args);
			} else if (command.equals("copy")) {
				return copy(sender, player, args);
			} else if (command.equals("edit")) {
				return edit(sender, player, args);
			} else if (command.equals("insert") && args.length > 1) {
				pattern = Pattern.compile("(line|l)?(\\d+)");
				matcher = pattern.matcher(args[1].toLowerCase());
				if (matcher.matches())
					return insert(sender, player,
							Integer.parseInt(matcher.group(2)), args);
			} else if (command.equals("read")) {
				return read(sender, player, args);
			} else if (command.equals("redstone")) {
				return redstone(sender, player, args);
			} else if (command.equals("reload")) {
				return reload(sender, player, args);
			} else if (command.equals("remove")) {
				return remove(sender, player, args);
			} else if (command.equals("save")) {
				return save(sender, player, args);
			} else if (command.equals("toggle")) {
				return toggle(sender, player, args);
			} else if (command.equals("update")) {
				return update(sender, player, args);
			} else if (command.equals("view")) {
				return view(sender, player, args);
			} else {
				plugin.getMessenger().sendMessage(sender, "failure.wrong_syntax");
				return true;
			}
		}
		return false;
	}

	protected boolean read(final CommandSender sender, Player player,
			String[] args) {
		if (player == null) {
			plugin.getMessenger().sendMessage(sender, "failure.player_only");
		}
		if (plugin.hasPermission(player, "CommandIt.create.regular")) {
			PlayerState ps = plugin.getPlayerStates().get(player);
			if (ps == PlayerState.EDIT || ps == PlayerState.EDIT_SELECT) {
				finishEditing(player);
			}
			plugin.getPlayerStates().put(player, PlayerState.READ);
			plugin.getMessenger().sendMessage(player, "progress.read");
		} else {
			plugin.getMessenger().sendMessage(player, "failure.no_perms");
		}
		return true;
	}

	protected boolean redstone(final CommandSender sender, Player player,
			String[] args) {
		if (player == null) {
			plugin.getMessenger().sendMessage(sender, "failure.player_only");
		}
		if (plugin.hasPermission(player, "CommandIt.create.redstone")) {
			PlayerState ps = plugin.getPlayerStates().get(player);
			if (ps == PlayerState.EDIT || ps == PlayerState.EDIT_SELECT) {
				finishEditing(player);
			}
			plugin.getPlayerStates().put(player, PlayerState.REDSTONE);
			plugin.getMessenger().sendMessage(player, "progress.redstone");
		} else {
			plugin.getMessenger().sendMessage(player, "failure.no_perms");
		}
		return true;
	}

	protected boolean reload(final CommandSender sender, Player player,
			String[] args) {
		if (plugin.hasPermission(sender, "CommandIt.reload", false)) {
			plugin.load();
			plugin.getMessenger().sendMessage(sender, "success.reloaded");
		} else {
			plugin.getMessenger().sendMessage(player, "failure.no_perms");
		}
		return true;
	}

	protected boolean remove(final CommandSender sender, Player player,
			String[] args) {
		if (player == null) {
			plugin.getMessenger().sendMessage(sender, "failure.player_only");
		}
		if (plugin.hasPermission(player, "CommandIt.remove")) {
			PlayerState ps = plugin.getPlayerStates().get(player);
			if (ps == PlayerState.EDIT || ps == PlayerState.EDIT_SELECT) {
				finishEditing(player);
			}
			plugin.getPlayerStates().put(player, PlayerState.REMOVE);
			plugin.getMessenger().sendMessage(player, "progress.remove");
		} else {
			plugin.getMessenger().sendMessage(player, "failure.no_perms");
		}
		return true;
	}

	protected boolean save(final CommandSender sender, Player player,
			String[] args) {
		if (plugin.hasPermission(sender, "CommandIt.save", false)) {
			plugin.getCodeLoader().saveFile();
			plugin.getMessenger().sendMessage(sender, "success.saved");
		}
		return true;
	}

	protected boolean toggle(final CommandSender sender, Player player,
			String[] args) {
		if (player == null) {
			plugin.getMessenger().sendMessage(sender, "failure.player_only");
		}
		if (plugin.hasPermission(player, "CommandIt.toggle")) {
			PlayerState ps = plugin.getPlayerStates().get(player);
			if (ps == PlayerState.EDIT || ps == PlayerState.EDIT_SELECT) {
				finishEditing(player);
			}
			plugin.getPlayerStates().put(player, PlayerState.TOGGLE);
			plugin.getMessenger().sendMessage(player, "progress.toggle");
		} else {
			plugin.getMessenger().sendMessage(player, "failure.no_perms");
		}
		return true;
	}

	protected boolean update(final CommandSender sender, Player player,
			String[] args) {
		//TODO: Write the update method...
		return true;
	}

	protected boolean view(final CommandSender sender, Player player,
			String[] args) {
		if (player == null) {
			plugin.getMessenger().sendMessage(sender, "failure.player_only");
		}
		if (plugin.hasPermission(player, "CommandIt.create.regular")) {
			LuaCode text = plugin.getPlayerCode().get(player);
			if (text == null) {
				player.sendMessage("No text in clipboard");
			} else {
				int i = 1;
				for (String s : text) {
					if (!s.equals("")) {
						player.sendMessage(i + ": " + s);
					}
					i++;
				}
			}
			plugin.getPlayerStates().remove(player);
		} else {
			plugin.getMessenger().sendMessage(player, "failure.no_perms");
		}
		return true;
	}

}
