package com.thl.web.aop;

import java.lang.reflect.Method;

import com.thl.core.log.Logger;
import com.thl.core.log.LoggerFactory;
import com.thl.web.aop.proxy.Proxy;
import com.thl.web.aop.proxy.ProxyChain;

public abstract class AspectProxy implements Proxy {

	private static final Logger LOGGER = LoggerFactory.getLogger(AspectProxy.class);

	@Override
	public Object doProxy(ProxyChain proxyChain) throws Throwable {
		Object result = null;
		
		Class<?> targetClass = proxyChain.getTargetClass();
		Method targetObject = proxyChain.getTargetMethod();
		Object[] methodParams = proxyChain.getMethodParams();
		
		begin();
		try {
			if (intercept(targetClass, targetObject, methodParams)) {
				before(targetClass, targetObject, methodParams);
				result = proxyChain.doProxyChain();
				after(targetClass, targetObject, methodParams);
			} else {
				result = proxyChain.doProxyChain();
			}
		} catch (Exception e) {
			LOGGER.error("create proxy failure", e);
			error(targetClass, targetObject, methodParams, e);
		} finally {
			end();
		}
		
		return result;
	}

	
	 //  通过继承 AspectProxy 并 复写 以下方法  （将增强的代码写到对应的方法中）来实现增强
	
	
	// 判断是否拦截
	public boolean intercept(Class<?> targetClass, Method targetObject, Object[] methodParams) throws Throwable {
		return true;
	}
	// 前置增强  与目标类 无关
	public void begin() {
		
	}
	// 前置增强  与目标类 有关
	public void before(Class<?> targetClass, Method targetObject, Object[] methodParams) throws Throwable {
		
	}
	// 后置增强 与目标类 无关
	public void end() {
		
	}
	// 后置增强 与目标类 有关
	public void after(Class<?> targetClass, Method targetObject, Object[] methodParams) throws Throwable {
		
	}
	// 抛出增强
	public void error(Class<?> targetClass, Method targetObject, Object[] methodParams, Throwable e) {
		
	}
}



