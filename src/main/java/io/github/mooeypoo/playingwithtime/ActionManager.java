package io.github.mooeypoo.playingwithtime;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.mooeypoo.playingwithtime.configs.ConfigurationException;
import io.github.mooeypoo.playingwithtime.configs.interfaces.DefinitionConfigInterface;
import io.github.mooeypoo.playingwithtime.configs.interfaces.SubSectionGroupPermCommandsInterface;

public class ActionManager {
	
	private JavaPlugin plugin;
	private TimeCollector timeCollector;
	private HashMap<String, String> commands = new HashMap<String, String>();
	private static DecimalFormat timeMinuteFormat = new DecimalFormat("#.##");

	public ActionManager(JavaPlugin plugin) {
		this.plugin = plugin;
		this.timeCollector = new TimeCollector(this.plugin);
		
		this.reload();
	}
	
	/**
	 * Load or reload the system and all underlying data and prepare for processing.
	 */
	public void reload() {
		try {
			this.timeCollector.initialize();
		} catch (ProcessException e) {
			this.plugin.getLogger().warning("Processing error: " + e.getMessage());
		} catch (ConfigurationException e) {
			this.plugin.getLogger().warning("Configuration error: " + e.getMessage());
		}
		
		if (this.timeCollector.getMainConfigData() == null) {
			this.plugin.getLogger().warning("Could not load main configuration file. Please verify the file exists and is in valid yaml.");
			return;
		}
		SubSectionGroupPermCommandsInterface cmdGroups = this.timeCollector.getMainConfigData().commands_definition_add();
		this.commands.clear();
		if (ConditionChecker.isStringEmpty(cmdGroups.add_group())) {
			this.plugin.getLogger().warning("Main config does not have a command for adding group. All group actions will be skipped. Please review the main config file.");
		}
		if (ConditionChecker.isStringEmpty(cmdGroups.add_permission())) {
			this.plugin.getLogger().warning("Main config does not have a command for adding permissions. All permission actions will be skipped. Please review the main config file.");
		}
		// Store the commands to run to add groups/permissions
		this.commands.put("group", cmdGroups.add_group());
		this.commands.put("permission", cmdGroups.add_permission());
	}

	private void log(String output) {
		this.plugin.getLogger().info("[PlayingWithTime] " + output);
	}

	/**
	 * Process the list of add groups or permissions for the user
	 *
	 * @param player Requested player
	 * @param addList List of permissions or groups to apply
	 * @param isGroup Whether the list is adding groups (true) or permissions (false)
	 */
	private void processAddCommand(Player player, Set<String> addList, Boolean isGroup) {
		String useCommand = isGroup ? this.commands.get("group") : this.commands.get("permission");
		String typeStr = isGroup ? "group" : "permission";
		if (ConditionChecker.isStringEmpty(useCommand)) {
			return;
		}

		// Queue commands for adding groups
		for (String itemName : addList) {
			if (player.hasPermission(
					isGroup ? "group." + itemName.trim() : itemName.trim()
			)) {
				this.log(String.format(
					"(Skipping assignment) Player [%s] already has %s assignment [%s]",
					player.getName(),
					typeStr,
					itemName
				));
				continue;
			}

			// Apply group for user using the given command
			this.log(String.format(
					"Applying %s for player: [%s] -> %s",
					typeStr,
					player.getName(),
					itemName
			));

			// Schedule the command
			this.dispatchCommand(
				this.replaceCommandPlaceholders(useCommand, player.getName(), itemName, ConditionChecker.getPlayerTimeInMinutes(player))
			);
		}
	}
	/**
	 * Process a player's in-game time and actions
	 *
	 * @param player Requested player
	 */
	public void processPlayer(Player player) {
		ArrayList<DefinitionConfigInterface> definitions = this.timeCollector.getRelevantDefinitionsForPlayer(player);
		if (definitions.isEmpty()) {
			return;
		}
		Double playerTimeInGame = ConditionChecker.getPlayerTimeInMinutes(player);
		// Output log
		this.log(
			String.format(
				"[%s] time on server (%s mins) match %d relevant rules.",
				player.getName(),
				"" + timeMinuteFormat.format(playerTimeInGame),
				definitions.size()
			)
		);
		// Go over all definitions that are relevant for this player
		for (DefinitionConfigInterface def : definitions) {
			this.processAddCommand(player, def.add().groups(), true);
			this.processAddCommand(player, def.add().permissions(), false);

			// Queue custom_commands 
			for (String cmd : def.custom_commands()) {
				// Replace magic words:
				this.dispatchCommand(
					this.replaceCommandPlaceholders(cmd, player.getName(), "", playerTimeInGame)
				);
			}
		}

	}
	
	/**
	 * Dispatch raw console command to the server
	 * 
	 * @param cmd Command to dispatch
	 */
	private void dispatchCommand(String cmd) {
		final String runnableCommand = cmd;
		this.log("Invoking command: " + runnableCommand);
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), runnableCommand);
	}
	
	private String replaceCommandPlaceholders(String rawCommand, String playerName, String typeName, Double timeInGame) {
		String cmd = rawCommand;

		return cmd
			.replaceAll("%playername%", playerName)
			.replaceAll("%typename%", typeName)
			.replaceAll("%timeingame%", timeMinuteFormat.format(timeInGame));
	}
}
