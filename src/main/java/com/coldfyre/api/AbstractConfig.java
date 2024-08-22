package com.coldfyre.api;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import com.coldfyre.api.manager.FilesManager;
import com.coldfyre.api.utilities.InvalidFileFormatException;

/**
 * Abstract class that opens and obtains the information of the given file data. This
 * only supports YML files that Bukkit (Spigot) uses. Each YMl configuration file that
 * exists within the plugin should extend this class and have the specific methods and
 * data for use. 
 * 
 * @author Sommod
 * @version 1.0
 *
 */
public abstract class AbstractConfig {
	
	protected YamlConfiguration bukkitConfig;
	protected File bukkitFile;
	
	public AbstractConfig(String path) {
		this(new File(path));
	}
	
	public AbstractConfig(File ymlFile) {
		bukkitFile = ymlFile;
		
		try {
			if(!FilenameUtils.getExtension(bukkitFile.getAbsolutePath()).equals("yml"))
				throw new InvalidFileFormatException("The given file is not a YML file and cannot be configured.");
		} catch (Exception e) {
			FilesManager.LogException(e);
		}
		
		bukkitConfig = YamlConfiguration.loadConfiguration(bukkitFile);
	}
	
	/**
	 * Gets the stored File object of this config.
	 * 
	 * @return {@link File}
	 */
	protected File getFile() { return bukkitFile; }
	
	/**
	 * Gets the 'Raw' stored YamlConfiguration object for this object. Note that
	 * any changes done to this file will NOT reflect to the data stored within
	 * this configuration object.
	 * 
	 * @return {@link YamlConfigurtion}
	 */
	protected YamlConfiguration getYamlConfiguration() { return bukkitConfig; }
}
