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

	// �����ݿ��е�����ת����ʵ�����е�����
	public Object converse(Object obj, Field field, Properties prop, String type) throws ClassCastException {
		// ��ȡʵ�����ж�Ӧ�ֶε�����
		String fieldName = field.getName();
		// ��ȡʵ�����ж�Ӧ�ֶε����ͣ����� "class " ��
		String entityGenericType = field.getGenericType().toString();
		// ȥ�����е� "class "
		String entityType = entityGenericType.substring(entityGenericType.indexOf(" ") + 1);
		// �������ļ��л�ȡ�����ݿ��Ӧ������
		String dbType = prop.getProperty(fieldName);

		// LOGGER.log(entityType + "  To  " + dbType);

		// ����һ�£�ֱ�ӷ���
		if (entityType.equals(dbType)) {
			return obj;
		}
		// ���� time ��������
		String dateTimePath = DateTime.class.getName();
		if (dbType.equals(dateTimePath)) {
			return obj;
		}
		// ���Ͳ�һ��
		try {
			// ��ȡ��ʵ�������ֶε��ֽ�����
			// һ��Ϊʵ�����е��ֶ����ͱ����ݿ��е�����Ҫ���ӣ�����ֱ�ӻ�ȡʵ�����е��ֽ�������
			Class<?> entityClass = Class.forName(entityType);
			// ��ȡ�����е������ֶ�
			Field[] fields = entityClass.getDeclaredFields();
			// ����һ�����ϣ����ڴ洢����Ҫ����ֶ�
			List<Field> list = new ArrayList<>();

			for (Field temp : fields) {
				// ���ʵ�����е��ֶ����������ݿ��Ӧ���ֶ�������ͬ
				String string = StringUtil.cutClassAndSpeace(temp.getGenericType().toString());
				if (string.equals(dbType)) {
					// ��ӵ�������
					temp.setAccessible(true);
					list.add(temp);
				}
			}
			Object entityFieldObject = null;
			if (type.equals("get")) {
				// ��ȡ��ʵ�������ֶζ�Ӧ�洢�Ķ���
				// entityFieldObject = field.get(obj);
				entityFieldObject = obj;
			} else if (type.equals("set")) {
				try {
					// ʵ����ʵ�����Ͷ�Ӧ���ֶ���
					entityFieldObject = entityClass.newInstance();
				} catch (Exception e) {
					// ����ʵ�������࣬��ö������
					return converseToSpecialEntity(Class.forName(entityType), obj);
				}
			}

			Object dbFieldObject = obj;
			if (list.size() > 1) {
				// ������ڶ���ֶη���Ҫ��
				Field finalField = null;
				// ��ȡ���д���"id"���ֶ�
				for (Field temp : list) {
					if (temp.getName().contains("id")) {
						finalField = temp;
					}
				}
				// ���û�У��Ǿ�ֱ�ӷ��ص�һ������Ҫ���
				finalField = list.get(0);

				if (type.equals("get")) {
					return finalField.get(entityFieldObject);
				} else if (type.equals("set")) {
					// �����ݿ��ж�ȡ���������õ��ո�ʵ�������ֶζ�Ӧ
					finalField.set(entityFieldObject, dbFieldObject);
					// �����ֶζ���
					return entityFieldObject;
				} else {
					return obj;
				}

			} else if (list.size() == 1) {
				// ���ֻ��һ������������������ֶξ������ݿ��Ӧ���ֶ�

				if (type.equals("get")) {
					// ������ֶ��г�ȡ�����ݿ��Ӧ���ֶβ�����
					return list.get(0).get(entityFieldObject);
				} else if (type.equals("set")) {
					// �����ݿ��ж�ȡ���������õ��ո�ʵ�������ֶζ�Ӧ
					list.get(0).set(entityFieldObject, dbFieldObject);
					// �����ֶζ���
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
	
	// �ж��Ƿ��ǻ������͵İ�װ��
	public boolean isWrapClass(Class<?> clz) {     
        try {     
           return ((Class<?>) clz.getField("TYPE").get(null)).isPrimitive();    
        } catch (Exception e) {     
            return false;     
        }     
    }  

}
