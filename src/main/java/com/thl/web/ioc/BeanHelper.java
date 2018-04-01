package com.thl.web.ioc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.thl.core.log.Logger;
import com.thl.core.log.LoggerFactory;
import com.thl.core.util.CollectionUtil;
import com.thl.core.util.ReflectionUtil;
import com.thl.web.core.ClassHelper;

public class BeanHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(BeanHelper.class);

	// 相当于是一个 Bean 容器
	private static final Map<Class<?>, Object> BEAN_MAP = new HashMap<>();

	static {
		// 将所有 Controller 和 Service 实例化，并存入到 BeanMap 中
		Set<Class<?>> classSet = ClassHelper.getBeanClassSet();
		for (Class<?> clazz : classSet) {
			Object obj = null;
			Class<?> superClass = null;
			if (clazz.isInterface()) {
				superClass = clazz;
				// 获取所有实现类
				Set<Class<?>> set = ClassHelper.getClassSetBySupper(superClass);
				if (CollectionUtil.isEmpty(set)) {
					continue;
				} 
				// 选择一个作为子类
				Iterator<Class<?>> it = set.iterator();
				if (it.hasNext()) {
					clazz = it.next();
				}
			} else {
				superClass = clazz.getSuperclass();
				if (!superClass.isInterface()) {
					superClass = null;
				}
			}
			obj = ReflectionUtil.newInstance(clazz);
			
			// 添加 接口类与对应实现类的映射
			if (superClass != null) {
				BEAN_MAP.put(superClass, obj);
			}
			
			BEAN_MAP.put(clazz, obj);
		}
	}

	public static Map<Class<?>, Object> getBeanMap() {
		return BEAN_MAP;
	}

	public static Object getBean(Class<?> clazz) {
		if (!BEAN_MAP.containsKey(clazz)) {
			LOGGER.error("the key is not found in bean map", new Exception("the key is not found"), true);
		}
		return BEAN_MAP.get(clazz);
	}

	// 方便用 代理对象 替换 原始对象
	public static void setBean(Class<?> key, Object value) {
		BEAN_MAP.put(key, value);
	}
	
}
