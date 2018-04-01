package com.thl.web.aop.proxy;

public interface Proxy {

	public Object doProxy(ProxyChain proxyChain) throws Throwable;
	
}
