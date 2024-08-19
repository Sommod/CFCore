package com.coldfyre.api.manager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class contains simple methods to enable file handling easier. This does not handle
 * any of the YAMLConfiguration objects that Bukkit uses for it's configuration, but rather
 * the basic File itself.
 * 
 * @author Sommod
 * @version 1.0
 *
 */
public class FilesManager {
	
	// Statically get the Default Exception Logger Folder
	static {
		DEFAULT_LOG_FOLDER = new File(Bukkit.getWorldContainer().getAbsoluteFile() + "/plugins/Exception Logger");
	}
	
	// Objects
	private JavaPlugin plugin;
	private PluginManager<?> pluginManager;
	
	private Map<String, File> folders;
	private Map<String, File> files;
	
	private static final File DEFAULT_LOG_FOLDER;
	private static File LOG_FOLDER;
	
	/**
	 * Creates a new FilesManager object that uses the {@link JavaPlugin} class as the default path for
	 * all files and folders. The default path is located at the Plugins Data Folder ({@link JavaPlugin#getDataFolder()}).
	 * By default, this will create both the Log Exception folder and the Plugin folder should it not already exist.
	 * 
	 * @see {@link #FilesManager(PluginManager)}
	 * @param plugin - JavaPlugin
	 */
	public FilesManager(JavaPlugin plugin) {
		this.plugin = plugin;
		initializeObjects();
	}
	
	/**
	 * Creates a new FilesManager object that uses the {@link PluginManager} class, which houses the  JavaPlugin
	 * object. The default path is located at the Plugins Data Folder ({@link JavaPlugin#getDataFolder()}).
	 * By default, this will create both the Log Exception folder and the Plugin folder should it not already exist.
	 * 
	 * @see {@link #FilesManager(JavaPlugin)}
	 * @param plugin - JavaPlugin
	 */
	public FilesManager(PluginManager<?> pluginManager) {
		this.pluginManager = pluginManager;
		initializeObjects();
	}
	
	// Initializes the Maps and ensure the default directories are created
	private void initializeObjects() {
		folders = new HashMap<String, File>();
		files = new HashMap<String, File>();
		LOG_FOLDER = new File(plugin != null ? plugin.getDataFolder() : pluginManager.getPlugin().getDataFolder(), "/Exception Logger");
		
		if(!DEFAULT_LOG_FOLDER.exists())
			DEFAULT_LOG_FOLDER.mkdir();
		
		if(!LOG_FOLDER.exists())
			LOG_FOLDER.mkdirs();
	}
	
	/**
	 * Adds the given file to the the Folder or Files list based on what the user
	 * wants to add the file as.
	 * 
	 * @param choice - TRUE: Folder | FALSE: File
	 * @param name - Name to obtain/store file into this class
	 * @param path - Path to file
	 * @return TRUE - if file was added to this class
	 */
	private boolean addToList(boolean choice, String name, String path) {
		File toAdd = new File(getPluginFolder() + (path.startsWith("/") ? path : "/" + path));
		
		if(choice) {
			if(folders.containsKey(name))
				return false;
			
			folders.put(name, toAdd);
			toAdd.mkdirs();
			return true;
		} else {
			if(files.containsKey(name))
				return false;
			
			files.put(name, toAdd);
			
			try {
				toAdd.createNewFile();
			} catch (IOException e) {
				if(plugin == null)
					LogException(pluginManager, e);
				else
					LogException(plugin, e);
				
				files.remove(name);
				return false;
			}
			
			return true;
		}
	}
	
	/**
	 * Removes a file or folder from both this class and the file system.
	 * 
	 * @param choice - TRUE: Folder | FALSE: File
	 * @param name - Name of item to remove
	 * @return TRUE - If deleted, otherwise false
	 */
	private boolean deleteItem(boolean choice, String name) {
		if(choice) {
			if(folders.containsKey(name)) {
				return folders.remove(name).delete();
			} else
				return false;
		} else {
			if(files.containsKey(name)) {
				return files.remove(name).delete();
			} else
				return false;
		}
	}
	
	/**
	 * Gets the default File (Folder) that is the JavaPlugin's data folder.
	 * 
	 * @return File - Plugin Data Folder
	 */
	public File getPluginFolder() {
		if(plugin == null)
			return pluginManager.javaPlugin.getDataFolder();
		else
			return plugin.getDataFolder();
	}
	
	/**
	 * Gets the entire list of files that are stored within this FilesManager class. This
	 * will only return the files portion of this class; any folders that may exist within
	 * this class will not be return.
	 * This is simply the File object and the file itself may not exist.
	 * 
	 * @return File Array
	 */
	public File[] getAllFiles() { return (File[]) files.values().toArray(); }
	
	/**
	 * Gets the entire list of folders that are stored wtihin this FilesManager class. This
	 * will return only the folders within this class.
	 * This is simply the File object and the file (folder) itself may not exist.
	 * 
	 * @return File Array (Folders Only)
	 */
	public File[] getAllFolders() { return (File[]) folders.values().toArray(); }
	
	/**
	 * Gets all the Names attached to the List of Files. Each file has a unique name (Key) that
	 * is returned in a set.
	 * 
	 * @return Set of Key names for files
	 */
	public Set<String> getFileKeys() { return files.keySet(); }
	
	/**
	 * Gets all the Names atteched to the List of Folders. Each folder has a unique name (Key) that
	 * is returned in a set.
	 * 
	 * @return Set of Key names for folders
	 */
	public Set<String> getFolderKeys() { return folders.keySet(); }
	
	/**
	 * Adds a Folder to this class and makes the directory. Note that the path MUST
	 * include the folder name that'll be used in the file system.
	 * 
	 * <br><br>eg.<strong> {@code FilesManager.addFolder("player_data_folder", "path/to/folder")}</strong><br><br>
	 * In this scenario, the <i>path/to/folder</i> will result in the folder called 'folder' while the name
	 * to get this folder from this class would be 'player_data_folder'. (Key::Value pairing)
	 * 
	 * @param name - Name to register to folder
	 * @param path - Path of folder
	 * @return TRUE - If folder was added/made, otherwise false
	 */
	public boolean addFolder(String name, String path) { return addToList(true, name, path); }
	
	/**
	 * Adds a file to this class and makes the file. Note that the path MUST include
	 * the filename that'll be used in the file system.
	 * 
	 * <br><br>eg.<strong> {@code FilesManager.addFile("this_is_a_name", "path/to/file.yml")}</strong><br><br>
	 * In this scenario, the <i>path/to/file.yml</i> will result in a file called 'file.yml' while the name
	 * to get this file from this class would be 'this_is_a_name'. (Key::value pairing)
	 * 
	 * @param name - Name to register to file
	 * @param path - Path to file
	 * @return TRUE - If file was added / created
	 */
	public boolean addFile(String name, String path) { return addToList(false, name, path); }
	
	/**
	 * Checks if the given Key (name) has a corresponding folder attached to it (folder). Note
	 * that this does not check if the folder exists within the file system, only that it's registered
	 * within this class.
	 * 
	 * @param name - Name of key to check folder
	 * @return TRUE - If folder exists within this class
	 */
	public boolean isFolder(String name) { return folders.containsKey(name); }
	
	/**
	 * Checks if the given Key (name) has a corresponding file attached to it (file). Note that
	 * this does not check if the file exists within the file system, only that it's registered
	 * within this class.
	 * 
	 * @param name - Name of key to check file
	 * @return TRUE - If file exists within this class
	 */
	public boolean isFile(String name) { return files.containsKey(name); }
	
	/**
	 * Deletes the folder from both this class and from the file system. This is permanent and
	 * cannot be undone. This is the same as removing the folder manually from the filing system.
	 * Note that if you delete a folder manually, this class will NOT recognize that and will
	 * attempt to make the directory the next time the directory is used within this class. To
	 * remove a file ONLY from this class use {@link #removeFolder(String)}.
	 * 
	 * @see {@link #removeFolder(String)}
	 * 
	 * @param name - Name of key to folder to remove
	 * @return TRUE - If folder was removed from both the file system and this class
	 */
	public boolean deleteFolder(String name) { return deleteItem(true, name); }
	
	/**
	 * Deletes the file from both this class and from the file system. This is permanent and
	 * cannot be undone. This is the same as removing the file manually from the filing system.
	 * Note that if you delete a file manually, this class will NOT reognize that and will
	 * attempt to make the file the next the file is used within this class. To remove a file ONLY
	 * from this class use {@link #removeFile(String)}.
	 * 
	 * @see {@link #removeFile(String)}
	 * @param name - Name of key to file to remove
	 * @return TRUE - if file was removed from both the file system and this class
	 */
	public boolean deleteFile(String name) { return deleteItem(false, name); }
	
	/**
	 * This method will ensure that all folder stored within this object will be made. Accessing
	 * files that don't exist will cause IO Errors. As such, it is suggested that this method
	 * always be ran during the initialization of your plugin, ensuring that all paths are created
	 * for any IO you may do. This will only create the folders, no files will be created. To have
	 * all files created, use the {@link #createAllFiles()} method. 
	 * 
	 * @return True - If all folders were created successfully
	 */
	public boolean mkdirs() {
		boolean check = true;
		
		for(File f : folders.values()) {
			if(!f.mkdirs())
				check = false;
		}
		
		return check;
	}
	
	/**
	 * This will create all files (and folders they are in) that are stored within this manager. However,
	 * note that while this will create the files, this does not load any data into the file. You can load
	 * any data into the file using the method {@link #writeToFile(String, File)} or {@link #writeToFile(String, String)}.
	 * 
	 * @return True - If all files were created successfully
	 */
	public boolean createAllFiles() {
		boolean check = true;
		
		try {
			for(File f : files.values()) {
				if(!f.createNewFile())
					check = false;
			}
		} catch (IOException e) {
			if(plugin == null)
				LogException(pluginManager, e);
			else
				LogException(plugin, e);
		}
		
		return check;
	}
	
	/**
	 * Writes the given data from the String into the file. Note that if the file already exists,
	 * then this will overwrite the current data within the file, replacing it with the given data
	 * provided. This is similar to the {@link #writeToFile(String, File)} method, but given as a string
	 * object of data.
	 * 
	 * @see {@link #writeToFile(String, File)}
	 * @param name - Name of file (key) to write the data to
	 * @param data - Data to write to the file
	 * @return TRUE - If the file was successfully written to, otherwise false
	 */
	public boolean writeToFile(String name, String data) {
		if(!files.containsKey(name))
			return false;
		
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(files.get(name)))) {
			if(!files.get(name).exists())
				files.get(name).createNewFile();
			
			writer.write(data);
			writer.flush();
			return true;
		} catch (IOException e) {
			if(plugin == null)
				LogException(pluginManager, e);
			else
				LogException(plugin, e);
			
			return false;
		}
	}
	
	/**
	 * Writes the given data <strong>FROM</strong> the fileData <strong>TO</strong> the file name within this class.
	 * Note that this will overwrite any data if the file already exists and contains data within it. This is similar
	 * to the {@link #writeToFile(String, String)} method, but uses a file to import the data and output to another file.
	 * (Basically acts like a copy data from-file-to-file).
	 * @param name - Name of file (key) to write data do
	 * @param fileData - Data to write to the file
	 * @return True - If the data was written to the file
	 */
	public boolean writeToFile(String name, File fileData) {
		if(!files.containsKey(name))
			return false;
		
		try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(files.get(name)))) {
			if(!files.get(name).exists())
				files.get(name).createNewFile();
			
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileData));
			byte[] bytes = new byte[1024];
			
			@SuppressWarnings("unused")
			int i;
			
			while((i = bis.read(bytes)) >= 0)
				bos.write(bytes);
			
			bis.close();
			
			return true;
		} catch (IOException e) {
			if(plugin == null)
				LogException(pluginManager, e);
			else
				LogException(plugin, e);
			return false;
		}
	}
	
	/**
	 * Writes the given data <strong>FROM</strong> the InputStream <strong>TO</strong> the file name within this class.
	 * Note that this will overwrite any data that exists already within the file. This method is a third option for files
	 * that are obtained using other various methods that don't exist outside of an additional file. This will automatically
	 * close the InputStream after use, regardless of whether the method succeeds or fails. This is to ensure that the memory
	 * usage for the Stream is released for other data.
	 * 
	 * @param name - Name of file stored within this class
	 * @param is - InputStream of data to write
	 * @return True - All data was written to the file
	 */
	public boolean writeToFile(String name, InputStream is) {
		if(!files.containsKey(name))
			return false;
		
		try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(files.get(name)))) {
			if(!files.get(name).exists())
				files.get(name).createNewFile();
			
			byte[] bytes = new byte[1024];
			
			@SuppressWarnings("unused")
			int i;
			
			while((i = is.read(bytes)) >= 0)
				bos.write(bytes);
			
			is.close();
			return true;
		} catch (IOException e) {
			if(plugin == null)
				LogException(pluginManager, e);
			else
				LogException(plugin, e);
			return false;
		} finally {
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {
					if(plugin == null)
						LogException(pluginManager, e);
					else
						LogException(plugin, e);
					return false;
				}
			}
		}
	}
	
	/**
	 * Writes the given data <strong>FROM</strong> the ByteArray given <strong>TO</strong> the file name within this class.
	 * Note that this will overwrite any data that exists already within the file. An alternative when using InputStreams as
	 * the method for writing data.
	 * 
	 * @param name - File name (key)
	 * @param byteArray - Array of bytes to write to file
	 * @return True - If data was written
	 */
	public boolean writeToFile(String name, byte[] byteArray) {
		if(!files.containsKey(name))
			return false;
		
		try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(files.get(name)))) {
			if(!files.get(name).exists())
				files.get(name).createNewFile();
			
			bos.write(byteArray);
			
			return true;
		} catch (IOException e) {
			if(plugin == null)
				LogException(pluginManager, e);
			else
				LogException(plugin, e);
			return false;
		}
	}
	
	/**
	 * Removes the given folder from this class. This will only affect the object within this class, but leave
	 * the folder within the file system intact. To remove both from this class and from the file system, use
	 * the function {@link #deleteFolder(String)}.
	 * 
	 * @see {@link #deleteFolder(String)}
	 * @param name - Name of key to object to remove
	 * @return TRUE - if folder was removed successfully
	 */
	public File removeFolder(String name) { return folders.remove(name); }
	
	/**
	 * Removes the given file form this class. This will only affect the object within this class, but leave
	 * the file within the file system intact. To remvoe both from this class and from the file system, use
	 * the function {@link #deleteFile(String)}.
	 * 
	 * @see {@link #deleteFile(String)}
	 * @param name
	 * @return
	 */
	public File removeFile(String name) { return files.remove(name); }
	
	/**
	 * Gets the <i>File</i> object of the folder attached to the name provided. If the folder does not
	 * exist, then this will return NULL.
	 * 
	 * @param name - Name of key of object to get
	 * @return FILE - If exists, otherwise null
	 */
	public File getFolder(String name) { return folders.get(name); }
	
	/**
	 * Gets the <i>File</i> object of the file attached to the name provided. If the file does not exist,
	 * then this will return NULL.
	 * 
	 * @param name - Name of key of object to get
	 * @return FILE - if exists, otherwise null
	 */
	public File getFile(String name) { return files.get(name); }
	
	/**
	 * Will log the output of the Exception to a .LOG file within the DEFAULT Log Exception folder. This
	 * folder is found within the root 'plugins' folder (where all the plugin data folders exist). The name
	 * of the file will always be the Date/Time of the creation of the file. Note that if an error occurs
	 * when trying to write to the DEFAULT directory, then the error will simply be output to the console.
	 * 
	 * @param e - Exception to log
	 */
	public static void LogException(Exception e) {
		File file = new File(DEFAULT_LOG_FOLDER, getGenericFileName() + ".log");
		
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			writer.write(e.getLocalizedMessage());
			writer.flush();
		} catch (IOException e2) {
			ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
			
			console.sendMessage("§4================§r\n§c[CFCore] Error: Could not write exception to expected file, IOException for handler has been thrown. First Error:§r\n", e.getLocalizedMessage(),
					"\n§r§cSecond Error:§r\n", e2.getLocalizedMessage(),
					"\n§4================§r");
		}
	}
	
	/**
	 * Logs the exception to the given file. If the file does not exist, then this will attempt to create the
	 * file before writing to it. If the creating the file fails, then this will log both the initial Exception
	 * to the DEFAULT folder as well as THIS functions error to the DEFAULT folder.
	 * 
	 * @param file - File to save exception to
	 * @param e - Exception to save
	 */
	public static void LogException(File file, Exception e) {
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e2) {
				LogException(e);
				LogException(e2);
			}
		} else {
			try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
				writer.write(e.getLocalizedMessage());
				writer.flush();
			} catch (IOException e2) {
				LogException(e);
				LogException(e2);
			}
		}
	}
	
	/**
	 * Logs the exception to the default <i>Plugin Data Exception Logger Folder</i>. This will use the plugin provided
	 * as a default path and store the .LOG file within a folder called 'Exception Logger' within said plugin's data folder.
	 * The name of the file will be the date/time the file was created. If the writing of the exception causes an error, then
	 * this will write both the initial and THIS function's error to the DEFAULT Exception Logger folder.
	 * 
	 * @param plugin - Plugin to use a default path
	 * @param e - Exception to save
	 */
	public static void LogException(JavaPlugin plugin, Exception e) {
		if(plugin == null)
			LogException(e);
		else {
			File file = new File(plugin.getDataFolder(), "/Exception Logger/" + getGenericFileName() + ".log");
			
			try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
				file.createNewFile();
				
				writer.write(e.getLocalizedMessage());
				writer.flush();
			} catch (IOException e2) {
				LogException(e);
				LogException(e2);
			}
		}
	}
	
	/**
	 * Logs the exception to the default <i>Plugin Data Exception Logger Folder</i> using the PluginManager's stored JavaPlugin
	 * object. This will save to the folder named 'Exception Logger' with the default name of the date/time. If writing to the
	 * file causes an error, then the initial error and THIS function's error will be saved to the DEFAULT Exception Logger folder.
	 * @param pluginManager
	 * @param e
	 */
	public static void LogException(PluginManager<?> pluginManager, Exception e) { LogException(pluginManager == null ? null : pluginManager.getPlugin(), e); }
	
	// Gets the current Date/Time of Local time of the machine to store/save the Exception to. The file name will be as the pattern
	// before with the .log extension used.
	private static String getGenericFileName() { return DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss").format(LocalDateTime.now()); }
}
