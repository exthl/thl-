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

	
	 //  ͨ���̳� AspectProxy �� ��д ���·���  ������ǿ�Ĵ���д����Ӧ�ķ����У���ʵ����ǿ
	
	
	// �ж��Ƿ�����
	public boolean intercept(Class<?> targetClass, Method targetObject, Object[] methodParams) throws Throwable {
		return true;
	}
	// ǰ����ǿ  ��Ŀ���� �޹�
	public void begin() {
		
	}
	// ǰ����ǿ  ��Ŀ���� �й�
	public void before(Class<?> targetClass, Method targetObject, Object[] methodParams) throws Throwable {
		
	}
	// ������ǿ ��Ŀ���� �޹�
	public void end() {
		
	}
	// ������ǿ ��Ŀ���� �й�
	public void after(Class<?> targetClass, Method targetObject, Object[] methodParams) throws Throwable {
		
	}
	// �׳���ǿ
	public void error(Class<?> targetClass, Method targetObject, Object[] methodParams, Throwable e) {
		
	}
}



