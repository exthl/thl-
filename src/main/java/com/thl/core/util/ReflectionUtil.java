package com.thl.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.thl.core.log.Logger;
import com.thl.core.log.LoggerFactory;

public class ReflectionUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtil.class);
	
	public static Object newInstance(Class<?> clazz) {
		Object instance = null;
		try {
			instance = clazz.newInstance();
		} catch (Exception e) {
			LOGGER.error("create object instance failure", e, true);
		}
		return instance;
	}
	
	public static Object invokeMethod(Object obj, Method method, Object[] args) {
		Object result = null;
		try {
			method.setAccessible(true);
			result = method.invoke(obj, args);
		} catch (Exception e) {
			LOGGER.error("invoke method failure", e, true);
		}
		return result;
	}
	
	public static Object invokeMethod(Object obj, Method method, Object arg) {
		return invokeMethod(obj, method, new Object[]{arg});
	}
	
	public static Object invokeMethod(Object obj, Method method) {
		return invokeMethod(obj, method, null);
	}
	
	public static void setField(Object obj, Field field, Object value) {
		try {
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception e) {
			LOGGER.error("set field failure", e, true);
		}
	}
	
}
