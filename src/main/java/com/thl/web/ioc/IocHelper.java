package com.thl.web.ioc;

import java.lang.reflect.Field;
import java.util.Map;

import com.thl.core.util.ArrayUtil;
import com.thl.core.util.CollectionUtil;
import com.thl.core.util.ReflectionUtil;

public class IocHelper {
	// ʵ�ֿ��Ʒ�ת��IOC����Ҳ����Ϊ������ע�루DI��
	
	// �����б���ע�� Inject ���ֶ�ע��ʵ��
	
	static {
		// ��ȡ Bean ����
		Map<Class<?>, Object> beanMap = BeanHelper.getBeanMap();
		if (CollectionUtil.isNotEmpty(beanMap)) {
			// �������ҳ�����Inject ע����ֶΣ���ע��ʵ��
			for (Map.Entry<Class<?>, Object> entry : beanMap.entrySet()) {
				Class<?> beanClass = entry.getKey();
				Object beanInstance = entry.getValue();
				Field[] beanFields = beanClass.getDeclaredFields();
				if (ArrayUtil.isNotEmpty(beanFields)) {
					for (Field beanField : beanFields) {
						// �ҵ� Ҫ���ֶ�
						if (beanField.isAnnotationPresent(Inject.class)) {
							// �� bean map �л�ȡʵ��
							Class<?> beanFieldClass = beanField.getType();
							Object beanFieldInstance = BeanHelper.getBean(beanFieldClass);
							// ע�����
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
