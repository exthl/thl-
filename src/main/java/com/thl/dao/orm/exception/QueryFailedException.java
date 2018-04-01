package com.thl.dao.orm.exception;

public class QueryFailedException extends Exception {
	private static final long serialVersionUID = 1L;

	public QueryFailedException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public QueryFailedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public QueryFailedException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public QueryFailedException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public QueryFailedException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
