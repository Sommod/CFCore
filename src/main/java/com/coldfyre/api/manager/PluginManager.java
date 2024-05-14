package com.coldfyre.api.manager;

import java.util.logging.Level;

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
	protected String[] header, footer;
	
	public PluginManager(J plugin) {
		javaPlugin = plugin;
	}
	
	protected J getPlugin() { return javaPlugin; }
	
	public String[] getHeader() { return header; }
	public String[] getFooter() { return footer; }
	public void printHeader() { print(header); }
	public void printFooter() { print(footer); }
	public void setHeader(String... header) { this.header = header; }
	public void setFooter(String... footer) { this.footer = footer; }
	
	public void print(String... lines) { javaPlugin.getServer().getConsoleSender().sendMessage(lines); }
	public void printWarning(String... lines) { printLogger(Level.WARNING, lines); }
	public void printError(String... lines) { printLogger(Level.SEVERE, lines); }
	private void printLogger(Level lvl, String... lines) {
		for(String s : lines)
			javaPlugin.getLogger().log(lvl, s);
	}
}
