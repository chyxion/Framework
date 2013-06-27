package com.shs.framework.core;
import java.lang.reflect.Method;
import com.shs.framework.aop.IActionInterceptor;
class Action {
	private final Class<? extends BaseController> controllerClass;
	private final String controllerKey;
	private final String actionKey;
	private final Method method;
	private final String methodName;
	private final IActionInterceptor[] interceptors;
	private final String viewPath;
	
	public Action(String controllerKey, String actionKey, Class<? extends BaseController> controllerClass, Method method, String methodName, IActionInterceptor[] interceptors, String viewPath) {
		this.controllerKey = controllerKey;
		this.actionKey = actionKey;
		this.controllerClass = controllerClass;
		this.method = method;
		this.methodName = methodName;
		this.interceptors = interceptors;
		this.viewPath = viewPath;
	}
	
	public Class<? extends BaseController> getControllerClass() {
		return controllerClass;
	}
	
	public String getControllerKey() {
		return controllerKey;
	}
	
	public String getActionKey() {
		return actionKey;
	}
	
	public Method getMethod() {
		return method;
	}
	
	public IActionInterceptor[] getInterceptors() {
		return interceptors;
	}
	
	public String getViewPath() {
		return viewPath;
	}
	
	public String getMethodName() {
		return methodName;
	}
}









