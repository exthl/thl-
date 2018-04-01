package com.thl.web.cache;

import java.util.HashMap;
import java.util.Map;

public class CacheHelper {

	public static final Map<Object, Object> CACHE_MAP;
	
	static {
		 CACHE_MAP = new HashMap<>();
	}
	
	public static boolean isContain(Object key){
		return CACHE_MAP.containsKey(key);
	}
	
	public static void addToCache(Object key, Object value) {
		CACHE_MAP.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getValue(Object key, Class<T> cls) {
		return (T) CACHE_MAP.get(key);
	}
	
	public static Object getValue(Object key) {
		return CACHE_MAP.get(key);
	}
	
	public static Object removeValue(Object key) {
		return CACHE_MAP.remove(key);
	}
	
	public static void clear() {
		CACHE_MAP.clear();
	}
	
}
