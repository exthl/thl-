package com.thl.web.aop.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.proxy.MethodProxy;

public class ProxyChain {

	private final Class<?> targetClass;
	private final Object targetObject;
	private final Method targetMethod;
	// CGLib 提供的代理 生成类
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
			// 执行类似于递归的操作，将
			result = proxyList.get(proxyIndex ++).doProxy(this);
		} else {
			// 获得代理对象
			result = methodProxy.invokeSuper(targetObject, methodParams);
		}
		
		return result;
	}
	
	
	
}
