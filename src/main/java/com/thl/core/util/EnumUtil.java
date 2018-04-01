package com.thl.core.util;

import java.lang.reflect.Field;

import com.thl.core.log.Logger;
import com.thl.core.log.LoggerFactory;

public class EnumUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(EnumUtil.class);
	
	
	public static boolean isEnum(Class<?> clazz) {
		String typeName = clazz.getSuperclass().getTypeName();
		if (typeName.contains("<")) {
			typeName = typeName.substring(0, typeName.indexOf("<"));
		}
		// 是否是枚举类
		if (typeName.equals("java.lang.Enum")) {
			return true;
		}
		return false;
	}
	
	
	// 根据字段对象获取对应的枚举类
	@SuppressWarnings("unchecked")
	public static <T> T toEnum(Object fieldInstance, Class<T> enumClass) {
		T enumObj = null;
		try {
			Field[] fields = enumClass.getDeclaredFields();
			if (ArrayUtil.isEmpty(fields)) {
				return null;
			}
			for (Field field : fields) {
				// 是否是枚举类中的枚举成员
				String typeName = StringUtil.cutClassAndSpeace(field.getGenericType().toString());
				if (!typeName.equals(enumClass.getName())) {
					continue;
				}
				Object tempObj = field.get(null);

				Field[] innerFields = tempObj.getClass().getDeclaredFields();
				for (Field field2 : innerFields) {
					// 是否与数据库对应的类型一致
					String str = StringUtil.cutClassAndSpeace(field2.getGenericType().toString());
					if (!str.equals(fieldInstance.getClass().getName())) {
						continue;
					}
					field2.setAccessible(true);
					// 数据是否相同
					if (field2.get(tempObj).equals(fieldInstance) || field2.get(tempObj) == fieldInstance) {
						// 相同 ，返回对应的枚举类
						enumObj = (T) tempObj;
					}
				}

			}

		} catch (Exception e) {
			LOGGER.error("converse to Enum failure", e);
			return null;
		}
		return enumObj;
	}

	
	// 根据字段名称获取字段值
	@SuppressWarnings("unchecked")
	public static <T> T toEnum(Object fieldValue, String fieldName, Class<?> enumClass) {
		T t = null;
		try {
			Field[] fields = enumClass.getDeclaredFields();
			if (ArrayUtil.isEmpty(fields)) {
				return null;
			}
			for (Field field : fields) {
				// 是否是枚举类中的枚举成员
				String typeName = StringUtil.cutClassAndSpeace(field.getGenericType().toString());
				if (!typeName.equals(enumClass.getName())) {
					continue;
				}
				Object tempObj = field.get(null);
				Field[] innerFields = tempObj.getClass().getDeclaredFields();
				for (Field innerField : innerFields) {
					if (innerField.getName().equals(fieldName)) {
						innerField.setAccessible(true);
						Object obj = innerField.get(tempObj);
						String str = obj + "";
						
						System.out.println(fieldValue);
						
						
						if (str.equals(fieldValue)) {
							t = (T) tempObj;
						}
					}
				}
				
			}
		} catch (Exception e) {
			LOGGER.error("converser failure", e, true);
		}
		return t;
	}
	
	// 根据字段类型获取字段值
	@SuppressWarnings("unchecked")
	public static <T> T fromEnum(Object instance, Class<T> fieldClass) {
		T t = null;
		try {
			Field[] fields = instance.getClass().getDeclaredFields();
			if (ArrayUtil.isEmpty(fields)) {
				return null;
			}
			for (Field field : fields) {
				String typeName = StringUtil.cutClassAndSpeace(field.getGenericType().toString());
				if (!typeName.equals(fieldClass.getName())) {
					continue;
				}
				field.setAccessible(true);
				Object tempObj = field.get(instance);
				t = (T) tempObj;
			}
		} catch (Exception e) {
			LOGGER.error("converser failure", e, true);
		}
		return t;
	}


}
