package com.thl.dao.core;

import java.io.IOException;
import java.util.Properties;

import com.thl.core.util.PropUtil;
import com.thl.core.util.StringUtil;

public final class ConfigHelper {

	private static String domainPath;

	private static Properties mainProp;
	private static boolean showSql;

	static {
		// try {
		mainProp = PropUtil.loadProperties(ConfigConstant.CONFIG_FILE);
		domainPath = PropUtil.getString(mainProp, ConfigConstant.DOMAIN_PATH, "domain");
		showSql = PropUtil.getBoolean(mainProp, ConfigConstant.SHOW_SQL);
		/*
		 * } catch (Exception e) { Logger logger =
		 * LoggerFactory.getLogger(ConfigHelper.class);
		 * logger.error("read thl-dao framemarker config file failure", e,
		 * true); }
		 */
	}

	public static String getDomainPath() {
		return domainPath;
	}

	public static Properties getMainProperties() {
		return mainProp;
	}

	public static boolean isShowSql() {
		return showSql;
	}

	public static Properties getPropertiesByClass(Class<?> clazz) {
		Properties prop = null;
		try {
			String qualifiedName = clazz.getName();
			String className = qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
			String path = domainPath + "//" + className + ConfigConstant.DOMAIN_CONFIG_FILE_SUFFIX;
			prop = PropUtil.loadProperties(path);
		} catch (Exception e) {
			return null;
		}
		return prop;
	}

	public static String getValue(Class<?> clazz, String key) throws IOException {
		Properties prop = getPropertiesByClass(clazz);
		return PropUtil.getString(prop, key);
	}

	public static String getTableName(Class<?> clazz) throws IOException {
		String tableName = getPropertiesByClass(clazz) //
				.getProperty(ConfigConstant.DOMAIN_CONFIG_TABLE);
		// 没有配置这个选项时，默认为类名，并转换为小写
		if (tableName == null || tableName.trim().equals("")) {
			String qualifiedName = clazz.getName();
			String className = qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
			tableName = className.toLowerCase();
		}
		return tableName;
	}

	public static String[] getPrimaryKey(Class<?> clazz) throws IOException {
		String primaryKey = getPropertiesByClass(clazz) //
				.getProperty(ConfigConstant.DOMAIN_PRIMARY_KEY);
		if (StringUtil.isEmpty(primaryKey)) {
			throw new RuntimeException("没有设置主键");
		}
		String[] primaryKeys = primaryKey.split(",");
		return primaryKeys;
	}
	public static String[] getForeignKey(Class<?> clazz) throws IOException {
		String foreignKey = getPropertiesByClass(clazz) //
				.getProperty(ConfigConstant.DOMAIN_FOREIGN_KEY);
		if (StringUtil.isEmpty(foreignKey)) {
			return null;
		}
		String[] foreignKeys = foreignKey.split(",");
		return foreignKeys;
	}
	
}
