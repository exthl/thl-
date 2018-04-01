package com.thl.dao.orm;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.thl.core.log.Logger;
import com.thl.core.log.LoggerFactory;
import com.thl.core.util.EnumUtil;
import com.thl.core.util.StringUtil;
import com.thl.dao.core.ConfigHelper;
import com.thl.dao.orm.bean.DateTime;

public final class DaoHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(DaoHelper.class);

	public Object converse(Object obj, Field field, Properties prop) {
		return converse(obj, field, prop, "get");
	}
	
	
	public Map<String, Class<?>> getForeignKey(Class<?> clazz) {
		Map<String, Class<?>> map = new HashMap<>();

		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			Class<?> cls = field.getType();
			if (List.class.isAssignableFrom(cls)) {
				Type type = field.getGenericType();
				if (type instanceof ParameterizedType) {
					ParameterizedType pt = (ParameterizedType) type;
					Class<?> genericeType = (Class<?>) pt.getActualTypeArguments()[0];
					Properties prop;
					
					prop = ConfigHelper.getPropertiesByClass(genericeType);
					
					if (prop != null) {
						
						map.put(field.getName(), genericeType);
					}

				}
			}
		}

		return map;
	}

	// 将数据库中的类型转换成实体类中的类型
	public Object converse(Object obj, Field field, Properties prop, String type) throws ClassCastException {
		// 获取实体类中对应字段的名称
		String fieldName = field.getName();
		// 获取实体类中对应字段的类型，带有 "class " 的
		String entityGenericType = field.getGenericType().toString();
		// 去掉其中的 "class "
		String entityType = entityGenericType.substring(entityGenericType.indexOf(" ") + 1);
		// 从配置文件中获取到数据库对应的类型
		String dbType = prop.getProperty(fieldName);

		// LOGGER.log(entityType + "  To  " + dbType);

		// 类型一致，直接返回
		if (entityType.equals(dbType)) {
			return obj;
		}
		// 带有 time 的日期类
		String dateTimePath = DateTime.class.getName();
		if (dbType.equals(dateTimePath)) {
			return obj;
		}
		// 类型不一致
		try {
			// 获取到实体类中字段的字节码类
			// 一般为实体类中的字段类型比数据库中的类型要复杂，所以直接获取实体类中的字节码类型
			Class<?> entityClass = Class.forName(entityType);
			// 获取到其中的所有字段
			Field[] fields = entityClass.getDeclaredFields();
			// 构建一个集合，用于存储符合要求的字段
			List<Field> list = new ArrayList<>();

			for (Field temp : fields) {
				// 如果实体类中的字段类型与数据库对应的字段类型相同
				String string = StringUtil.cutClassAndSpeace(temp.getGenericType().toString());
				if (string.equals(dbType)) {
					// 添加到集合中
					temp.setAccessible(true);
					list.add(temp);
				}
			}
			Object entityFieldObject = null;
			if (type.equals("get")) {
				// 获取到实体类中字段对应存储的对象
				// entityFieldObject = field.get(obj);
				entityFieldObject = obj;
			} else if (type.equals("set")) {
				try {
					// 实例化实体类型对应的字段类
					entityFieldObject = entityClass.newInstance();
				} catch (Exception e) {
					// 不可实例化的类，如枚举类型
					return converseToSpecialEntity(Class.forName(entityType), obj);
				}
			}

			Object dbFieldObject = obj;
			if (list.size() > 1) {
				// 如果存在多个字段符合要求
				Field finalField = null;
				// 获取其中带有"id"的字段
				for (Field temp : list) {
					if (temp.getName().contains("id")) {
						finalField = temp;
					}
				}
				// 如果没有，那就直接返回第一个符合要求的
				finalField = list.get(0);

				if (type.equals("get")) {
					return finalField.get(entityFieldObject);
				} else if (type.equals("set")) {
					// 将数据库中读取的数据设置到刚刚实例化的字段对应
					finalField.set(entityFieldObject, dbFieldObject);
					// 返回字段对象
					return entityFieldObject;
				} else {
					return obj;
				}

			} else if (list.size() == 1) {
				// 如果只有一个符合条件，那这个字段就是数据库对应的字段

				if (type.equals("get")) {
					// 从这个字段中抽取出数据库对应的字段并返回
					return list.get(0).get(entityFieldObject);
				} else if (type.equals("set")) {
					// 将数据库中读取的数据设置到刚刚实例化的字段对应
					list.get(0).set(entityFieldObject, dbFieldObject);
					// 返回字段对象
					return entityFieldObject;
				} else {
					return obj;
				}

			} else {
				LOGGER.error("converse failure", new ClassCastException("converse failure"));
				throw new ClassCastException("converse failure");
			}

		} catch (Exception e) {
			LOGGER.error("converse failure", new ClassCastException("converse failure"));
			throw new ClassCastException();
		}
	}

	private Object converseToSpecialEntity(Class<?> clazz, Object obj)
			throws IllegalArgumentException, IllegalAccessException {
		if (EnumUtil.isEnum(clazz)) {
			return EnumUtil.toEnum(obj, clazz);
		}
		String logstr = "converse to special entity failure";
		LOGGER.error(logstr, new ClassCastException(logstr));
		throw new ClassCastException(logstr);
	}
	
	// 判断是否是基本类型的包装类
	public boolean isWrapClass(Class<?> clz) {     
        try {     
           return ((Class<?>) clz.getField("TYPE").get(null)).isPrimitive();    
        } catch (Exception e) {     
            return false;     
        }     
    }  

}
