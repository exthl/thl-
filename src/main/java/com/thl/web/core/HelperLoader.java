package com.thl.web.core;

import com.thl.core.util.ClassUtil;
import com.thl.web.aop.AopHelper;
import com.thl.web.cache.CacheHelper;
import com.thl.web.ioc.BeanHelper;
import com.thl.web.ioc.IocHelper;
import com.thl.web.mvc.ControllerHelper;

public class HelperLoader {

	public static void init() {
		Class<?>[] clazzs = new Class[] { //
				ClassUtil.class, //
				ClassHelper.class, //
				BeanHelper.class, //
				AopHelper.class, // AopHelper Ҫ�� IocHelper ǰ�棬��ΪҪע����Ǵ�����
				IocHelper.class, //
				CacheHelper.class, //
				ControllerHelper.class, };
		for (Class<?> clazz : clazzs) {
			ClassUtil.loadClass(clazz.getName(), true);
		}
	}

}
