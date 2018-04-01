package com.thl.core.util;

public class ArrayUtil {

	public static boolean isEmpty(Object[] args) {
		if (args != null && args.length > 0) {
			return false;
		}
		return true;
	}

	public static boolean isNotEmpty(Object[] args) {
		return !isEmpty(args);
	}

}
