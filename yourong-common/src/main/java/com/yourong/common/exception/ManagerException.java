/**
 * 
 */
package com.yourong.common.exception;

/**
 * 
 * 
 */
public class ManagerException extends Exception {

	private static final long serialVersionUID = 9193108311452164251L;

	public ManagerException(String c) {
		super(c);
	}

	public ManagerException(String c, Throwable t) {
		super(c, t);
	}

	public ManagerException(Throwable t) {
		super(t);
	}
}
