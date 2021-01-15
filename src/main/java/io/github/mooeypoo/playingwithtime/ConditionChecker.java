package io.github.mooeypoo.playingwithtime;

import java.util.Set;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class ConditionChecker {
	/**
	 * Get the player time in-game on this server, in minutes.
	 * This is an estimate done by dividing by the ideal ticks-per-second count of 20.
	 *
	 * @param player Requested player
	 * @return Times played on the server, in minutes
	 */
	public static double getPlayerTimeInMinutes(Player player) {
		int ticks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
		double timeEstimateSeconds = ticks / 20.0; // Migrate from ticks to estimation of seconds; 20 ticks per second
		return timeEstimateSeconds / 60.0;
	}
	
	/**
	 * Check if the player has all of the specified permissions or groups
	 *
	 * @param conditionList List of permissions or groups to test 
	 * @param player Player to test against
	 * @param isGroup The values of the list are groups
	 * @return Player has all requested values
	 */
	public static Boolean doesPlayerHaveAll(Set<String> conditionList, Player player, Boolean isGroup) {
		for (String checkName : conditionList) {
			if (!player.hasPermission(isGroup ? "group." + checkName.trim() : checkName.trim())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if the player does not have any of the specified permissions or groups
	 *
	 * @param conditionList List of permissions or groups to test 
	 * @param player Player to test against
	 * @param isGroup The values of the list are groups
	 * @return Player has none requested values
	 */
	public static Boolean doesPlayerHaveNone(Set<String> conditionList, Player player, Boolean isGroup) {
		for (String checkName : conditionList) {
			if (player.hasPermission(isGroup ? "group." + checkName.trim() : checkName.trim())) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Check if a string variable is empty
	 *
	 * @param str Requested variable
	 * @return Variable is empty
	 */
	public static Boolean isStringEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}

}
