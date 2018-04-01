package com.thl.core.util;

import java.util.Collection;
import java.util.Map;

public class CollectionUtil {

	public static boolean isEmpty(Collection<?> coll) {
		if (coll != null && coll.size() > 0) {
			return false;
		}
		return true;
	}
	
	public static boolean isNotEmpty(Collection<?> coll) {
		return !isEmpty(coll);
	}
	
	// Map 接口没有继承 Collection 接口，所以单独实现判断
	public static boolean isEmpty(Map<?, ?> map) {
		if (map != null && map.size() > 0) {
			return false;
		}
		return true;
	}
	
	public static boolean isNotEmpty(Map<?, ?> map) {
		return !isEmpty(map);
	}
	
}
