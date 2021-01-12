package io.github.mooeypoo.playingwithtime;

import java.util.ArrayList;
import java.util.HashMap;

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
		if (this.isEmpty(cmdGroups.add_group())) {
			this.plugin.getLogger().warning("Main config does not have a command for adding group. All group actions will be skipped. Please review the main config file.");
		}
		if (this.isEmpty(cmdGroups.add_permission())) {
			this.plugin.getLogger().warning("Main config does not have a command for adding permissions. All permission actions will be skipped. Please review the main config file.");
		}
		// Store the commands to run to add groups/permissions
		this.commands.put("group", cmdGroups.add_group());
		this.commands.put("permission", cmdGroups.add_permission());
	}
	
	private Boolean isEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}

	public void processPlayer(Player player) {
		ArrayList<DefinitionConfigInterface> definitions = this.timeCollector.getRelevantDefinitionsForPlayer(player);
		if (definitions.isEmpty()) {
			return;
		}
		String addGroupCommand = this.commands.get("group");
		String addPermCommand = this.commands.get("permission");

		// Output log
		this.plugin.getLogger().info(
				"Player \"" + player.getName() + "\" time on server (" +
				this.timeCollector.getPlayerTimeInMinutes(player) + 
				"mins) matched given update rules. Processing actions for [" +
				definitions.size() + "] relevant definitions."
		);
		// Go over all definitions that are relevant for this player
		for (DefinitionConfigInterface def : definitions) {
			if (!this.isEmpty(addGroupCommand)) {
				// Queue commands for adding groups
				for (String groupName : def.add().groups()) {
					if (player.hasPermission("group." + groupName.trim())) {
						this.plugin.getLogger().info(String.format("Skipping. Player [%s] is already in group [%s]", player.getName(), groupName));
						// If the player already has this permission, skip
						continue;
					}

					// Apply group for user using the given command
					this.plugin.getLogger().info(String.format("Applying group for player: [%s] -> %s", player.getName(), groupName));
						// Schedule the command
						this.dispatchCommand(
							this.replaceCommandPlaceholders(addGroupCommand, player.getName(), groupName)
						);
				}
			}

			if (!this.isEmpty(addPermCommand)) {
				// Queue commands for adding permissions
				for (String permName : def.add().permissions()) {
					if (player.hasPermission(permName.trim())) {
						this.plugin.getLogger().info(String.format("Skipping. Player [%s] already has permission [%s]", player.getName(), permName));
						// If the player already has this permission, skip
						continue;
					}
					// Apply group for user using the given command
					this.plugin.getLogger().info(String.format("Applying permission for player: [%s] -> %s", player.getName(), permName));
						// Schedule the command
						this.dispatchCommand(
							this.replaceCommandPlaceholders(addPermCommand, player.getName(), permName)
						);
				}
			}

			// Queue custom_commands 
			for (String cmd : def.custom_commands()) {
				// Replace magic words:
				this.dispatchCommand(
					this.replaceCommandPlaceholders(cmd, player.getName(), "")
				);
			}
		}

	}
	
	private void dispatchCommand(String cmd) {
		final String runnableCommand = cmd;
		this.plugin.getLogger().info("PlayingWithTime invoking command: " + runnableCommand);
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), runnableCommand);
	}
	
	private String replaceCommandPlaceholders(String rawCommand, String playerName, String typeName) {
		String cmd = rawCommand;

		return cmd
			.replaceAll("%playername%", playerName)
			.replaceAll("%typename%", typeName);
	}
}
