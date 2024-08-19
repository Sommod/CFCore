package com.coldfyre.api.utilities;

/**
 * Provides an exception when dealing with IO of files that are not supported by the plugin. Certain
 * files are required in a certain format for the plugin to work, this exception is used when an
 * improper file format (extension) is used.
 * 
 * @author Sommod
 * @version 1.0
 *
 */
public class InvalidFileFormatException extends Exception {
	
	@java.io.Serial
	private static final long serialVersionUID = 5672203177888981778L;
	private String message;
	
	public InvalidFileFormatException() { this("The given file in the system is not supported by this plugin."); }
	
	public InvalidFileFormatException(String msg) {
		message = msg;
	}
	
	@Override
	public String getMessage() {
		return message;
	}

}
