package com.thl.web.aop;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.thl.core.log.Logger;
import com.thl.core.log.LoggerFactory;
import com.thl.dao.tx.TransactionProxy;
import com.thl.web.aop.proxy.Proxy;
import com.thl.web.aop.proxy.ProxyManager;
import com.thl.web.core.ClassHelper;
import com.thl.web.ioc.BeanHelper;

public class AopHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(AopHelper.class);

	static {
		try {
			Map<Class<?>, Set<Class<?>>> proxyMap = createProxyMap();
			Map<Class<?>, List<Proxy>> targetMap = createTargetMap(proxyMap);
			for (Map.Entry<Class<?>, List<Proxy>> entry : targetMap.entrySet()) {
				Class<?> targetClass = entry.getKey();
				List<Proxy> proxyList = entry.getValue();
				Class<?> proxyClass = targetClass;
				if (targetClass.isInterface()) {
					proxyClass = BeanHelper.getBean(targetClass).getClass();
				}
				// �����������
				Object obj = ProxyManager.createProxy(proxyClass, proxyList);
				// ��������� ��ӵ� beanMap ��
				BeanHelper.setBean(targetClass, obj);
			}
		} catch (Exception e) {
			LOGGER.error("aop failure", e);
		}
	}

	// ����һ����������Ҫ�����Ŀ����ļ���
	public static Set<Class<?>> createTargetClassSet(Aspect aspect) throws Exception {
		Set<Class<?>> targetClassSet = new HashSet<>();

		Class<? extends Annotation> targetClass = aspect.value();
		if (targetClass != null && !Aspect.class.equals(targetClass)) {
			targetClassSet.addAll(ClassHelper.getClassSetByAnnotation(targetClass));
		}

		return targetClassSet;
	}

	// ���� ������ �� ��Ŀ���� �Ĺ�ϵӳ��
	public static Map<Class<?>, Set<Class<?>>> createProxyMap() throws Exception {
		Map<Class<?>, Set<Class<?>>> proxyMap = new HashMap<>();
		proxyMap.putAll(createDefaultProxyMap());
		proxyMap.putAll(createTransactionProxyMap());
		return proxyMap;
	}

	private static Map<Class<?>, Set<Class<?>>> createTransactionProxyMap() throws Exception {
		Map<Class<?>, Set<Class<?>>> proxyMap = new HashMap<>();
		Set<Class<?>> serviceSet = ClassHelper.getServices();
		proxyMap.put(TransactionProxy.class, serviceSet);
		return proxyMap;
	}

	public static Map<Class<?>, Set<Class<?>>> createDefaultProxyMap() throws Exception {
		Map<Class<?>, Set<Class<?>>> proxyMap = new HashMap<>();
		// ȡ����������
		Set<Class<?>> proxyClassSet = ClassHelper.getClassSetBySupper(AspectProxy.class);
		for (Class<?> proxyClass : proxyClassSet) {
			if (proxyClass.isAnnotationPresent(Aspect.class)) {
				Aspect aspect = proxyClass.getDeclaredAnnotation(Aspect.class);
				// ȡ��Ŀ���༯��
				Set<Class<?>> targetClassSet = createTargetClassSet(aspect);
				// ��ӵ� map ��
				proxyMap.put(proxyClass, targetClassSet);
			}
		}
		return proxyMap;
	}

	// ���� Ŀ���� �� ������� �Ĺ�ϵӳ��
	public static Map<Class<?>, List<Proxy>> createTargetMap(Map<Class<?>, Set<Class<?>>> proxyMap) throws Exception {
		Map<Class<?>, List<Proxy>> targetMap = new HashMap<>();
		for (Map.Entry<Class<?>, Set<Class<?>>> entry : proxyMap.entrySet()) {
			Class<?> proxyClass = entry.getKey();
			Set<Class<?>> targetClassSet = entry.getValue();
			for (Class<?> targetClass : targetClassSet) {
				// ��������
				Proxy proxy = (Proxy) proxyClass.newInstance();
				if (targetMap.containsKey(targetClass)) {
					targetMap.get(targetClass).add(proxy);
				} else {
					List<Proxy> proxyList = new ArrayList<>();
					proxyList.add(proxy);
					targetMap.put(targetClass, proxyList);
				}
			}
		}
		return targetMap;
	}

}
