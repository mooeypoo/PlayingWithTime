package io.github.mooeypoo.playingwithtime.configs;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Set;

import io.github.mooeypoo.playingwithtime.configs.interfaces.DefinitionConfigInterface;
import io.github.mooeypoo.playingwithtime.configs.interfaces.MainConfigInterface;

public class ConfigManager {
    private Path dataFolder;
	private String prefix;
	private ConfigLoader<MainConfigInterface> mainConfig = null;
	private HashMap<String, ConfigLoader<DefinitionConfigInterface>> configs = new HashMap<String, ConfigLoader<DefinitionConfigInterface>>();

	public ConfigManager(Path dataFolder, String prefix) throws ConfigurationException {
        this.dataFolder = dataFolder;
        this.prefix = prefix;
    }

	public void reload() throws ConfigurationException {
		// Main config
		if (this.mainConfig == null) {
			this.mainConfig = ConfigLoader.create(this.dataFolder, "config.yml", MainConfigInterface.class);
		}
		this.mainConfig.reloadConfig();
		
		// Group configs
		this.configs.clear();
		for (String groupName : this.mainConfig.getConfigData().definitions()) {
			ConfigLoader<DefinitionConfigInterface> groupConfig = ConfigLoader.create(
				dataFolder,
				this.prefix + "_" + groupName + ".yml",
				DefinitionConfigInterface.class
			);
			groupConfig.reloadConfig();
			this.configs.put(groupName, groupConfig);
		}
	}
	
	public ConfigLoader<MainConfigInterface> getMainConfig() {
		return this.mainConfig;
	}

	public Set<String> getDefinitionNames() {
		return this.configs.keySet();
	}
	
	public DefinitionConfigInterface getDefinitionData(String rankName) throws ConfigurationException {
		ConfigLoader<DefinitionConfigInterface> conf = this.configs.get(rankName);
		if (conf == null) {
			return null;
		}
		return conf.getConfigData();
	}

	public MainConfigInterface getMainConfigData() throws ConfigurationException {
		if (this.mainConfig == null) {
			return null;
		}
		return this.mainConfig.getConfigData();
	}
}
