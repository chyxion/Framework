package com.shs.framework.aop;
import java.lang.reflect.Method;

import com.shs.framework.core.ActionInvocation;
import com.shs.framework.core.BaseController;

/**
 * ActionInvocationWrapper invoke the InterceptorStack.
 */
class ActionInvocationWrapper extends ActionInvocation {
	
	private IActionInterceptor[] inters;
	private ActionInvocation actionInvocation;
	private int index = 0;
	
	ActionInvocationWrapper(ActionInvocation actionInvocation, IActionInterceptor[] inters) {
		this.actionInvocation = actionInvocation;
		this.inters = inters;
	}
	
	/**
	 * Invoke the action
	 */
	@Override
	public final void invoke() {
		if (index < inters.length)
			inters[index++].intercept(this);
		else
			actionInvocation.invoke();
	}
	
	@Override
	public BaseController getController() {
		return actionInvocation.getController();
	}
	
	@Override
	public String getActionKey() {
		return actionInvocation.getActionKey();
	}
	
	@Override
	public String getControllerKey() {
		return actionInvocation.getControllerKey();
	}
	
	@Override
	public Method getMethod() {
		return actionInvocation.getMethod();
	}
	
	@Override
	public String getMethodName() {
		return actionInvocation.getMethodName();
	}
	
	/**
	 * Return view path of this controller
	 */
	@Override
	public String getViewPath() {
		return actionInvocation.getViewPath();
	}
	
	/*
	 * It should be added method below when com.jfinal.core.ActionInvocation add method, otherwise null will be returned.
	 */
}







