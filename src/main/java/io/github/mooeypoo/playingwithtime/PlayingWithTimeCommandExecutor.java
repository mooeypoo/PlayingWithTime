package io.github.mooeypoo.playingwithtime;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayingWithTimeCommandExecutor implements CommandExecutor {
	private PlayingWithTime plugin;
	private HashMap<String, String> paramMap = new HashMap<String, String>();

	public PlayingWithTimeCommandExecutor(PlayingWithTime plugin) {
		this.plugin = plugin;
		this.generateParameterMap();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("playingwithtime")) {
			return false;
		}

		if (args.length == 0 || args[0].equalsIgnoreCase("help") ) {
			this.outputHelp(sender);
			return true;
		} else if (args[0].equalsIgnoreCase("process")) {
			if (!sender.hasPermission("playingwithtime.cmd.process")) {
				this.outputToPlayerAndConsole("You do not have permission to invoke the process action.", sender);
				return false;
			}
			// Go over all online users
			this.outputToPlayerAndConsole("Starting manual processing of online users...", sender);
			int counter = this.runProcessPlayers();
			this.outputToPlayerAndConsole("Processing finished for " + counter + " online players.", sender);
			return true;
		} else if (args[0].equalsIgnoreCase("reload")) {
			if (!sender.hasPermission("playingwithtime.cmd.reload")) {
				this.outputToPlayerAndConsole("You do not have permission to invoke the reload action.", sender);
				return false;
			}

			this.outputToPlayerAndConsole("Reloading configuration files", sender);
			this.plugin.getActionManager().reload();
			this.outputToPlayerAndConsole("Reload complete. You may need to reprocess the new configuration for users who are currently online.", sender);
			return true;
		}

		return false;
	}
	
	private int runProcessPlayers() {
		int counter = 0;
		for (Player player: Bukkit.getServer().getOnlinePlayers()) {
			if (!this.plugin.shouldRunForPlayer(player)) {
				continue;
			}
			
			// Output individual players to console-only
			this.plugin.getLogger().info("[PlayingWithTime] Processing for player: " + player.getName());
			this.plugin.getActionManager().processPlayer(player);
			counter++;
		}
		return counter;
	}
		
	private void outputHelp(CommandSender sender) {
		String output = "";
		Boolean toPlayer = (sender instanceof Player);
		
		output = "Available actions for the /playingwithtime command:";
		
		if (sender instanceof Player) {
			// Send to the player, in-game
			sender.sendMessage(output);
		} else {
			this.plugin.getLogger().info(output);
		}

		this.outputToPlayerOrConsole("Commands list:", sender);

		for (String param : this.paramMap.keySet()) {
			output = String.format(
				toPlayer ? "* " + ChatColor.GREEN + "%s" + ChatColor.WHITE + ": %s" : "* %s: %s",
				param, this.paramMap.get(param)
			);
			this.outputToPlayerOrConsole(output, sender);
		}
		this.outputToPlayerOrConsole("Use the command /playingwithtime [action] to invoke any of the above actions.", sender);
	}
	
	private void generateParameterMap() {
		this.paramMap.clear();
		this.paramMap.put("process", "Run the evaluation process manually for all online users.");
		this.paramMap.put("reload", "Reload all configuration files.");
	}
	
	private void output(String out, CommandSender sender, Boolean sendToBoth) {
		String formatColor = ChatColor.BLUE + "[PlayingWithTime] " + ChatColor.WHITE + "%s";
		String formatBlank = "%s";
		Boolean inGame = sender instanceof Player;
		
		if (sendToBoth || !inGame) {
			this.plugin.getLogger().info(String.format(formatBlank, out));
		}
		
		if (inGame) {
			// Send to the player, in-game
			// Only do that if this was invoked from in-game
			sender.sendMessage(String.format(formatColor, out));
		}
		
	}

	private void outputToPlayerOrConsole(String out, CommandSender sender) {
		this.output(out, sender, false);
	}
	
	private void outputToPlayerAndConsole(String out, CommandSender sender) {
		this.output(out, sender, true);
	}
}
