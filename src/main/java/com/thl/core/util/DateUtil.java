package com.thl.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";
	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	private static final String DEFAULT_TIME_FORMAT = "hh:mm:ss";

	
	public static boolean hasTime(String source) {
		if (source.contains(":") && source.length() >= DEFAULT_TIME_FORMAT.length()) {
			return true;
		}
		return false;
	}
	
	public static boolean hasDate(String source) {
		if (source.contains("-") && source.length() >= DEFAULT_DATE_FORMAT.length()) {
			return true;
		}
		return false;
	}
	
	public static boolean isDate(Class<?> clazz) {
		String className = clazz.getName();
		if (className.equals(Date.class.getName())) {
			return true;
		}
		return false;
	}
	
	public static String formatDate(Date date) {
		return format(date, DEFAULT_DATE_FORMAT);
	}

	public static String formatTime(Date date) {
		return format(date, DEFAULT_TIME_FORMAT);
	}

	public static String formatDateTime(Date date) {
		return format(date, DEFAULT_DATE_TIME_FORMAT);
	}

	public static String format(Date date, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}

	// ------------------------------

	public static Date parseDate(String source) {
		return parse(source, DEFAULT_DATE_FORMAT);
	}

	public static Date parseTime(String source) {
		return parse(source, DEFAULT_TIME_FORMAT);
	}

	public static Date parseDateTime(String source) {
		return parse(source, DEFAULT_DATE_TIME_FORMAT);
	}

	public static Date parse(String source, String pattern) {
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		try {
			date = format.parse(source);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		return date;
	}

}
