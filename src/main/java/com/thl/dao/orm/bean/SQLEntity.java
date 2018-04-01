package com.thl.dao.orm.bean;

public class SQLEntity<T> {

	private String tableName;
	private T updateField;
	private T condition;
	private String sql;
	private Object[] args;
	
	public SQLEntity() {}
	
	// ²åÈë
	public SQLEntity(String tableName, T updateField) {
		this.tableName = tableName;
		this.updateField = updateField;
	}
	// ¸üÐÂ
	public SQLEntity(String tableName, T updateField, T condition) {
		this.tableName = tableName;
		this.updateField = updateField;
		this.condition = condition;
	}
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public T getUpdateField() {
		return updateField;
	}
	public void setUpdateField(T updateField) {
		this.updateField = updateField;
	}
	public T getCondition() {
		return condition;
	}
	public void setCondition(T condition) {
		this.condition = condition;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public Object[] getArgs() {
		return args;
	}
	public void setArgs(Object[] args) {
		this.args = args;
	}
	
	
}
