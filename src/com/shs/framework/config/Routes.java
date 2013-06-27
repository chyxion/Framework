package com.shs.framework.config;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import com.shs.framework.core.BaseController;

/**
 * Routes.
 */
public abstract class Routes {
	
	private final Map<String, Class<? extends BaseController>> map = new HashMap<String, Class<? extends BaseController>>();
	private final Map<String, String> viewPathMap = new HashMap<String, String>();
	
	/**
	 * you must implement config method and use add method to config route
	 */
	public abstract void config();
	
	public Routes add(Routes routes) {
		if (routes != null) {
			routes.config();	// very important!!!
			map.putAll(routes.map);
			viewPathMap.putAll(routes.viewPathMap);
		}
		return this;
	}
	
	/**
	 * Add route
	 * @param controllerKey A key can find controller
	 * @param controllerClass Controller Class
	 * @param viewPath View path for this Controller
	 */
	public Routes add(String controllerKey, Class<? extends BaseController> controllerClass, String viewPath) {
		if (controllerKey == null)
			throw new IllegalArgumentException("The controllerKey can not be null");
		controllerKey = controllerKey.trim();
		if ("".equals(controllerKey))
			throw new IllegalArgumentException("The controllerKey can not be blank");
		if (controllerClass == null)
			throw new IllegalArgumentException("The controllerClass can not be null");
		if (map.containsKey(controllerKey))
			throw new IllegalArgumentException("The controllerKey already exists");
		
		if (!controllerKey.startsWith("/"))
			controllerKey = "/" + controllerKey;
		map.put(controllerKey, controllerClass);
		
		if (StringUtils.isEmpty(viewPath))	// view path is controllerKey by default
			viewPath = controllerKey;
		
		viewPath = viewPath.trim();
		// 默认view路径为controller路径
		if (StringUtils.isBlank(viewPath)) {
			viewPath = controllerKey + "/";
		} else {
			if (!viewPath.startsWith("/"))					// "/" added to prefix
				viewPath = "/" + viewPath;
			
			if (!viewPath.endsWith("/"))					// "/" added to postfix
				viewPath = viewPath + "/";
		}
		
		viewPathMap.put(controllerKey, viewPath);
		return this;
	}
	
	/**
	 * Add url mapping to controller. The view path is controllerKey
	 * @param controllerkey A key can find controller
	 * @param controllerClass Controller Class
	 */
	public Routes add(String controllerkey, Class<? extends BaseController> controllerClass) {
		return add(controllerkey, controllerClass, controllerkey);
	}
	
	public Set<Entry<String, Class<? extends BaseController>>> getEntrySet() {
		return map.entrySet();
	}
	
	public String getViewPath(String key) {
		return viewPathMap.get(key);
	}
}






