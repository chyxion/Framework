package com.shs.framework.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.shs.framework.aop.IActionInterceptor;

/**
 * ActionInvocation invoke the action
 */
public class ActionInvocation {
	
	private BaseController controller;
	private IActionInterceptor[] inters;
	private Action action;
	private int index = 0;
	
	private static final Object[] NULL_ARGS = new Object[0];	// Prevent new Object[0] by jvm for paras of action invocation.
	
	// ActionInvocationWrapper need this constructor
	protected ActionInvocation() {
		
	}
	
	ActionInvocation(Action action, BaseController controller) {
		this.controller = controller;
		this.inters = action.getInterceptors();
		this.action = action;
	}
	
	/**
	 * Invoke the action.
	 */
	public void invoke() {
		if (index < inters.length)
			inters[index++].intercept(this);
		else if (index++ == inters.length)	// index++ ensure invoke action only one time
			try {
				action.getMethod().invoke(controller, NULL_ARGS);
			}
			catch (InvocationTargetException e) {
				throw new RuntimeException(e.getTargetException());
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
	}
	
	/**
	 * Return the controller of this action.
	 */
	public BaseController getController() {
		return controller;
	}
	
	/**
	 * Return the action key.
	 * actionKey = controllerKey + methodName
	 */
	public String getActionKey() {
		return action.getActionKey();
	}
	
	/**
	 * Return the controller key.
	 */
	public String getControllerKey() {
		return action.getControllerKey();
	}
	
	/**
	 * Return the method of this action.
	 * <p>
	 * You can getMethod.getAnnotations() to get annotation on action method to do more things
	 */
	public Method getMethod() {
		return action.getMethod();
	}
	
	/**
	 * Return the method name of this action's method.
	 */
	public String getMethodName() {
		return action.getMethodName();
	}
	
	/**
	 * Return view path of this controller.
	 */
	public String getViewPath() {
		return action.getViewPath();
	}
}
