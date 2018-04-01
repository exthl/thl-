package com.thl.core.log;

import java.util.Date;

public interface Logger {

	
	public void log(String text);
	
	
	public void error(String text);
	
	public void error(String text, Throwable e);
	
	public void error(String text, Throwable e, boolean isThrows);

	public void error(String text, Throwable e, boolean isThrows, Date date);
	
	
	
}
