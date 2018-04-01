package com.thl.dao.tx;

import java.lang.reflect.Method;

import com.thl.core.log.Logger;
import com.thl.core.log.LoggerFactory;
import com.thl.dao.datasouce.DatabaseHelper;
import com.thl.web.aop.proxy.Proxy;
import com.thl.web.aop.proxy.ProxyChain;

public class TransactionProxy implements Proxy {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionProxy.class);

	private static final ThreadLocal<Boolean> FLAG_HOLDER = new ThreadLocal<Boolean>() {
		@Override
		public Boolean initialValue() {
			return false;
		}
	};

	@Override
	public Object doProxy(ProxyChain proxyChain) throws Throwable {
		Object result = null;
		boolean flag = FLAG_HOLDER.get();
		Method method = proxyChain.getTargetMethod();
		if (!flag && method.isAnnotationPresent(Transaction.class)) {
			FLAG_HOLDER.set(true);
			try {
				DatabaseHelper.beginTransaction();
				LOGGER.log("begin transaction");
				result = proxyChain.doProxyChain();
				DatabaseHelper.commitTransaction();
				LOGGER.log("commit transaction");
			} catch (Exception e) {
				DatabaseHelper.rollbackTransaction();
				LOGGER.log("rollback transaction");
				throw e;
			} finally {
				FLAG_HOLDER.remove();
			}
		} else {
			result = proxyChain.doProxyChain();
		}
		return result;
	}

}
