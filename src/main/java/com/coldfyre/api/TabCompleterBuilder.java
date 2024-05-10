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
	
	public TabCompleterBuilder(String cmd) { creator = new TabCompleterCreator(cmd); }
	
	public TabCompleter build() { return creator; }
	public TabCompleter buildAndApply(JavaPlugin plugin) {
		plugin.getCommand(creator.cmd).setTabCompleter(creator);
		
		return creator;
	}
	
	private boolean ensure(int argPos, String command) {
		if(!creator.commands.containsKey(argPos + "_" + command)) {
			addCommand(argPos, command);
			return false;
		}
		
		return true;
	}
	
	public boolean addCommand(int argPos, String command) {
		if(creator.commands.containsKey(argPos + "_" + command))
			return false;
		
		creator.commands.put(argPos + "_" + command, new ArrayList<String>());
		
		return true;
	}
	
	public void addTabOption(int argPos, String command, String option) {
		ensure(argPos, command);
		
		List<String> options = creator.commands.get(argPos + "_" + command);
		
		options.add(option);
		creator.commands.put(argPos + "_" + command, options);
	}
	
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
	
	public void addTabOption(int argPos, String command, Players playerFlag, Players.PLAYERS_LETTER... letter) {
		if(letter.length == 0) {
			addTabOption(argPos, command, playerFlag);
			return;
		}
		
		List<String> tabList = creator.commands.get(argPos + "_" + command);
		
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
				
				return;
			}
		}
		
		String option = "playerinfo+" + playerFlag.name();
		
		for(Players.PLAYERS_LETTER ltrs : letter)
			option += ("+" + ltrs.name());
		
		tabList.add(option);
		creator.commands.put(argPos + "_" + command, tabList);
	}
	
	public void removeTabOption(int argPos, String command, int option) {
		if(!ensure(argPos, argPos == 0 ? creator.cmd.toLowerCase() : command))
			return;
		
		List<String> tabList = creator.commands.get(argPos + "_" + command);
		if(tabList.isEmpty())
			return;
		
		tabList.remove(option);
		creator.commands.put(argPos + "_" + command, tabList);
	}
	
	public void removeTabOption(int argPos, String command, String option) {
		if(!ensure(argPos, argPos == 0 ? creator.cmd.toLowerCase() : command))
			return;
		
		List<String> tabList = creator.commands.get(argPos + "_" + command);
		if(tabList.isEmpty())
			return;
		
		tabList.remove(option);
		creator.commands.put(argPos + "_" + command, tabList);
	}
	
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
			}
			
			loc++;
		}
		
		if(loc != tabList.size()) {
			tabList.remove(loc);
			tabList.add(loc, edited);
		}
		
		creator.commands.put(argPos + "_" + command, tabList);
	}
	
	public boolean hasTabOptions(int argPos, String command) {
		if(!ensure(argPos, argPos == 0 ? creator.cmd.toLowerCase() : command))
			return false;
		
		return !creator.commands.get(argPos + "_" + command).isEmpty();
	}
	
	public void setTabOptions(int argPos, String command, List<String> options) {
		ensure(argPos, argPos == 0 ? creator.cmd.toLowerCase() : command);
		
		creator.commands.put(argPos + "_" + command, options);
	}
	
	@Deprecated
	public void setTabOptions(Map<String, List<String>> tabCompleter) { creator.commands = tabCompleter; }
	
	public void clear() { creator.commands.clear(); }
	public void clear(int argPos, String command) { creator.commands.put(argPos + "_" + command, new ArrayList<String>()); }

/*********************************** CLASS SEPARATOR *************************************/
	public enum Players {
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
		
		public abstract List<OfflinePlayer> getPlayers(PLAYERS_LETTER... ltr);
		
		public static Players getFlag(String value) {
			for(Players tf : values()) {
				if(tf.name().equals(value.toUpperCase()))
					return tf;
			}
			
			return null;
		}
		
		private static List<OfflinePlayer> getAllPlayers() { return Arrays.asList(Bukkit.getServer().getOfflinePlayers()); }
		private static Collection<? extends Player> getAllOnlinePlayers() { return Bukkit.getServer().getOnlinePlayers(); }
		private static List<OfflinePlayer> getAllOfflinePlayers() {
			List<OfflinePlayer> players =  Arrays.asList(Bukkit.getServer().getOfflinePlayers());
			players.removeAll(Bukkit.getServer().getOnlinePlayers());
			
			return players;
		}
		
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
		
		public enum PLAYERS_LETTER {
			A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z;
			
			@Override
			public String toString() { return name().toLowerCase(); }
			
			public static PLAYERS_LETTER getLetter(String value) {
				for(PLAYERS_LETTER ltr : values()) {
					if(ltr.toString().equals(value.toLowerCase()))
						return ltr;
				}
				
				return null;
			}
			
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
				
				return ret[ret.length - 1] == null ? null : ret;
			}
		}
	}
	
/************************************* CLASS SEPARATOR ****************************************/
	private class TabCompleterCreator implements TabCompleter {
		private Map<String, List<String>> commands;
		private String cmd;
		
		protected TabCompleterCreator(String cmd) {
			this.cmd = cmd;
			commands = new HashMap<String, List<String>>();
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
			
			return calc.isEmpty() ? null : calc;
		}
		
	}
}
