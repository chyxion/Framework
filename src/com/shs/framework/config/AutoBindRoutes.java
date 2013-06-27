package com.shs.framework.config;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.shs.framework.aop.RouteMapping;
import com.shs.framework.core.BaseController;
import com.shs.framework.utils.ClassSearcher;

/**
 * @version 0.1
 * @author chyxion
 * @describe: 
 * @date created: Apr 15, 2013 10:17:37 AM
 * @support: chyxion@163.com
 * @date modified: 
 * @modified by: 
 */

public class AutoBindRoutes extends Routes {

    protected final Logger logger = Logger.getLogger(getClass());

    private List<Class<? extends BaseController>> excludeClasses = new LinkedList<Class<? extends BaseController>>();

    private List<String> routeJARs = new LinkedList<String>();

    private boolean autoScan = true;
    private boolean scanAllJARs = false;
    
    private String suffix = "Controller";

    public AutoBindRoutes addJAR(String jarName) {
        if (!StringUtils.isEmpty(jarName)) {
            routeJARs.add(jarName);
        }
        return this;
    }

    public AutoBindRoutes addJARs(String jarNames) {
        if (!StringUtils.isEmpty(jarNames)) {
            addJARs(jarNames.split(","));
        }
        return this;
    }

    public AutoBindRoutes addJARs(String[] jarsName) {
        routeJARs.addAll(Arrays.asList(jarsName));
        return this;
    }

    public AutoBindRoutes addJARs(List<String> jarsName) {
        routeJARs.addAll(jarsName);
        return this;
    }

    public AutoBindRoutes addExcludeClass(Class<? extends BaseController> clazz) {
        if (clazz != null) {
            excludeClasses.add(clazz);
        }
        return this;
    }

    public AutoBindRoutes addExcludeClasses(Class<? extends BaseController>[] clazzes) {
        excludeClasses.addAll(Arrays.asList(clazzes));
        return this;
    }

    public AutoBindRoutes addExcludeClasses(List<Class<? extends BaseController>> clazzes) {
        excludeClasses.addAll(clazzes);
        return this;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public void config() {
        List<Class<? extends BaseController>> controllers = 
        	ClassSearcher.findInClasspathAndJars(BaseController.class, routeJARs);
        // 扫描全部JAR包
        if (scanAllJARs) controllers.addAll(ClassSearcher.findAllJARs(BaseController.class));
        RouteMapping routePath = null;
        for (Class controller : controllers) {
            if (excludeClasses.contains(controller)) {
                continue;
            }
            routePath = (RouteMapping) controller.getAnnotation(RouteMapping.class);
            if (routePath == null) {
                if (!autoScan) {
                    continue;
                }
                this.add(controllerKey(controller), controller);
                logger.debug("user default class route!");
                logger.debug("routes.add(" + controllerKey(controller) + ", " + controller.getName() + ")");
            } else {
            	String controllerPath = routePath.controller();
            	if (!StringUtils.isEmpty(controllerPath)) {
	                this.add(controllerPath, controller, routePath.view());
	                logger.debug("routes.add(" + controllerPath + ", " + controller + ","
	                        + routePath.view() + ")");
            	}
            }
        }
    }

    private String controllerKey(Class<BaseController> clazz) {
    	if (!clazz.getSimpleName().endsWith(suffix)) {
    		throw new RuntimeException(clazz.getSimpleName() + " does not has a RoutePathBind annotation and it's name is not end with " + suffix);
    	}
        		
        String controllerKey = "/" + StringUtils.uncapitalize(clazz.getSimpleName());
        controllerKey = controllerKey.substring(0, controllerKey.indexOf("Controller"));
        return controllerKey;
    }

    public List<Class<? extends BaseController>> getExcludeClasses() {
        return excludeClasses;
    }

    public void setExcludeClasses(List<Class<? extends BaseController>> excludeClasses) {
        this.excludeClasses = excludeClasses;
    }

    public List<String> getRouteJARs() {
        return routeJARs;
    }
    public void setRouteJARs(List<String> includeJars) {
        this.routeJARs = includeJars;
    }

    public void setAutoScan(boolean autoScan) {
        this.autoScan = autoScan;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

	public void setScanAllJARs(boolean scanAllJARs) {
		this.scanAllJARs = scanAllJARs;
	}

	public boolean isScanAllJARs() {
		return scanAllJARs;
	}


}
