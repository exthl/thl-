package com.thl.dao.core;

public interface ConfigConstant {

	String FILE_SUFFIX = ".properties";
	
	// 配置文件路径
	String CONFIG_FILE = "thl-daoutil" + FILE_SUFFIX;
	
	// 默认配置项
	//// 实体域对象配置文件的后缀
	String DOMAIN_CONFIG_FILE_SUFFIX = ".thl.properties";
	//// 实体域对象中的配置项目
	///// 表名
	String DOMAIN_CONFIG_TABLE = "table";
	///// 主键
	String DOMAIN_PRIMARY_KEY = "primary_key";
	///// 外键
	String DOMAIN_FOREIGN_KEY = "foreign_key";
	
	
	// 实体域对象存放路径
	String DOMAIN_PATH = "thl.dao.domain_path";
	
	// 数据库连接配置项
	String JDBC_CONFIG_FILE = "thl.dao.jdbc" + FILE_SUFFIX;
	String JDBC_DRIVER = "thl.dao.jdbc.Driver";
	String JDBC_URL = "thl.dao.jdbc.url";
	String JDBC_USERNAME = "thl.dao.jdbc.username";
	String JDBC_PASSWORD = "thl.dao.jdbc.password";
	
	// 数据源配置项
	String DATASOURCE_CONFIG_FILE = "thl.dao.datasource" + FILE_SUFFIX;
	String DATASOURCE_INIT_POOL_SIZE = "thl.dao.ds.init_pool_size"; 
	String DATASOURCE_MIN_POOL_SIZE = "thl.dao.ds.min_pool_size"; 
	String DATASOURCE_MAX_POOL_SIZE = "thl.dao.ds.max_pool_size"; 
	
	// 
	String SHOW_SQL = "thl.dao.show_sql";
}
