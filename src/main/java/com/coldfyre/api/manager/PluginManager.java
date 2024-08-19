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
	
	/**
	 * Stores the given plugin paramater for method use.
	 * 
	 * @param plugin - Your plugin
	 */
	public PluginManager(J plugin) {
		javaPlugin = plugin;
		
		header = new String[] {"§6============ [" + javaPlugin.getName() + "] ============"};
		footer = new String[] {"§6==============" + UpdateFooter(javaPlugin.getName().length()) + "=============="};
	}
	
	// Updates the footer length to the given int length
	private String UpdateFooter(int value) {
		String ret = "";
		
		for(; value > 0; value--)
			ret += "=";
		
		return ret;
	}
	
	/**
	 * Gets the Plugin Object.
	 * 
	 * @return {@link JavaPlugin}
	 */
	public J getPlugin() { return javaPlugin; }
	
	/**
	 * Gets the Header for console display use.
	 * 
	 * @return String
	 */
	public String[] getHeader() { return header; }
	
	/**
	 * Gets the Footer for console display use.
	 * 
	 * @return String
	 */
	public String[] getFooter() { return footer; }
	
	/**
	 * Outputs the header to the console.
	 */
	public void printHeader() { print(header); }
	
	/**
	 * Outputs the footer to the console.
	 */
	public void printFooter() { print(footer); }
	
	/**
	 * Sets the header to the given parameterized set of strings.
	 * 
	 * @param header - Single or set of Strings
	 */
	public void setHeader(String... header) { this.header = header; }
	
	/**
	 * Sets the footer to the given parameterized set of strings.
	 * 
	 * @param footer - Single of set of String
	 */
	public void setFooter(String... footer) { this.footer = footer; }
	
	/**
	 * Displays the given set of Strings to the console.
	 * 
	 * @param lines - Single or set of strings
	 */
	public void print(String... lines) { javaPlugin.getServer().getConsoleSender().sendMessage(lines); }
	
	/**
	 * Displays the given set of strings to the console as a warning system.
	 * 
	 * @param lines - Single or set of strings
	 */
	public void printWarning(String... lines) { printLogger(Level.WARNING, lines); }
	
	/**
	 * Displays the given set of strings to the console as an error system.
	 * 
	 * @param lines - Single or set of strings
	 */
	public void printError(String... lines) { printLogger(Level.SEVERE, lines); }
	
	// Prints the given set of strings with the given Logger Level
	private void printLogger(Level lvl, String... lines) {
		for(String s : lines)
			javaPlugin.getLogger().log(lvl, s);
	}
}
