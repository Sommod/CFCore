package com.coldfyre.api.utilities;

import java.util.concurrent.TimeUnit;

public class Util {
	
	/**
	 * Intakes a String representation of time and converts it into seconds. The format for the input
	 * String is as required: '00W 00D 00H 00M 00S'. Each letter representations a given timer interval
	 * with the numerical values on the LEFT. A Space ' ' is the given delimiter for the different times.
	 * Note that this can accept duplicate time intervals (ie 00W 15W 17S 2H 99H....) The numerical value
	 * is not limited to the standard time restriction of clocks; Additionally, the order of the times
	 * does not matter, as such you can have each time interval in any order.
	 * 
	 * @param value - String representation of time
	 * @return Long - long value representing the String time
	 */
	public static long toTime(String value) {
		long time = 0x0L;
		
		for(String s : value.split(" ")) {
			long timeValue = Long.parseLong(s.substring(0, s.length() - 1));
			
			switch (s.toLowerCase().charAt(s.length() - 1)) {
			case 'w':
				time += TimeUnit.SECONDS.convert(timeValue * 7, TimeUnit.DAYS);
				break;
			case 'd':
				time += TimeUnit.SECONDS.convert(timeValue, TimeUnit.DAYS);
				break;
			case 'h':
				time += TimeUnit.SECONDS.convert(timeValue, TimeUnit.HOURS);
				break;
			case 'm':
				time += TimeUnit.SECONDS.convert(timeValue, TimeUnit.MINUTES);
				break;
			case 's':
				time += timeValue;
				break;

			default:
				break;
			}
		}
		
		return time;
	}
	
	/**
	 * Intakes a single String object and adds newline values into specific locations based on the number
	 * of words per line. This is to help condense long descriptive text into smaller <i>paragraph like</i>
	 * format. The length is not determined by the number of characters, but rather the number of words. This
	 * will not split the string, but rather add to it; should you desire to split based on the word wrapping,
	 * use the {@link #wordWarpAsArray(String, int)} method.
	 * 
	 * To word wrap based on character number, see {@link #wordWarp(String, int, boolean)}.
	 * 
	 * @see #wordWarpAsArray(String, int)
	 * @param input - String text input
	 * @param words - number of words per line
	 * @return String - String input with escape sequences (\n).
	 */
	public static String wordWarp(String input, int words) {
		String[] split = input.trim().split(" ");
		String result = split[0];
		
		for(int i = 1; i < split.length; i++) {
			if((i + 1) % words == 0)
				result = result.concat("\n");
			
			if(!result.endsWith("\n"))
				result = result.concat(" ".concat(split[i]));
			else
				result = result.concat(split[i]);
		}
		
		return result;
	}
	
	/**
	 * Intakes a String object and converts it into a String array. The String object of each index will
	 * contain at most the numerical value of the parameter <b>words</b>. In similarity to <i>wordWarp</i>,
	 * this simple returns the altered value into an array.
	 * 
	 * @param input - String text input
	 * @param words - number of words per line
	 * @return String[] - String array of word warp
	 */
	public static String[] wordWarpAsArray(String input, int words) {
		return wordWarp(input, words).split("\n");
	}
	
	/**
	 * Given an array, a String is returned with the delimiter used as the spacing between each index. If no
	 * delimiter is to be added in between each of the String objects, then set the <i>limiter</i> parameter
	 * to NULL.
	 * 
	 * @param input - String Array
	 * @param limiter - Delimiter to add (Can be null)
	 * @return String - Object of combined array into single String Object
	 */
	public static String combineArray(String[] input, String limiter) {
		String ret = "";
		
		for(String s : input)
			ret += (s + limiter == null ? "" : limiter);
		
		return ret;
	}
	
	/**
	 * Simple method for checking if the String object is the same as any of the given comparisons. If one
	 * of the given comparisons is the same as the input, then this will return true. There are two options,
	 * checking with and without casing.
	 * 
	 * @param ignoreCase - Require if Case-Sensitive is necessary for comparison
	 * @param input - String input
	 * @param compareTo - Comparisons for input
	 * @return True - if input matches at least one comparison, otherwise false
	 */
	public static boolean isAny(boolean ignoreCase, String input, String... compareTo) {
		if(compareTo.length == 0)
			return false;
		
		for(String s : compareTo) {
			if(ignoreCase ? s.equalsIgnoreCase(input) : s == input)
				return true;
		}
		
		return false;
	}

}
