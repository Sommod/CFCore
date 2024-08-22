package com.coldfyre.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.coldfyre.api.utilities.Util;

public class TabCompleterBuilder {
	private TabCompleterCreator creator;
	
	/**
	 * Creates a new Tab Builder for the given command. This
	 * should only be the base command, not argument commands.
	 * The Builder will contain methods for argument commands.
	 * 
	 * @param cmd - Command to make a TabCompleter for
	 */
	public TabCompleterBuilder(String cmd) { creator = new TabCompleterCreator(cmd); }
	
	/**
	 * Builds the TabCompleter and returns the Object to be used for a plugin command.
	 * @return
	 */
	public TabCompleter build() { return creator; }
	
	/**
	 * Similar to the {@link #build()} method, this will create the TabCompleter while also
	 * passing the plugin will attach the given TabCompleter to the command that was used
	 * to initialize this Builder.
	 * 
	 * @param plugin - Plugin to attach Completer to
	 * @return TabCompleter - The TabCompleter from the builder
	 */
	public TabCompleter buildAndApply(JavaPlugin plugin) {
		plugin.getCommand(creator.cmd).setTabCompleter(creator);
		
		return creator;
	}
	
	// checks that the given command in the position is there
	private boolean ensure(int argPos, String command) {
		if(!creator.commands.containsKey(argPos + "_" + command)) {
			addCommand(argPos, command);
			return false;
		}
		
		return true;
	}
	
	private Players.PLAYERS_LETTER[] removeDuplicateLetters(Players.PLAYERS_LETTER[] ltrs) {
		List<Players.PLAYERS_LETTER> arrayHolder = new ArrayList<Players.PLAYERS_LETTER>();
		
		for(Players.PLAYERS_LETTER ltr : ltrs) {
			if(!arrayHolder.contains(ltr))
				arrayHolder.add(ltr);
		}
		
		return (Players.PLAYERS_LETTER[]) arrayHolder.toArray();
	}
	
	/**
	 * Adds a command (argument). To ensure no overwriting of the commands occur, the
	 * argument positioning should start at 1. This ensure the base command and an
	 * argument command do not overwrite each other.<br>
	 * {@code /cmd {1} {2} {3}...}
	 * 
	 * @param argPos - Position to add a new command
	 * @param command - Command to create options for
	 * @return True - If command was added successfully
	 */
	public boolean addCommand(int argPos, String command) {
		if(creator.commands.containsKey(argPos + "_" + command))
			return false;
		
		creator.commands.put(argPos + "_" + command, new ArrayList<String>());
		
		return true;
	}
	
	/**
	 * Adds an option to the given command. To add an option to the base command, give
	 * an argument position of 0 and the base command (the string value passed during initialization).
	 * 
	 * @param argPos - position of argument
	 * @param command - command to add option to
	 * @param option - String option to add
	 */
	public void addTabOption(int argPos, String command, String option) {
		ensure(argPos, command);
		
		List<String> options = creator.commands.get(argPos + "_" + command);
		
		options.add(option);
		creator.commands.put(argPos + "_" + command, options);
	}
	
	/**
	 * Adds a {@link Players} option to the command.
	 * 
	 * @param argPos - Position of the command
	 * @param command - Command to add option to
	 * @param playerFlag - {@link Players} Flag to add
	 */
	public void addTabOption(int argPos, String command, Players playerFlag) {
		ensure(argPos, command);
		
		List<String> tabList = creator.commands.get(argPos + "_" + command);
		
		for(String line : tabList) {
			if(line.startsWith("playerinfo+" + playerFlag.name()))
				return;
		}
		
		tabList.add("playerinfo+" + playerFlag.name());
		creator.commands.put(argPos + "_" + command, tabList);
	}
	
	/**
	 * Adds a tab option to a command with the specific letters of player names based on the
	 * {@link Players} set of flags. Any duplicate letters are automatically removed to avoid multiple
	 * results of the same outcome.
	 * 
	 * @param argPos - Position of the argument
	 * @param command - argument command
	 * @param playerFlag - {@link Players} flag
	 * @param letter - Letter(s) to add
	 */
	public void addTabOption(int argPos, String command, Players playerFlag, Players.PLAYERS_LETTER... letter) {
		if(letter.length == 0) {
			addTabOption(argPos, command, playerFlag);
			return;
		}
		
		List<String> tabList = creator.commands.get(argPos + "_" + command);
		int loc = 0;
		
		for(String line : tabList) {
			if(line.startsWith("playerinfo+" + playerFlag.name())) {
				for(Players.PLAYERS_LETTER ltr : letter) {
					String[] details = line.split("+");
					
					if(details.length == 2)
						line += ("+" + ltr.name());
					else {
						boolean hit = false;
						
						for(String l : details) {
							if(l.equals(ltr.name())) {
								hit = true;
								break;
							}
						}
						
						if(!hit) {
							line += ("+" + ltr.name());
						}
					}
				}
				
				tabList.set(loc, line);
				creator.commands.put(argPos + "_" + command, tabList);
				return;
			}
			
			loc++;
		}
		
		String option = "playerinfo+" + playerFlag.name();
		
		for(Players.PLAYERS_LETTER ltrs : removeDuplicateLetters(letter))
			option += ("+" + ltrs.name());
		
		tabList.add(option);
		creator.commands.put(argPos + "_" + command, tabList);
	}
	
	/**
	 * Removes a Tab Option, from the tab list of options for the given command.
	 * 
	 * @param argPos - Position of the command
	 * @param command - Command
	 * @param option - Line to remove
	 */
	public void removeTabOption(int argPos, String command, int option) {
		if(!ensure(argPos, argPos == 0 ? creator.cmd.toLowerCase() : command))
			return;
		
		List<String> tabList = creator.commands.get(argPos + "_" + command);
		if(tabList.isEmpty())
			return;
		
		tabList.remove(option);
		creator.commands.put(argPos + "_" + command, tabList);
	}
	
	/**
	 * Removes a Tab Option, from the tab list of options for the given command.
	 * 
	 * @param argPos - Position of command
	 * @param command - Command
	 * @param option - Option to remove
	 */
	public void removeTabOption(int argPos, String command, String option) {
		if(!ensure(argPos, argPos == 0 ? creator.cmd.toLowerCase() : command))
			return;
		
		List<String> tabList = creator.commands.get(argPos + "_" + command);
		if(tabList.isEmpty())
			return;
		
		tabList.remove(option);
		creator.commands.put(argPos + "_" + command, tabList);
	}
	
	/**
	 * Removes the {@link Players} Flag Tab option from the command.
	 * 
	 * @param argPos - Position of command
	 * @param command - Command
	 * @param playerFlag - {@link Players} Flag to remove
	 */
	public void removeTabOption(int argPos, String command, Players playerFlag) {
		if(!ensure(argPos, argPos == 0 ? creator.cmd.toLowerCase() : command))
			return;
		
		List<String> tabList = creator.commands.get(argPos + "_" + command);
		if(tabList.isEmpty())
			return;
		
		int hit = -1;
		
		for(int i = 0; i < tabList.size(); i++) {
			if(tabList.get(i).startsWith("playerinfo+" + playerFlag.name())) {
				hit = i;
				break;
			}
		}
		
		if(hit != -1)
			tabList.remove(hit);
		
		creator.commands.put(argPos + "_" + command, tabList);
	}
	
	/**
	 * Removes the set of letters for the {@link Players} flag.
	 * 
	 * @param argPos - Position of command
	 * @param command - Command
	 * @param playerFlag - {@link Players} Flag
	 * @param letter - Letters to remove
	 */
	public void removeTabOption(int argPos, String command, Players playerFlag, Players.PLAYERS_LETTER... letter) {
		if(!ensure(argPos, argPos == 0 ? creator.cmd.toLowerCase() : command))
			return;
		else if(letter.length == 0) {
			removeTabOption(argPos, command, playerFlag);
			return;
		}
		
		List<String> tabList = creator.commands.get(argPos + "_" + command);
		if(tabList.isEmpty())
			return;
		
		int loc = 0;
		String edited = "";
		
		for(String line : tabList) {
			if(line.startsWith("playerinfo+" + playerFlag.name())) {
				List<String> alter = Arrays.asList(line.split("+"));
				
				for(Players.PLAYERS_LETTER ltr : letter)
					alter.remove(ltr.name());
				edited = Util.combineArray((String[]) alter.toArray(), "+");
				break;
			}
			
			loc++;
		}
		
		if(loc != tabList.size()) {
			tabList.remove(loc);
			tabList.add(loc, edited);
		}
		
		creator.commands.put(argPos + "_" + command, tabList);
	}
	
	/**
	 * Removes the command from the TabCompleter Builder and any Tab Options added to said command.
	 * 
	 * @param argPos - Position of command
	 * @param command - Command to remove
	 */
	public void removeCommand(int argPos, String command) {
		creator.commands.remove(argPos + "_" + command);
	}
	
	/**
	 * Checks if the command exists within this Builder.
	 * 
	 * @param argPos - Position of command
	 * @param command - Command to check
	 * @return True - If command exists
	 */
	public boolean hasCommand(int argPos, String command) {
		return creator.commands.get(argPos + "_" + command) != null;
	}
	
	/**
	 * Checks if the given command has any Tab Options.
	 * 
	 * @param argPos - Position of the command
	 * @param command - Command to check
	 * @return True - If list of options is NOT empty
	 */
	public boolean hasTabOptions(int argPos, String command) {
		return hasCommand(argPos, command) && !creator.commands.get(argPos + "_" + command).isEmpty();
	}
	
	/**
	 * Checks if the option exists for the given command. This uses the {@link String#contains(CharSequence)} method for searching.
	 * 
	 * @param argPos - Position of command
	 * @param command - Command to check
	 * @param option - Option to check for
	 * @return True - If option already exists
	 */
	public boolean hasTabOptions(int argPos, String command, String option) {
		if(hasTabOptions(argPos, command)) {
			List<String> temp = creator.commands.get(argPos + "_" + command);
			
			for(String s : temp) {
				if(s.contains(option))
					return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the command contains the {@link Players} Flag within its list of options. This
	 * does not determine if the Flag uses specific Letters, only if the flag itself exists. To
	 * determine if a specific Letter for the flag exists, use the other boolean method for letters.
	 * 
	 * @param argPos - Position of command
	 * @param command - Command to check
	 * @param playerFlag - {@link Players} Flag to check for
	 * @return True - If Flag (base) exists
	 */
	public boolean hasTabOptions(int argPos, String command, Players playerFlag) {
		return hasTabOptions(argPos, command, playerFlag.name());
	}
	
	/**
	 * Checks if the command contains the specified letter for the given {@link Players} flag. If the
	 * letter does not exist, then this will return false.
	 * 
	 * @param argPos - Position of Command
	 * @param command - Command
	 * @param playerFlag - {@link Players} Flag
	 * @param ltr - Letter to check for
	 * @return True - If letter exist for given Command/Flag
	 */
	public boolean hasTabOptions(int argPos, String command, Players playerFlag, Players.PLAYERS_LETTER ltr) {
		if(hasTabOptions(argPos, command)) {
			List<String> temp = creator.commands.get(argPos + "_" + command);
			
			for(String line : temp) {
				if(line.contains(playerFlag.name()) && line.split(playerFlag.name())[1].contains(ltr.name()))
					return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the command contains the specified letters for the given {@link Players} flag. ALL letters must
	 * be found for this to return true; meaning if a single letter is missing, then this will return false.
	 * 
	 * @param argPos - Position of Command
	 * @param command - Command
	 * @param playerFlag - {@link Players} Flag
	 * @param ltrs - Letters to check for
	 * @return True - If and only if ALL letters exist, otherwise false
	 */
	public boolean hasTabOptions(int argPos, String command, Players playerFlag, Players.PLAYERS_LETTER[] ltrs) {
		for(Players.PLAYERS_LETTER ltr : ltrs) {
			if(!hasTabOptions(argPos, command, playerFlag, ltr))
				return false;
		}
		
		return true;
	}
	
	/**
	 * Sets the tab options for a given command.
	 * 
	 * @param argPos - Position of Command
	 * @param command - Command
	 * @param options - List of options for the command
	 */
	public void setTabOptions(int argPos, String command, List<String> options) {
		ensure(argPos, argPos == 0 ? creator.cmd.toLowerCase() : command);
		
		creator.commands.put(argPos + "_" + command, options);
	}
	
	/**
	 * This sets the entire Collection of Tab Options for all commands. This should NOT be used unless
	 * the correct format of command handling is known. If an incorrect format is given, then this will break.
	 * 
	 * @param tabCompleter - Map of ALL commands and Tab Options
	 */
	@Deprecated
	public void setTabOptions(Map<String, List<String>> tabCompleter) { creator.commands = tabCompleter; }
	
	/**
	 * Clears ALL commands and Tab Options.
	 */
	public void clear() { creator.commands.clear(); }
	
	/**
	 * Clears all Tab Options for the given Command.
	 * 
	 * @param argPos - Position of command
	 * @param command - Command
	 */
	public void clear(int argPos, String command) { creator.commands.put(argPos + "_" + command, new ArrayList<String>()); }
	
	/**
	 * Gets the size of the Tab Options List. This does not account for all the dynamic options, just the count
	 * that A dynamic option exists. (i.e. "Online Players" Flag is dynamic, this will only return 1 rather than the number of players that are online).
	 * 
	 * @param argPos - Position of Command
	 * @param command - Command
	 * @return INT - size of Tab Option list
	 */
	public int size(int argPos, String command) {
		return creator.commands.containsKey(argPos +"_" + command) ? creator.commands.get(argPos + "_" + command).size() : -1;
	}

/*********************************** CLASS SEPARATOR *************************************/
	
	/**
	 * Enumeration class used for handling dynamic tab options based on in-the-moment information. This class has several
	 * options that allow for different types of players as well as including an internal option for narrowing the dynamic
	 * to specific letter categories.
	 * 
	 * @author Sommod
	 * @version 1.0
	 *
	 */
	public enum Players {
		
		/**
		 * Gets the entire list of ALL players that joined the server. Note, this should NOT be commonly used as the more
		 * players that join the server, the longer this list will be.
		 */
		PLAYERS_ALL {
			@Override
			public List<OfflinePlayer> getPlayers(PLAYERS_LETTER... ltr) {
				if(ltr.length == 0)
					return getAllPlayers();
				
				List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
				
				for(OfflinePlayer off : getAllPlayers()) {
					for(PLAYERS_LETTER l : ltr) {
						if(off.getName().toLowerCase().startsWith(l.toString()) || off.getName().toLowerCase().startsWith("_" + l.toString()))
							players.add(off);
					}
				}
				
				return players;
			}
		},
		
		/**
		 * Gets a list of all currently online players of the server.
		 */
		PLAYERS_ONLINE {
			@Override
			public List<OfflinePlayer> getPlayers(PLAYERS_LETTER... ltr) {
				if(ltr.length == 0)
					return new ArrayList<OfflinePlayer>(getAllOnlinePlayers());
				
				List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
				
				for(PLAYERS_LETTER l : ltr) {
					for(Player on : getAllOnlinePlayers()) {
						if(on.getName().toLowerCase().startsWith(l.toString()) || on.getName().toLowerCase().startsWith("_" + l.toString()))
							players.add(on);
					}
				}
				
				return players;
			}
		},
		
		/**
		 * Gets the list of ONLY offline players of the server. If a player is online, then this will exclude them.
		 */
		PLAYERS_OFFLINE {
			@Override
			public List<OfflinePlayer> getPlayers(PLAYERS_LETTER... ltr) {
				if(ltr.length == 0)
					return getAllOfflinePlayers();
				
				List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
				
				for(OfflinePlayer off : getAllOfflinePlayers()) {
					for(PLAYERS_LETTER l : ltr) {
						if(off.getName().toLowerCase().startsWith(l.toString()) || off.getName().toLowerCase().startsWith("_" + l.toString()))
							players.add(off);
					}
				}
				
				return players;
			}
		},
		
		/**
		 * Gets the list of players that are not hidden from sight. This may not be an accurate collection of players as
		 * other plugins and manipulate the visual of players, as such this should be used less frequent.  
		 */
		PLAYERS_NO_HIDDEN {
			@Override
			public List<OfflinePlayer> getPlayers(PLAYERS_LETTER... ltr) {
				List<OfflinePlayer> players;
				
				if(ltr.length == 0) {
					players = new ArrayList<OfflinePlayer>(getAllOnlinePlayers());
					players.removeAll(getAllHiddenPlayers());
					
					return players;
				}
				
				players = new ArrayList<OfflinePlayer>();
				
				for(Player p : getAllOnlinePlayers()) {
					for(PLAYERS_LETTER l : ltr) {
						if((p.getName().toLowerCase().startsWith(l.toString()) || p.getName().toLowerCase().startsWith("_" + l.toString())) && !getAllHiddenPlayers().contains(p)) {
							players.add(p);
							break;
						}
					}
				}
				
				return players;
			}
		},
		
		/**
		 * Similar to {@link #PLAYERS_NO_HIDDEN}, this will get a list of ONLY hidden players, but the finding of hiddening players is
		 * not strongly selected. As such, some results may vary based on other plugin handling of player visuals.
		 */
		PLAYERS_HIDDEN_ONLY {
			@Override
			public List<OfflinePlayer> getPlayers(PLAYERS_LETTER... ltr) {
				if(ltr.length == 0)
					return getAllHiddenPlayers();
				
				List<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
				
				for(OfflinePlayer p : getAllHiddenPlayers()) {
					for(PLAYERS_LETTER l : ltr) {
						if(p.getName().toLowerCase().startsWith("_" + l.toString()) || p.getName().toLowerCase().startsWith(l.toString()))
							players.add(p);
					}
				}
				
				return players;
			}
		};
		
		/**
		 * Gets the list of players of the Player flag. Additionally, if an array of letters are provided, then collection of players
		 * will reduce to the given letters.
		 * 
		 * @param ltr - Array of Letters
		 * @return {@link List}<{@link OfflinePlayer}> - List of Players
		 */
		public abstract List<OfflinePlayer> getPlayers(PLAYERS_LETTER... ltr);
		
		public static Players getFlag(String value) {
			for(Players tf : values()) {
				if(tf.name().equals(value.toUpperCase()))
					return tf;
			}
			
			return null;
		}
		
		// Gets All players of the server
		private static List<OfflinePlayer> getAllPlayers() { return Arrays.asList(Bukkit.getServer().getOfflinePlayers()); }
		
		// Gets All online players of the server
		private static Collection<? extends Player> getAllOnlinePlayers() { return Bukkit.getServer().getOnlinePlayers(); }
		
		// Gets all OfflinePlayers of the server
		private static List<OfflinePlayer> getAllOfflinePlayers() {
			List<OfflinePlayer> players =  Arrays.asList(Bukkit.getServer().getOfflinePlayers());
			players.removeAll(Bukkit.getServer().getOnlinePlayers());
			
			return players;
		}
		
		// Gets all Hidden players on the server
		private static List<OfflinePlayer> getAllHiddenPlayers() {
			List<OfflinePlayer> all = new ArrayList<OfflinePlayer>();
			
			for(Player outter : getAllOnlinePlayers()) {
				for(Player inner : getAllOnlinePlayers()) {
					if(!(outter.equals(inner) || outter.canSee(inner) || all.contains(inner)))
						all.add(inner);
				}
			}
			
			return all;
		}
		
		/**
		 * Inner Enumeration of Letters used for narrowing and localizing the {@link Players} flags based on
		 * an alphabetical category.
		 * 
		 * @author Sommod
		 * @version 1.0
		 *
		 */
		public enum PLAYERS_LETTER {
			A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z;
			
			/**
			 * Returns the letter as Lowercase.
			 */
			@Override
			public String toString() { return name().toLowerCase(); }
			
			/**
			 * Gets the letter based on the char input.
			 * 
			 * @param value - character to get
			 * @return Letter - if found, otherwise null
			 */
			public static PLAYERS_LETTER getLetter(char value) { return getLetter(value); }
			
			/**
			 * Gets the letter based on the string input.
			 * 
			 * @param value - Character to get.
			 * @return Letter - if found, otherwise null
			 */
			public static PLAYERS_LETTER getLetter(String value) {
				for(PLAYERS_LETTER ltr : values()) {
					if(ltr.toString().equals(value.toLowerCase()))
						return ltr;
				}
				
				return null;
			}
			
			/**
			 * Gets an array of letters based on the string input.
			 * @param value - Array of string characters
			 * @return Array of letters
			 */
			public static PLAYERS_LETTER[] getLetter(String[] value) {
				PLAYERS_LETTER[] ret = new PLAYERS_LETTER[value.length];
				
				int placement = 0;
				for(PLAYERS_LETTER ltr : values()) {
					for(String s : value) {
						if(ltr.toString().equals(s.toLowerCase())) {
							ret[placement] = ltr;
							placement++;
							break;
						}
					}
				}
				
				return ret.length == 0 || ret[ret.length - 1] == null ? null : ret;
			}
			
			/**
			 * Gets an array of letters based on the char array input
			 * 
			 * @param value - Char array
			 * @return Array of letters
			 */
			public static PLAYERS_LETTER[] getLetter(char[] value) { return getLetter(value); }
		}
	}
	
/************************************* CLASS SEPARATOR ****************************************/
	
	/**
	 * Private creator class used for the builder.
	 * 
	 * @author Sommod
	 * @version 1.0
	 *
	 */
	private class TabCompleterCreator implements TabCompleter {
		private Map<String, List<String>> commands;
		private String cmd;
		
		protected TabCompleterCreator(String cmd) {
			this.cmd = cmd;
			commands = new HashMap<String, List<String>>();
			
			commands.put("0_" + cmd, new ArrayList<>());
		}
		
		private List<String> playerToString(List<OfflinePlayer> players) {
			List<String> convert = new ArrayList<String>();
			
			for(OfflinePlayer p : players)
				convert.add(p.getName());
			
			return convert;
		}

		@Override
		public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
			List<String> calc = new ArrayList<String>();
			List<String> temp = commands.get(args.length == 0 ? 0 + "_" + cmd.toLowerCase() : args.length + "_" + args[args.length - 1]);
			
			if(temp != null) {
				for(String line : temp) {
					if(line.startsWith("playerinfo+")) {
						String[] details = line.split("+");
						Players.PLAYERS_LETTER[] ltrs = new Players.PLAYERS_LETTER[details.length - 2];
						
						for(int i = 0; i + 2 < details.length; i++)
							ltrs[i] = Players.PLAYERS_LETTER.getLetter(details[i + 2]);
						
						calc.addAll(playerToString(Players.getFlag(details[1]).getPlayers(ltrs)));
					} else
						calc.add(line);
				}
			}
			
			return calc.isEmpty() ? null : calc;
		}
		
	}
}
