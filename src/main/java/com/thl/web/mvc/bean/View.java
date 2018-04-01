package com.thl.web.mvc.bean;

import java.util.HashMap;
import java.util.Map;

public class View {

	// ∑µªÿ“≥√Ê
	
	private String path;
	private Map<String, Object> requestModel;
	private Map<String, Object> sessionModel;
	
	public View(String path) {
		this.path = path;
		requestModel = new HashMap<>();
		sessionModel = new HashMap<>();
	}
	
	public void addModel(String key, Object value) {
		requestModel.put(key, value);
	}
	
	public void addModelToSession(String key, Object value) {
		sessionModel.put(key, value);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Map<String, Object> getRequestModel() {
		return requestModel;
	}

	public Map<String, Object> getSessionModel() {
		return sessionModel;
	}
	
	
}
