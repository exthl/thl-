package com.thl.web.aop.proxy;

import java.lang.reflect.Method;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class ProxyManager {

	@SuppressWarnings("unchecked")
	public static <T> T createProxy(final Class<T> targetClass, final List<Proxy> proxyList) {
		// ͨ�� CGLib �����������
		return (T) Enhancer.create(targetClass, new MethodInterceptor() {
			@Override
			public Object intercept(Object targetObject, Method targetMethod, Object[] methodParams,
					MethodProxy methodProxy) throws Throwable {
				return new ProxyChain(targetClass, targetObject, targetMethod, methodProxy, methodParams, proxyList) //
						.doProxyChain();
			}

		});
	}

}
