package io.github.mooeypoo.playingwithtime;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayingWithTimeCommandExecutor implements CommandExecutor {
	private PlayingWithTime plugin;

	public PlayingWithTimeCommandExecutor(PlayingWithTime plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("playingwithtime")) {
			return false;
		}

		if (args.length == 0) {
			// TODO: Maybe output help?
			sender.sendMessage("[PlayingWithTime] Command `/playingwithtime` requires at least one more parameter.");
			return false;
		}

		if (
			// Process command
			args[0].equalsIgnoreCase("process") &&
			sender.hasPermission("playingwithtime.cmd.process")
		) {
			int counter = 0;
			// Go over all online users
			this.plugin.getLogger().info("[PlayingWithTime] Starting manual processing of online users...");
			for (Player player: Bukkit.getServer().getOnlinePlayers()) {
				if (!this.plugin.shouldRunForPlayer(player)) {
					continue;
				}
				
				this.plugin.getLogger().info("Processing for player: "+ player.getName());
				this.plugin.getActionManager().processPlayer(player);
				counter++;
			}
			
			this.plugin.getLogger().info("[PlayingWithTime] Processing finished for " + counter + " online players.");
		}

		return false;
	}

}
