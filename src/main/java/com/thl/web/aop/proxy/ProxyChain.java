package com.thl.web.aop.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.proxy.MethodProxy;

public class ProxyChain {

	private final Class<?> targetClass;
	private final Object targetObject;
	private final Method targetMethod;
	// CGLib �ṩ�Ĵ��� ������
	private final MethodProxy methodProxy;
	private final Object[] methodParams;
	
	private List<Proxy> proxyList = new ArrayList<>();
	private int proxyIndex = 0;
	
	public ProxyChain(Class<?> targetClass, Object targetObject, Method targetMethod, MethodProxy methodProxy,
			Object[] methodParams, List<Proxy> proxyList) {
		super();
		this.targetClass = targetClass;
		this.targetObject = targetObject;
		this.targetMethod = targetMethod;
		this.methodProxy = methodProxy;
		this.methodParams = methodParams;
		this.proxyList = proxyList;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public Method getTargetMethod() {
		return targetMethod;
	}

	public Object[] getMethodParams() {
		return methodParams;
	}
	
	
	public Object doProxyChain() throws Throwable {
		Object result = null;
		
		if (proxyIndex < proxyList.size()) {
			// ִ�������ڵݹ�Ĳ�������
			result = proxyList.get(proxyIndex ++).doProxy(this);
		} else {
			// ��ô������
			result = methodProxy.invokeSuper(targetObject, methodParams);
		}
		
		return result;
	}
	
	
	
}
