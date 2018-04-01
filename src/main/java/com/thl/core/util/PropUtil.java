package com.thl.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.thl.core.log.Logger;
import com.thl.core.log.LoggerFactory;

public class PropUtil {

	private static Logger logger = LoggerFactory.getLogger(PropUtil.class);

	public static Properties loadProperties(String fileName) {
		Properties prop = null;
		InputStream in = null;
		try {
			prop = new Properties();
			in = PropUtil.class.getClassLoader()//
					.getResourceAsStream(fileName);
			prop.load(in);
		} catch (Exception e) {
			logger.error(fileName + " properties is not found", e, true);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				logger.error("close input stream failure", e, true);
			}
		}
		return prop;
	}

	// String
	public static String getString(Properties prop, String key) {
		return getString(prop, key, null);
	}

	public static String getString(Properties prop, String key, String defaultValue) {
		String value = "";
		if (prop.containsKey(key)) {
			value = prop.getProperty(key).trim();
			if (StringUtil.isEmpty(value)) {
				logger.log("the key \"" + key + "\" is not found in properties file");
				if (StringUtil.isNotEmpty(defaultValue)) {
					value = defaultValue.trim();
				}
			}
		} else {
			logger.log("the key \"" + key + "\" is not found in properties file");
			if (StringUtil.isNotEmpty(defaultValue)) {
				value = defaultValue.trim();
			}
		}
		return value;
	}

	// int
	public static int getInteger(Properties prop, String key) {
		return getInteger(prop, key, 0);
	}

	public static int getInteger(Properties prop, String key, int defaultValue) {
		String value = getString(prop, key, defaultValue + "");
		return Integer.parseInt(value);
	}

	// long
	public static long getLong(Properties prop, String key) {
		return getLong(prop, key, 0l);
	}

	public static long getLong(Properties prop, String key, long defaultValue) {
		String value = getString(prop, key, defaultValue + "");
		return Long.parseLong(value);
	}

	// boolean
	public static boolean getBoolean(Properties prop, String key) {
		return getBoolean(prop, key, false);
	}

	public static boolean getBoolean(Properties prop, String key, boolean defaultValue) {
		String value = getString(prop, key, defaultValue + "");
		return Boolean.parseBoolean(value);
	}

}
