package com.thl.web.ioc;

import java.lang.reflect.Field;
import java.util.Map;

import com.thl.core.util.ArrayUtil;
import com.thl.core.util.CollectionUtil;
import com.thl.core.util.ReflectionUtil;

public class IocHelper {
	// 实现控制反转（IOC），也被成为是依赖注入（DI）
	
	// 将所有标有注解 Inject 的字段注入实例
	
	static {
		// 获取 Bean 容器
		Map<Class<?>, Object> beanMap = BeanHelper.getBeanMap();
		if (CollectionUtil.isNotEmpty(beanMap)) {
			// 遍历，找出带有Inject 注解的字段，并注入实例
			for (Map.Entry<Class<?>, Object> entry : beanMap.entrySet()) {
				Class<?> beanClass = entry.getKey();
				Object beanInstance = entry.getValue();
				Field[] beanFields = beanClass.getDeclaredFields();
				if (ArrayUtil.isNotEmpty(beanFields)) {
					for (Field beanField : beanFields) {
						// 找到 要求字段
						if (beanField.isAnnotationPresent(Inject.class)) {
							// 从 bean map 中获取实例
							Class<?> beanFieldClass = beanField.getType();
							Object beanFieldInstance = BeanHelper.getBean(beanFieldClass);
							// 注入对象
							if (beanFieldInstance != null) {
								ReflectionUtil.setField(beanInstance, beanField, beanFieldInstance);
							}
						}
					}
				}
				
			}
		}
	}
	
	
}
