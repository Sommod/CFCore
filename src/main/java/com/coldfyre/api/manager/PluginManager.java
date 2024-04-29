package com.coldfyre.api.manager;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Manager class for containing the Plugin structure. Contains built-in methods to help
 * accelerate the process of initializing a Plugin.
 * 
 * @author Sommod
 * @version 1.0
 *
 * @param <J> - Your Main plugin file
 */
public class PluginManager<J extends JavaPlugin> {
	
	protected J javaPlugin;
	
	public PluginManager(J plugin) {
		javaPlugin = plugin;
	}
	
	protected J getPlugin() { return javaPlugin; }

}
