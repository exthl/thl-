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
	
	// Map �ӿ�û�м̳� Collection �ӿڣ����Ե���ʵ���ж�
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
