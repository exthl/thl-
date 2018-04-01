package com.thl.dao.orm;

import java.lang.reflect.Field;
import java.util.Map;

import com.thl.core.util.ArrayUtil;
import com.thl.core.util.CollectionUtil;
import com.thl.core.util.StringUtil;
import com.thl.dao.core.ConfigHelper;
import com.thl.dao.orm.bean.SQLEntity;
import com.thl.dao.orm.exception.QueryFailedException;
import com.thl.dao.orm.exception.UpdateFailedException;
import com.thl.web.annotation.Repository;

@Repository
public class ORMTemplate extends ORMHelper {

	/*public <T> T get(String id, Class<T> clazz) throws QueryFailedException {
		return get(id, clazz, 0);
	}

	public <T> T get(Integer id, Class<T> clazz) throws QueryFailedException {
		return get(id, clazz, 0);
	}

	public <T> T get(Long id, Class<T> clazz) throws QueryFailedException {
		return get(id, clazz, 0);
	}*/

	// @Param i 标记位置，用于区分其他几个方法
	public <T> T get(Object id, Class<T> clazz) throws QueryFailedException {
		String sql;
		T t;
		try {
			sql = "SELECT * FROM " + ConfigHelper.getTableName(clazz) + " WHERE id=?";
			t = (T) executeQuery(sql, id, clazz).get(0);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new QueryFailedException();
		}
		return t;
	}
/*
	public <T> void delete(String id, Class<T> clazz) throws UpdateFailedException {
		delete(id, clazz, 0);
	}

	public <T> void delete(Integer id, Class<T> clazz) throws UpdateFailedException {
		delete(id, clazz, 0);
	}

	public <T> void delete(Long id, Class<T> clazz) throws UpdateFailedException {
		delete(id, clazz, 0);
	}
*/
	// @Param i 标记位置，用于区分其他几个方法
	public <T> void delete(Object id, Class<T> clazz) throws UpdateFailedException {
		String sql;
		try {
			Map<String, Class<?>> foreignKeys = daoUtil.getForeignKey(clazz);
			if (CollectionUtil.isNotEmpty(foreignKeys)) {
				for (Map.Entry<String, Class<?>> entry : foreignKeys.entrySet()) {
					Class<?> cls = entry.getValue();
					String[] foreignKeyStrs = ConfigHelper.getForeignKey(cls);
					sql = "DELETE FROM " + ConfigHelper.getTableName(cls) + " WHERE ";
					
					for (String foreignKeyStr : foreignKeyStrs) {
						if (StringUtil.isEmpty(foreignKeyStr)) {
							continue;
						}
						Field field = cls.getDeclaredField(foreignKeyStr);
						if (field.getType().equals(clazz)) {
							sql += (foreignKeyStr + " =? AND ");
						}
					}
					
					sql = sql.substring(0, sql.lastIndexOf("AND"));
					executeUpdate(sql, id, cls);
				}
			}
			sql = "DELETE FROM " + ConfigHelper.getTableName(clazz) + " WHERE id=?";
			executeUpdate(sql, id, clazz);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new UpdateFailedException();
		}
	}
	
	public <T> Object save(T updateArgs, Class<T> clazz) throws UpdateFailedException {
		SQLEntity<T> condition;
		Object primaryKey;
		try {
			condition = new SQLEntity<>(ConfigHelper.getTableName(clazz), updateArgs);
			SQLEntity<T> result = fullSQLEntity(condition, clazz);
			primaryKey = executeSave(result.getSql(), result.getArgs(), clazz, updateArgs);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new UpdateFailedException();
		}
		return primaryKey;
	}

	public <T> void update(String id, T updateArgs, Class<T> clazz) throws UpdateFailedException {
		update(id, updateArgs, clazz, new String[] { "java.lang.String" });
	}

	public <T> void update(Integer id, T updateArgs, Class<T> clazz) throws UpdateFailedException {
		update(id, updateArgs, clazz, new String[] { "java.lang.Integer", "int" });
	}

	public <T> void update(Long id, T updateArgs, Class<T> clazz) throws UpdateFailedException {
		update(id, updateArgs, clazz, new String[] { "java.lang.Long", "long" });
	}

	// @Param args 判断参数类型列表，是否匹配其中的类型
	private <T> void update(Object id, T updateArgs, Class<T> clazz, String[] args) throws UpdateFailedException {
		T condition2;
		SQLEntity<T> condition;
		try {
			condition2 = clazz.newInstance();
			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				String fieldName = field.getName();
				String primaryKey = ConfigHelper.getPrimaryKey(clazz)[0];
				if (fieldName.equals(primaryKey)) {
					String fieldType = field.getGenericType().toString();
					if (fieldType.contains("class")) {
						fieldType = fieldType.substring(fieldType.indexOf("class") + 6);
					}
					if (ArrayUtil.isNotEmpty(args)) {
						for (String arg : args) {
							if (fieldType.equals(arg)) {
								field.setAccessible(true);
								field.set(condition2, id);
							}
						}
					}
				}
			}
			condition = new SQLEntity<>(ConfigHelper.getTableName(clazz), updateArgs, condition2);
			SQLEntity<T> result = fullSQLEntity(condition, clazz);
			executeUpdate(result.getSql(), result.getArgs(), clazz);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UpdateFailedException();
		}
	}


}
