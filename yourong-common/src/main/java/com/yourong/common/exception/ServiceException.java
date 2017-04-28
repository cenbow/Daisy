package com.yourong.common.exception;

public class ServiceException extends Exception {
	
	private static final long serialVersionUID = 9193108311452164251L;

	public ServiceException(String c) {
		super(c);
	}

	public ServiceException(String c, Throwable t) {
		super(c, t);
	}

	public ServiceException(Throwable t) {
		super(t);
	}
}
