package com.coldfyre.api.utilities;

public class Util {
	
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
