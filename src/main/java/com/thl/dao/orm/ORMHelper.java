package com.thl.dao.orm;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.thl.core.log.Logger;
import com.thl.core.log.LoggerFactory;
import com.thl.core.util.CollectionUtil;
import com.thl.core.util.ReflectionUtil;
import com.thl.core.util.StringUtil;
import com.thl.dao.core.ConfigHelper;
import com.thl.dao.datasouce.DatabaseHelper;
import com.thl.dao.orm.bean.SQLEntity;
import com.thl.dao.orm.exception.ExecuteFailedException;
import com.thl.dao.orm.exception.QueryFailedException;
import com.thl.dao.orm.exception.UpdateFailedException;
import com.thl.web.annotation.Repository;

@Repository
public class ORMHelper {

	protected static final Logger LOGGER = LoggerFactory.getLogger(ORMHelper.class);
	protected DaoHelper daoUtil = new DaoHelper();

	public List<Map<String, Object>> execute(String sql, Object arg) throws ExecuteFailedException {
		Object[] args = new Object[] { arg };
		return execute(sql, args);
	}

	public List<Map<String, Object>> execute(String sql, Object[] args) throws ExecuteFailedException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Map<String, Object>> results = null;
		try {
			conn = DatabaseHelper.getConnection();
			ps = conn.prepareStatement(sql);
			for (int i = 1; args != null && i <= args.length; i++) {
				ps.setObject(i, args[i - 1]);
			}
			rs = ps.executeQuery();
			results = new ArrayList<>();
			while (rs.next()) {
				int row = rs.getRow();
				Map<String, Object> map = new HashMap<>();
				ResultSetMetaData md = rs.getMetaData();
				for (int i = 1; i <= row; i++) {
					try {
						Object obj = rs.getObject(i);
						// ��ȡ����
						String name = md.getColumnLabel(i);
						map.put(name, obj);
						// ��ȡ����
						name = md.getColumnName(i);
						map.put(name, obj);
					} catch (Exception e) {
						continue;
					}
				}
				results.add(map);
			}
			
			if (ConfigHelper.isShowSql()) {
				LOGGER.log(sql);
			}
			
			return results;
		} catch (Exception e) {
			LOGGER.error("execute failure", e);
			throw new ExecuteFailedException();
		} finally {
			DatabaseHelper.closeDb(conn, ps, rs);
		}

	}

	public <T> List<T> executeQuery(String sql, Object arg, Class<T> clazz) throws QueryFailedException {
		Object[] args = new Object[] { arg };
		return executeQuery(sql, args, clazz);
	}

	public <T> List<T> executeQuery(String sql, Object[] args, Class<T> clazz) throws QueryFailedException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHelper.getConnection();
			ps = conn.prepareStatement(sql);
			for (int i = 1; args != null && i <= args.length; i++) {
				ps.setObject(i, args[i - 1]);
			}
			rs = ps.executeQuery();
			List<T> list = new ArrayList<>();
			while (rs.next()) {
				Object obj = clazz.newInstance();
				Field[] fileds = clazz.getDeclaredFields();

				for (Field field : fileds) {
					field.setAccessible(true);
					Properties prop = ConfigHelper.getPropertiesByClass(clazz);
					if (!prop.containsKey(field.getName())) {
						continue;
					}
					field.set(obj, //
							daoUtil.converse(rs.getObject(field.getName()), field, prop, "set"));
				}

				Map<String, Class<?>> foreignKeys = daoUtil.getForeignKey(clazz);
				if (CollectionUtil.isNotEmpty(foreignKeys)) {
					List<Object> primaryKeys = new ArrayList<>();
					String[] primaryKeyStr = ConfigHelper.getPrimaryKey(clazz);
					for (String fieldName : primaryKeyStr) {
						Field field = clazz.getDeclaredField(fieldName);
						field.setAccessible(true);
						Object primaryKey = field.get(obj);
						primaryKeys.add(primaryKey);
					}

					for (Map.Entry<String, Class<?>> entry : foreignKeys.entrySet()) {
						Class<?> cls = entry.getValue();
						sql = "SELECT * FROM " + ConfigHelper.getTableName(cls) + " WHERE ";

						String[] foreignKeyStrs = ConfigHelper.getForeignKey(cls);
						for (String foreignKeyStr : foreignKeyStrs) {
							if (StringUtil.isEmpty(foreignKeyStr)) {
								continue;
							}
							Field field = cls.getDeclaredField(foreignKeyStr);
							if (field.getType().equals(clazz)) {
								sql += (foreignKeyStr + " =? AND ");
							}
						}
						if (sql.contains("AND")) {
							sql = sql.substring(0, sql.lastIndexOf("AND"));
						}

						try {
							List<?> foreignKeyList = executeQuery(sql, primaryKeys.toArray(), cls);
							Field field = clazz.getDeclaredField(entry.getKey());
							ReflectionUtil.setField(obj, field, foreignKeyList);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				// ��Ϊ������ʵֻ���ڱ�������ʵ�ֵĶ������������ʶ�����������Ҫ��������н��������ͽ��в�����
				// Ҳ����˵���ڱ���׶�ʹ�÷��ͣ����н׶�ȡ�����ͣ���������
				// ��ΪJava�ķ�����ʹ�ò�����ʵ�ֵģ�����ζ�ŵ�����ʹ�÷���ʱ���κξ����������Ϣ�����������ˣ�
				// Ҳ����˵Map<String,List<FoodBean>> ����������Ķ����������ˣ����Իᱨ�쳣
				@SuppressWarnings("unchecked")
				T t = (T) obj;
				list.add(t);
			}

			if (ConfigHelper.isShowSql()) {
				LOGGER.log(sql);
			}

			return list;
		} catch (Exception e) {
			LOGGER.error("execute query failure", e);
			throw new QueryFailedException();
		} finally {
			DatabaseHelper.closeDb(conn, ps, rs);
		}

	}

	@SuppressWarnings("unchecked")
	public <T> Object executeSave(String sql, Object[] args, Class<T> clazz, T updateArgs) throws UpdateFailedException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Object primaryKey;
		try {
			conn = DatabaseHelper.getConnection();
			ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			for (int i = 1; args != null && i <= args.length; i++) {
				ps.setObject(i, args[i - 1]);
			}

			int flag = ps.executeUpdate();
			if (flag <= 0) {
				throw new UpdateFailedException("����ʧ��");
			}
			rs = ps.getGeneratedKeys();
			rs.next();
			primaryKey = rs.getObject(1);
			if (ConfigHelper.isShowSql()) {
				LOGGER.log(sql);
			}

			Map<String, Class<?>> foreignKeys = daoUtil.getForeignKey(clazz);
			if (CollectionUtil.isNotEmpty(foreignKeys)) {
				
				for (Map.Entry<String, Class<?>> entry : foreignKeys.entrySet()) {
					try {
						Field field = clazz.getDeclaredField(entry.getKey());
						field.setAccessible(true);
						Class<Object> foreignClass = (Class<Object>) entry.getValue();

						List<Object> list = (List<Object>) field.get(updateArgs);
						
						String[] foreignKeyStrs = ConfigHelper.getForeignKey(foreignClass);
						Field foreignField = null;
						for (String foreignKeyStr : foreignKeyStrs) {
							if (StringUtil.isEmpty(foreignKeyStr)) {
								continue;
							}
							Field temp = foreignClass.getDeclaredField(foreignKeyStr);
							if (temp.getType().equals(clazz)) {
								foreignField = temp;
							}
						}
						
						List<Field> primaryKeyFields = new ArrayList<>();
						String[] primaryKeyStr = ConfigHelper.getPrimaryKey(clazz);
						for (String fieldName : primaryKeyStr) {
							Field tempfield = clazz.getDeclaredField(fieldName);
							tempfield.setAccessible(true);
							primaryKeyFields.add(tempfield);
						}
						
						for (Object object : list) {
							if (foreignField != null) {
								Object foreignObject = foreignField.getType().newInstance();
								if (CollectionUtil.isNotEmpty(primaryKeyFields)) {
									for (Field f : primaryKeyFields) {
										ReflectionUtil.setField(foreignObject, f, primaryKey);
									}
								}
								ReflectionUtil.setField(object, foreignField, foreignObject);
							}
							SQLEntity<Object> condition1 = new SQLEntity<>(ConfigHelper.getTableName(foreignClass), object);
							SQLEntity<Object> result1 = fullSQLEntity(condition1, foreignClass);
							executeUpdate(result1.getSql(), result1.getArgs(), foreignClass);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		} catch (Exception e) {
			LOGGER.error("execute update failure", e);
			throw new UpdateFailedException("�����쳣");
		} finally {
			DatabaseHelper.closeDb(conn, ps, rs);
		}
		return primaryKey;
	}

	public <T> void executeUpdate(String sql, Object arg, Class<T> clazz) throws UpdateFailedException {
		Object[] args = new Object[] { arg };
		executeUpdate(sql, args, clazz);
	}

	public <T> void executeUpdate(String sql, Object[] args, Class<T> clazz) throws UpdateFailedException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DatabaseHelper.getConnection();
			ps = conn.prepareStatement(sql);
			for (int i = 1; args != null && i <= args.length; i++) {
				ps.setObject(i, args[i - 1]);
			}

			int flag = ps.executeUpdate();
			if (flag <= 0) {
				throw new UpdateFailedException("����ʧ��");
			}
			if (ConfigHelper.isShowSql()) {
				LOGGER.log(sql);
			}
		} catch (Exception e) {
			LOGGER.error("execute update failure", e);
			throw new UpdateFailedException("�����쳣");
		} finally {
			DatabaseHelper.closeDb(conn, ps, rs);
		}
	}

	// ��� �������� sql ���
	public <T> SQLEntity<T> fullSQLEntity(SQLEntity<T> entity, Class<T> clazz) throws UpdateFailedException {
		try {

			T t = entity.getUpdateField();
			String sql1 = "";
			String sql2 = "";
			// Object[] args = new Object[10];
			ArrayList<Object> list = new ArrayList<>();
			int index = 0;
			Field[] fields = clazz.getDeclaredFields();

			// �ж��ǲ��ǲ������
			if (entity.getCondition() == null) {
				sql1 = "INSERT INTO " + entity.getTableName() + "(";
				sql2 = " VALUES(";
				// ���������
				for (Field field : fields) {
					field.setAccessible(true);
					// �Ƿ���ڸ��ֶ�
					Properties prop = ConfigHelper.getPropertiesByClass(clazz);
					if (!prop.containsKey(field.getName())) {
						continue;
					}
					Object temp = field.get(t);
					if (temp != null) {
						temp = daoUtil.converse(temp, field, prop);
						sql1 += " " + field.getName() + ", ";
						sql2 += " ?, ";
						// args[index] = temp;
						list.add(index, temp);
						index++;
					} /*
						 * else if (field.getName().contains("id") // ||
						 * field.getName().contains("Id") // ||
						 * field.getName().contains("ID")) { if
						 * (daoUtil.isWrapClass(field.getType())) { sql1 += " "
						 * + field.getName() + ", "; sql2 += " null, "; } }
						 */
				}
				// ȥ������� ����
				sql1 = sql1.substring(0, sql1.lastIndexOf(","));
				sql2 = sql2.substring(0, sql2.lastIndexOf(","));
				sql1 += ") ";
				sql2 += ") ";
			} else {
				sql1 = "UPDATE " + entity.getTableName() + " SET ";
				sql2 = " WHERE ";
				// ���������
				for (Field field : fields) {
					field.setAccessible(true);
					Properties prop = ConfigHelper.getPropertiesByClass(clazz);
					if (!prop.containsKey(field.getName())) {
						continue;
					}
					Object temp = field.get(t);
					if (temp != null) {
						temp = daoUtil.converse(temp, field, prop);
						sql1 += " " + field.getName() + "=?, ";
						// args[index] = temp;
						list.add(index, temp);
						index++;
					}
				}
				for (Field field : fields) {
					field.setAccessible(true);
					Properties prop = ConfigHelper.getPropertiesByClass(clazz);
					if (!prop.containsKey(field.getName())) {
						continue;
					}
					Object temp = field.get(entity.getCondition());
					if (temp != null) {
						temp = daoUtil.converse(temp, field, prop);
						sql2 += " " + field.getName() + "=? AND ";
						// args[index] = temp;
						list.add(index, temp);
						index++;
					}
				}
				// ȥ������� ����
				sql1 = sql1.substring(0, sql1.lastIndexOf(","));
				sql2 = sql2.substring(0, sql2.lastIndexOf("AND"));
			}
			String sql = sql1 + sql2;
			entity.setArgs(list.toArray());
			entity.setSql(sql);
			return entity;
		} catch (Exception e) {
			LOGGER.error("full SQL statement failure", e);
			throw new UpdateFailedException("����������");
		}
	}

}
