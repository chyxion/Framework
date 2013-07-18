package com.shs.framework.exceptions;
/**
 * @version 0.1
 * @author chyxion
 * @describe: 验证异常
 * @date created: Apr 13, 2013 11:34:13 AM
 * @support: chyxion@163.com
 * @date modified: 
 * @modified by: 
 */
public class ValidateException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public ValidateException(String msg) {
		super(msg);
	}
	public ValidateException(Throwable e) {
		super(e);
	}
	public ValidateException(String msg, Throwable e) {
		super(msg, e);
	}
}
