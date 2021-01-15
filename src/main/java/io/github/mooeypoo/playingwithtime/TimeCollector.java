package io.github.mooeypoo.playingwithtime;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.mooeypoo.playingwithtime.configs.ConfigManager;
import io.github.mooeypoo.playingwithtime.configs.ConfigurationException;
import io.github.mooeypoo.playingwithtime.configs.interfaces.DefinitionConfigInterface;
import io.github.mooeypoo.playingwithtime.configs.interfaces.MainConfigInterface;

public class TimeCollector {
	private JavaPlugin plugin;
	private ConfigManager configManager;
	private HashMap<Double, ArrayList<DefinitionConfigInterface>> mapByTime = new HashMap<Double, ArrayList<DefinitionConfigInterface>>();

	public TimeCollector(JavaPlugin plugin) {
		this.plugin = plugin;

		try {
			this.configManager = new ConfigManager(Paths.get(this.plugin.getDataFolder().getPath()), "PlayingWithTime_rank");
		} catch (ConfigurationException e) {
			this.plugin.getLogger().warning("Initiation aborted for PlayingWithTime. Error in configuration file '" + e.getConfigFileName() + "': " + e.getMessage());
		}
	}
	
	/**
	 * Initialize the time collector system by gathering all available definitions
	 * and mapping them to a useful time-based map.
	 * 
	 * @throws ProcessException
	 * @throws ConfigurationException
	 */
	public void initialize() throws ProcessException, ConfigurationException {
		ArrayList<String> errors = new ArrayList<String>();

		// Reset and map config values
		this.configManager.reload();
		this.mapByTime.clear();
		
		for (String def : this.configManager.getDefinitionNames()) {
			DefinitionConfigInterface data = null;
			try {
				data = this.configManager.getDefinitionData(def);
			} catch (ConfigurationException e) {
				// Something is wrong with this definition. Skip
				errors.add("Could not process definition for '" + def + ":' " + e.getMessage());
			}
			
			Double actualTime = 0.0;
			try {
				actualTime = Double.parseDouble(data.time());
			} catch (NumberFormatException e) {
				// Time is not formatted correctly, we have to ignore
				errors.add("Malformed time in '" + def + ":' " + e.getMessage());
			}

			if (!errors.isEmpty()) {
				continue;
			}

			// Now that we have the actual time, add that to the map:
			ArrayList<DefinitionConfigInterface> listForTime = this.mapByTime.get(actualTime);
			if (listForTime == null) {
				// Add the ArrayList if it's the first time
				listForTime = new ArrayList<DefinitionConfigInterface>(); 
				this.mapByTime.put(actualTime, listForTime);
			}
			listForTime.add(data);
		}

		// If there were errors, throw them for logging
		if (!errors.isEmpty()) {
			throw ProcessException.create(errors);
		}
	}
	
	/**
	 * Check whether all prerequisites (musthaves and canthaves) pass for this player
	 *
	 * @param player Requested player
	 * @param def Configuration definition
	 * @return Prerequisites pass
	 */
	private Boolean doPrerequisitesPass(Player player, DefinitionConfigInterface def) {
		return (
			ConditionChecker.doesPlayerHaveAll(def.musthave().groups(), player, true) &&
			ConditionChecker.doesPlayerHaveAll(def.musthave().permissions(), player, false) &&
			ConditionChecker.doesPlayerHaveNone(def.canthave().groups(), player, true) &&
			ConditionChecker.doesPlayerHaveNone(def.canthave().permissions(), player, false)
		);
	}
	/**
	 * Get a list of the definitions that apply to the user from all measurable rules:
	 * - The user's in-game play time on the server matches the definition
	 * - All the prerequisites groups an permissions apply to the user (if they exist)
	 *
	 * @param player Requested player
	 * @return A list of configuration definitions that are relevant for this player.
	 */
	public ArrayList<DefinitionConfigInterface> getRelevantDefinitionsForPlayer(Player player) {
		ArrayList<DefinitionConfigInterface> result = new ArrayList<DefinitionConfigInterface>();
		
		// Get relevant times from the map:
		for (Double timeInMap : this.mapByTime.keySet()) {
			// Check if time is valid for the user (user time is bigger than or equal to time stated)
			if (ConditionChecker.getPlayerTimeInMinutes(player) < timeInMap) {
				continue;
			}

			// Check if prerequisites are valid
			for (DefinitionConfigInterface def : this.mapByTime.get(timeInMap)) {
				if (this.doPrerequisitesPass(player, def)) {
					// Valid! add it to the result list
					result.add(def);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Get the main configuration data
	 * @return Main configuration data
	 */
	public MainConfigInterface getMainConfigData() {
		try {
			return this.configManager.getMainConfigData();
		} catch (ConfigurationException e) {
			return null;
		}
	}
}
