package com.coldfyre.commands;

public enum Messages {
	
	ERROR_COMMAND("§c§[CFCore] Error, no command with that input. Use §b/CFCore Help§c for list of commands."), RELOAD_COMMAND("§a[CFCore] Plugin Reloaded."), BREAK("§6====== [CFCore] ======"),
	HELP_HELP_COMMAND("§6Help §8[§ch§8]:§7 Lists all the commands as well as the ability to show details about each command (such as this). This also includes a list of short-cut words that can be used as well instead of typing the entire command."),
	HELP_RELOAD_COMMAND("§6Reload §8[§cR§8 | §crl§8]:§7 Reloads the plugin without the need restart the server."),
	HELP_VERSION_COMMAND("§6Version §8[§cv§8]:§7 Shows the plugins version, description and other details.");
	
	private String name;
	
	private Messages(String value) { name = value; }
	
	@Override
	public String toString() { return name;	}

}
