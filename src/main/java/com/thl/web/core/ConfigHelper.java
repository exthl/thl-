package com.thl.web.core;

import java.util.Properties;

import com.thl.core.util.PropUtil;

public class ConfigHelper {
	
	private static Properties mainProp;
	private static String basePackage;
	private static String jspPath;
	private static String staticPath;
	private static String encode;
	private static int uploadLimit;
	private static String exception_page;
	
	
	static {
		mainProp = PropUtil.loadProperties(ConfigConstant.CONFIG_FILE);
		
		basePackage = PropUtil.getString(mainProp, ConfigConstant.BASE_PACKAGE);
		jspPath = PropUtil.getString(mainProp, ConfigConstant.JSP_PATH, "/WEB-INF/view");
		staticPath = PropUtil.getString(mainProp, ConfigConstant.STATIC_PATH, "/WEB-INF/static");
		encode = PropUtil.getString(mainProp, ConfigConstant.ENCOND, "utf-8");
		uploadLimit = PropUtil.getInteger(mainProp, ConfigConstant.UPLOAD_LIMIT, 10);
		exception_page = PropUtil.getString(mainProp, ConfigConstant.EXCEPTION_PAGE, "/message");
	}

	public static Properties getMainProp() {
		return mainProp;
	}

	public static String getBasePackage() {
		return basePackage;
	}

	public static String getJspPath() {
		if (!jspPath.startsWith("/")) {
			jspPath = "/" + jspPath;
		}
		if (jspPath.endsWith("/")) {
			jspPath = jspPath.substring(0, jspPath.length());
		}
		return jspPath;
	}

	public static String getStaticPath() {
		return staticPath;
	}

	public static String getEncode() {
		return encode;
	}
	
	public static int getUploadLimit() {
		return uploadLimit;
	}
	
	
	public static String getExceptionPage() {
		return exception_page;
	}
}
