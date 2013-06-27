package com.shs.framework.core;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.shs.framework.aop.IActionInterceptor;
import com.shs.framework.config.Interceptors;
import com.shs.framework.config.Routes;

final class ActionMapping {
	
	private static final String SLASH = "/";
	private Routes routes;
	private Interceptors interceptors;
	
	private final Map<String, Action> mapping = new HashMap<String, Action>();
	
	ActionMapping(Routes routes, Interceptors interceptors) {
		this.routes = routes;
		this.interceptors = interceptors;
	}
	
	private Set<String> buildExcludedMethodName() {
		Set<String> excludedMethodName = new HashSet<String>();
		Method[] methods = BaseController.class.getMethods();
		for (Method m : methods) {
			if (m.getParameterTypes().length == 0)
				excludedMethodName.add(m.getName());
		}
		return excludedMethodName;
	}
	
	void buildActionMapping() {
		Set<String> excludedMethodName = buildExcludedMethodName();
		InterceptorBuilder interceptorBuilder = new InterceptorBuilder();
		IActionInterceptor[] defaultInters = interceptors.getInterceptorArray();
		interceptorBuilder.addToInterceptorsMap(defaultInters);
		for (Entry<String, Class<? extends BaseController>> entry : routes.getEntrySet()) {
			Class<? extends BaseController> controllerClass = entry.getValue();
			IActionInterceptor[] controllerInters = interceptorBuilder.buildControllerInterceptors(controllerClass);
			Method[] methods = controllerClass.getMethods();
			for (Method method : methods) {
				String methodName = method.getName();
				if (!excludedMethodName.contains(methodName) && method.getParameterTypes().length == 0) {
					IActionInterceptor[] methodInters = interceptorBuilder.buildMethodInterceptors(method);
					IActionInterceptor[] actionInters = interceptorBuilder.buildActionInterceptors(defaultInters, controllerInters, controllerClass, methodInters, method);
					String controllerKey = entry.getKey();
					
					ActionKey ak = method.getAnnotation(ActionKey.class);
					if (ak != null) {
						String actionKey = ak.value().trim();
						if ("".equals(actionKey))
							throw new IllegalArgumentException(controllerClass.getName() + "." + methodName + "(): The argument of ActionKey can not be blank.");
						
						if (!actionKey.startsWith(SLASH))
							actionKey = SLASH + actionKey;
						
						if (mapping.containsKey(actionKey)) {
							warnning(actionKey, controllerClass, method);
							continue;
						}
						
						Action action = new Action(controllerKey, actionKey, controllerClass, method, methodName, actionInters, routes.getViewPath(controllerKey));
						mapping.put(actionKey, action);
					}
					else if (methodName.equals("index")) {
						String actionKey = controllerKey;
						
						Action action = new Action(controllerKey, actionKey, controllerClass, method, methodName, actionInters, routes.getViewPath(controllerKey));
						action = mapping.put(actionKey, action);
						
						if (action != null) {
							warnning(action.getActionKey(), action.getControllerClass(), action.getMethod());
						}
					}
					else {
						String actionKey = controllerKey.equals(SLASH) ? SLASH + methodName : controllerKey + SLASH + methodName;
						
						if (mapping.containsKey(actionKey)) {
							warnning(actionKey, controllerClass, method);
							continue;
						}
						
						Action action = new Action(controllerKey, actionKey, controllerClass, method, methodName, actionInters, routes.getViewPath(controllerKey));
						mapping.put(actionKey, action);
					}
				}
			}
		}
		
		Action actoin = mapping.get("/");
		if (actoin != null)
			mapping.put("", actoin);
	}
	
	private static final void warnning(String actionKey, Class<? extends BaseController> controllerClass, Method method) {
		StringBuilder sb = new StringBuilder();
		sb.append("--------------------------------------------------------------------------------\nWarnning!!!\n")
		.append("ActionKey already used: \"").append(actionKey).append("\" \n") 
		.append("Action can not be mapped: \"")
		.append(controllerClass.getName()).append(".").append(method.getName()).append("()\" \n")
		.append("--------------------------------------------------------------------------------");
		System.out.println(sb.toString());
	}
	
	/**
	 * Support four types of url
	 * 1: http://abc.com/controllerKey                 ---> 00
	 * 2: http://abc.com/controllerKey/para            ---> 01
	 * 3: http://abc.com/controllerKey/method          ---> 10
	 * 4: http://abc.com/controllerKey/method/para     ---> 11
	 * 5: http://abc.com/controllerKey/method/param/param     ---> 100
	 */
	Action getAction(String url, String[] urlParam) {
		// 忽略最后一个/
		if (url.endsWith("/"))
			url = url.substring(0, url.length() - 1);
		
		Action action = mapping.get(url);
		if (action == null) {
			int i = url.lastIndexOf(SLASH);
			if (i != -1) {
				action = mapping.get(url.substring(0, i));
				urlParam[0] = url.substring(i + 1);
			}
		}
		return action;
	}
	/**
	 * 从后往前匹配action
	 * @param url
	 * @param urlParam
	 * @return
	 */
	List<String> getAllActionKeys() {
		List<String> allActionKeys = new ArrayList<String>(mapping.keySet());
		Collections.sort(allActionKeys);
		return allActionKeys;
	}
}





