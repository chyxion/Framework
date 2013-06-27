package com.shs.framework.aop;

import com.shs.framework.core.ActionInvocation;

/**
 * PrototypeInterceptor.
 */
public abstract class PrototypeInterceptor implements IActionInterceptor {
	
	final public void intercept(ActionInvocation ai) {
		try {
			getClass().newInstance().doIntercept(ai);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	abstract public void doIntercept(ActionInvocation ai);
}
