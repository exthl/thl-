package com.thl.core.log;

public class LoggerFactory {

	public static Logger getLogger(Class<?> source){
		return new MyLogger(source);
	}
	
}
