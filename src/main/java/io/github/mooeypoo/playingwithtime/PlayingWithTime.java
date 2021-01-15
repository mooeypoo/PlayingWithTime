package io.github.mooeypoo.playingwithtime;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

public class PlayingWithTime extends JavaPlugin implements Listener {
	private ActionManager actionManager;
	private Boolean isTest = false;

	public PlayingWithTime() {
        super();
    }

	/**
	 * Instantiation for test purposes.
	 * @param loader
	 * @param description
	 * @param dataFolder
	 * @param file
	 */
    protected PlayingWithTime(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
        this.isTest = true;
    }

    @Override
	public void onEnable() {
		this.getLogger().info("Initializing PlayingWithTime configuration...");

		// Connect events
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(this, (this));

		this.actionManager = new ActionManager(this);
		this.getLogger().info("PlayingWithTime is enabled.");
	}

	@Override
	public void onDisable() {
		this.getLogger().info("PlayingWithTime is disabled.");
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!this.shouldRunForPlayer(event.getPlayer())) {
			return;
		}
		
		this.actionManager.processPlayer(event.getPlayer());
	}
	
	/**
	 * Method that checks whether any the process should run for
	 * the player. This means checking against permissions or existing ranks.
	 *
	 * @param player
	 * @return System should process this player
	 */
	private Boolean shouldRunForPlayer(Player player) {
		return (
			// Don't automatically run for tests
			!this.isTest &&
			// Player doesn't have the permission to be ignored
			!player.hasPermission("playingwithtime.ignore") 
		);
	}
}
