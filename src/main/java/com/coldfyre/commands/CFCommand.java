package com.coldfyre.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.coldfyre.CFCore;
import com.coldfyre.api.utilities.Util;

public class CFCommand implements CommandExecutor {
	private CFCore core;
	
	public CFCommand(CFCore core) {
		this.core = core;
		core.getCommand("CFCore").setExecutor(this);
		core.getCommand("CFCore").setTabCompleter(new CFCompleter());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		switch (args.length) {
		case 0:
			sender.sendMessage(Messages.BREAK.toString(),
					"§6Version: §r§7" + core.getDescription().getVersion(),
					"§6Authors: §r§7Sommod",
					"§6Description: §r§7A simple Library plugin",
					Messages.BREAK.toString());
			return true;
			
		case 1:
			if(Util.isAny(true, args[0], "version", "v")) {
				sender.sendMessage(Messages.BREAK.toString(),
						"§6Version: §r§7" + core.getDescription().getVersion(),
						"§6Authors: §r§7Sommod",
						"§6Description: §r§7A simple Library plugin",
						Messages.BREAK.toString());
			} else if(Util.isAny(true, args[0], "reload", "r", "rl")) {
				//TODO: Insert reload command when needed. Unneeded for 1.0
				sender.sendMessage(Messages.RELOAD_COMMAND.toString());
			} else if(Util.isAny(true, args[0], "help", "h")) {
				sender.sendMessage(Messages.BREAK.toString(),
						"§6/CFCore Help [Cmd]:§7 Displays the list of commands, or command details",
						"§6/CFCore Reload:§7 Reloads the Plugin",
						"§6/CFCore Version:§7 Shows plugin version information",
						Messages.BREAK.toString());
			} else
				sender.sendMessage(Messages.ERROR_COMMAND.toString());
			return true;
			
		case 2:
			if(Util.isAny(true, args[0], "help", "h")) {
				if(Util.isAny(true, args[1], "help", "h"))
					sender.sendMessage(Messages.BREAK.toString(), Messages.HELP_HELP_COMMAND.toString(), Messages.BREAK.toString());
				else if(Util.isAny(true, args[1], "reload", "r"))
					sender.sendMessage(Messages.BREAK.toString(), Messages.HELP_RELOAD_COMMAND.toString(), Messages.BREAK.toString());
				else if(Util.isAny(true, args[1], "version", "v"))
					sender.sendMessage(Messages.BREAK.toString(), Messages.HELP_VERSION_COMMAND.toString(), Messages.BREAK.toString());
				else
					sender.sendMessage(Messages.BREAK.toString(), Messages.ERROR_COMMAND.toString(), Messages.BREAK.toString());
			} else
				sender.sendMessage(Messages.ERROR_COMMAND.toString());
			return true;
			
		default:
			sender.sendMessage(Messages.ERROR_COMMAND.toString());
			return false;
		}
	}

}
