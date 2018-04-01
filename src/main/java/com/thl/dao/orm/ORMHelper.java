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
						// 获取别名
						String name = md.getColumnLabel(i);
						map.put(name, obj);
						// 获取列名
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

				// 因为泛型其实只是在编译器中实现的而虚拟机并不认识泛型类项，所以要在虚拟机中将泛型类型进行擦除。
				// 也就是说，在编译阶段使用泛型，运行阶段取消泛型，即擦除。
				// 因为Java的泛型是使用擦除来实现的，这意味着当你在使用泛型时，任何具体的类型信息都被擦除掉了，
				// 也就是说Map<String,List<FoodBean>> 尖括号里面的东西被擦掉了，所以会报异常
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
				throw new UpdateFailedException("更新失败");
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
			throw new UpdateFailedException("操作异常");
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
				throw new UpdateFailedException("更新失败");
			}
			if (ConfigHelper.isShowSql()) {
				LOGGER.log(sql);
			}
		} catch (Exception e) {
			LOGGER.error("execute update failure", e);
			throw new UpdateFailedException("操作异常");
		} finally {
			DatabaseHelper.closeDb(conn, ps, rs);
		}
	}

	// 填充 插入或更新 sql 语句
	public <T> SQLEntity<T> fullSQLEntity(SQLEntity<T> entity, Class<T> clazz) throws UpdateFailedException {
		try {

			T t = entity.getUpdateField();
			String sql1 = "";
			String sql2 = "";
			// Object[] args = new Object[10];
			ArrayList<Object> list = new ArrayList<>();
			int index = 0;
			Field[] fields = clazz.getDeclaredFields();

			// 判断是不是插入语句
			if (entity.getCondition() == null) {
				sql1 = "INSERT INTO " + entity.getTableName() + "(";
				sql2 = " VALUES(";
				// 填充插入语句
				for (Field field : fields) {
					field.setAccessible(true);
					// 是否存在该字段
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
				// 去除冗余的 符号
				sql1 = sql1.substring(0, sql1.lastIndexOf(","));
				sql2 = sql2.substring(0, sql2.lastIndexOf(","));
				sql1 += ") ";
				sql2 += ") ";
			} else {
				sql1 = "UPDATE " + entity.getTableName() + " SET ";
				sql2 = " WHERE ";
				// 填充更新语句
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
				// 去除冗余的 符号
				sql1 = sql1.substring(0, sql1.lastIndexOf(","));
				sql2 = sql2.substring(0, sql2.lastIndexOf("AND"));
			}
			String sql = sql1 + sql2;
			entity.setArgs(list.toArray());
			entity.setSql(sql);
			return entity;
		} catch (Exception e) {
			LOGGER.error("full SQL statement failure", e);
			throw new UpdateFailedException("构建语句出错");
		}
	}

}
