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
	
	public static String wordWarp(String input, int words) {
		String[] split = input.trim().split(" ");
		String result = split[0];
		
		for(int i = 1; i < split.length; i++) {
			if((i + 1) % 13 == 0)
				result = result.concat("\n");
			
			if(!result.endsWith("\n"))
				result = result.concat(" ".concat(split[i]));
			else
				result = result.concat(split[i]);
		}
		
		return result;
	}
	
	public static String[] wordWarpAsArray(String input, int words) {
		return wordWarp(input, words).split("\n");
	}
	
	public static String combineArray(String[] input, String limiter) {
		String ret = "";
		
		for(String s : input)
			ret += (s + limiter);
		
		return ret;
	}
	
	public static boolean isAny(boolean ignoreCase, String input, String... compareTo) {
		if(compareTo.length == 0)
			return false;
		
		for(String s : compareTo) {
			if(ignoreCase ? s.equalsIgnoreCase(input) : s.equals(input))
				return true;
		}
		
		return false;
	}

}
