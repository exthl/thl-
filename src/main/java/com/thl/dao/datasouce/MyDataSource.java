package com.thl.dao.datasouce;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.thl.core.log.LoggerFactory;
import com.thl.core.util.PropUtil;
import com.thl.dao.core.ConfigConstant;
import com.thl.dao.core.ConfigHelper;
import com.thl.dao.orm.exception.ConnectionPoolBusyException;

public class MyDataSource implements DataSource {

	// 使用链式列表，方便增删
	private LinkedList<Connection> pool = new LinkedList<>();
	
	
	//
	private static int initPoolSize = 5; 
	private static int minPoolSize = 5;
	private static int maxPoolSize = 10;

	private static final com.thl.core.log.Logger LOGGER = LoggerFactory.getLogger(MyDataSource.class);
	
	// 静态代码块读取参数
	static {
		Properties prop = null;
		InputStream is = null;
		try {
			prop = ConfigHelper.getMainProperties();
			
			String dataSouceProperties = prop.getProperty(ConfigConstant.DATASOURCE_CONFIG_FILE);
			
			String logString = "properties for data source is not found";
			
			if (dataSouceProperties == null) {
				LOGGER.log(logString);
			}
			
			if (dataSouceProperties != null) {
				try {
					prop = PropUtil.loadProperties(dataSouceProperties);
				} catch (Exception e) {
					LOGGER.log(logString);
				}
			}
			initPoolSize = PropUtil.getInteger(prop, ConfigConstant.DATASOURCE_INIT_POOL_SIZE, initPoolSize);
			minPoolSize = PropUtil.getInteger(prop, ConfigConstant.DATASOURCE_MIN_POOL_SIZE, minPoolSize);
			maxPoolSize = PropUtil.getInteger(prop, ConfigConstant.DATASOURCE_MAX_POOL_SIZE, maxPoolSize);

		} catch (Exception e) {
			LOGGER.error("read database properties file failure", e, true);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				LOGGER.error("close input stream failure", e, true);
			}
		}
	}

	// 初始化数据源中的链接
	public void init() {
		for (; pool.size() < initPoolSize;) {
			try {
				pool.add(DatabaseHelper.createConnection());
			} catch (SQLException e) {
				LOGGER.error("initializate datasource failure", e, true);
			}
		}
	}

	// 返回代理的链接，也就是 connection 调用 close() 方法后重新返回到连接池中
	@Override
	public Connection getConnection() {
		Connection connection = null;
		if (pool.size() > 0) {
			final Connection conn = pool.removeFirst();
			connection = (Connection) Proxy.newProxyInstance( //
					MyDataSource.class.getClassLoader(), //
					conn.getClass().getInterfaces(), //
					new InvocationHandler() {
						@Override
						public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
							// 筛选出 close() 方法
							if (method.getName().equals("close")) {
								pool.add(conn);
							} else {
								return method.invoke(conn, args);
							}
							return null;
						}
					});
		} else {
			Exception e = new ConnectionPoolBusyException("连接池中数量不足");
			LOGGER.error("connection from pool is not enough", e, true);
		}
		return connection;

	}
	
	public int size() {
		return pool.size();
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return null;
	}

}
