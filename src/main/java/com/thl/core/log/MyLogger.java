package com.thl.core.log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyLogger implements Logger {

	private String source;
	private String baseProjectName = "com.thl";
	
	private String message;
	
	
	protected MyLogger(Class<?> source) {
		this.source = source.getName();
	}

	private void handle(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String dateStr = formatter.format(date);
		this.message = "From [" + this.baseProjectName + "]"
				+ " In \"" + source + "\""
				+ " At " + dateStr 
				+ "  : \n\t";
	}
	
	@Override
	public void log(String text) {
		handle(new Date());
		System.out.println("Message " + message + text);
	}

	@Override
	public void error(String text) {
		error(text, null, false, new Date());
	}

	@Override
	public void error(String text, Throwable e) {
		error(text, e, false, new Date());
	}

	@Override
	public void error(String text, Throwable e, boolean isThrows) {
		error(text, e, isThrows, new Date());
	}

	@Override
	public void error(String text, Throwable e, boolean isThrows, Date date) {
		handle(date);
		System.err.println("Error " + message + text);
		if (e != null) {
			e.printStackTrace();
			if (isThrows) {
				throw new RuntimeException(e);
			}
		}
	}
	
}
