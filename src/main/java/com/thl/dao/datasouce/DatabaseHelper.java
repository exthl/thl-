package com.thl.dao.datasouce;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.thl.core.log.Logger;
import com.thl.core.log.LoggerFactory;
import com.thl.core.util.PropUtil;
import com.thl.dao.core.ConfigConstant;
import com.thl.dao.core.ConfigHelper;

public final class DatabaseHelper {

	private static String driver;
	private static String url;
	private static String username;
	private static String password;

	private static MyDataSource ds = null;

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);
	private static final ThreadLocal<Connection> CONNECTION_HOLDER = new ThreadLocal<>();

	static {

		Properties prop = null;
		InputStream is = null;
		try {
			// 通过配置文件读取相关信息
			prop = ConfigHelper.getMainProperties();

			String jdbcProperties = prop.getProperty(ConfigConstant.JDBC_CONFIG_FILE);

			String logString = "properties for jdbc is not found";

			if (jdbcProperties == null) {
				LOGGER.log(logString);
			}

			if (jdbcProperties != null) {
				try {
					prop = PropUtil.loadProperties(jdbcProperties);
				} catch (Exception e) {
					LOGGER.log(logString);
				}
			}

			driver = prop.getProperty(ConfigConstant.JDBC_DRIVER);
			url = prop.getProperty(ConfigConstant.JDBC_URL);
			username = prop.getProperty(ConfigConstant.JDBC_USERNAME);
			password = prop.getProperty(ConfigConstant.JDBC_PASSWORD);

			// 注册驱动
			Class.forName(driver);

			ds = new MyDataSource();
			ds.init();

		} catch (Exception e) {
			LOGGER.error("read jdbc properties failure", e, true);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (Exception e) {
				LOGGER.error("close input stream failure", e, true);
			}
		}

	}
	
	
	public static void beginTransaction() {
		Connection conn = getConnection();
		if (conn != null) {
			try {
				conn.setAutoCommit(false);
			} catch (SQLException e) {
				LOGGER.error("begin transaction failure", e, true);
			} finally {
				CONNECTION_HOLDER.set(conn);
			}
		}
	}
	
	public static void commitTransaction() {
		Connection conn = getConnection();
		if (conn != null) {
			try {
				conn.commit();
				conn.close();
			} catch (Exception e) {
				LOGGER.error("commit transaction failure", e, true);
			} finally {
				CONNECTION_HOLDER.remove();
			}
		}
	}
	
	public static void rollbackTransaction() {
		Connection conn = getConnection();
		if (conn != null) {
			try {
				conn.rollback();
				conn.close();
			} catch (Exception e) {
				LOGGER.error("rollback transaction failure", e, true);
			} finally {
				CONNECTION_HOLDER.remove();
			}
		}
	}

	public static Connection createConnection() throws SQLException {
		Connection conn = DriverManager.getConnection(url, username, password);
		return conn;
	}

	public static Connection getConnection(String username, String password) throws SQLException {
		Connection conn = DriverManager.getConnection(url, username, password);
		CONNECTION_HOLDER.set(conn);
		return conn;
	}

	public static Connection getConnection() {
		Connection conn = CONNECTION_HOLDER.get();
		if (conn != null) {
			return conn;
		}
		conn = ds.getConnection();
		CONNECTION_HOLDER.set(conn);
		return conn;
		/*
		// return ds.getConnection();
		try {
			return createConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		 */
	}

	public static MyDataSource getMyDataSource() {
		return ds;
	}

	// 关闭资源
	public static void closeDb(Connection conn, PreparedStatement ps, ResultSet rs) {
		try {
			conn = CONNECTION_HOLDER.get();
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			LOGGER.error("close Connection failure", e);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				LOGGER.error("close PreparedStatement failure", e);
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (SQLException e) {
					LOGGER.error("close ResultSet failure", e);
				}
			}
		}
	}
}
