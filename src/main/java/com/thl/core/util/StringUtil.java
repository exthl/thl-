package com.thl.core.util;

import java.util.UUID;

public class StringUtil {

	public static final String SPEARATOR = String.valueOf((char) 29);

	public static boolean isEmpty(String value) {
		if (value != null && !value.trim().equals("")) {
			return false;
		}
		return true;
	}

	public static boolean isNotEmpty(String value) {
		return !isEmpty(value);
	}

	public static String firstToUpper(String value) {
		return Character.toUpperCase(value.charAt(0)) + value.substring(1);
	}

	public static String firstToLower(String value) {
		return Character.toLowerCase(value.charAt(0)) + value.substring(1);
	}

	public static String cutClassAndSpeace(String string) {
		if (string.contains("class ")) {
			string = string.substring(string.indexOf("class ") + 6);
		}
		return string;
	}
	
	public static String getUUID() {
		return UUID.randomUUID().toString();
	}
}
