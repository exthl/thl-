package com.thl.dao.core;

public interface ConfigConstant {

	String FILE_SUFFIX = ".properties";
	
	// �����ļ�·��
	String CONFIG_FILE = "thl-daoutil" + FILE_SUFFIX;
	
	// Ĭ��������
	//// ʵ������������ļ��ĺ�׺
	String DOMAIN_CONFIG_FILE_SUFFIX = ".thl.properties";
	//// ʵ��������е�������Ŀ
	///// ����
	String DOMAIN_CONFIG_TABLE = "table";
	///// ����
	String DOMAIN_PRIMARY_KEY = "primary_key";
	///// ���
	String DOMAIN_FOREIGN_KEY = "foreign_key";
	
	
	// ʵ���������·��
	String DOMAIN_PATH = "thl.dao.domain_path";
	
	// ���ݿ�����������
	String JDBC_CONFIG_FILE = "thl.dao.jdbc" + FILE_SUFFIX;
	String JDBC_DRIVER = "thl.dao.jdbc.Driver";
	String JDBC_URL = "thl.dao.jdbc.url";
	String JDBC_USERNAME = "thl.dao.jdbc.username";
	String JDBC_PASSWORD = "thl.dao.jdbc.password";
	
	// ����Դ������
	String DATASOURCE_CONFIG_FILE = "thl.dao.datasource" + FILE_SUFFIX;
	String DATASOURCE_INIT_POOL_SIZE = "thl.dao.ds.init_pool_size"; 
	String DATASOURCE_MIN_POOL_SIZE = "thl.dao.ds.min_pool_size"; 
	String DATASOURCE_MAX_POOL_SIZE = "thl.dao.ds.max_pool_size"; 
	
	// 
	String SHOW_SQL = "thl.dao.show_sql";
}
