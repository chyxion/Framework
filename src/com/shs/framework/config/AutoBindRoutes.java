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
    protected final Logger logger = Logger.getLogger(AutoBindRoutes.class);
    // 不包括的类
    private List<Class<? extends BaseController>> excludeClasses = new LinkedList<Class<? extends BaseController>>();
    // 路由Jav包
    private List<String> routeJARs = new LinkedList<String>();
    // 扫描所有JAR包
    private boolean scanAllJARs = false;
    // 控制器后缀
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
    	// 扫描所有的控制器
        List<Class<? extends BaseController>> controllers = 
        	ClassSearcher.findInClasspathAndJars(BaseController.class, routeJARs);
        // 扫描全部JAR包
        if (scanAllJARs) 
        	controllers.addAll(ClassSearcher.findAllJARs(BaseController.class));
        // 路由映射
        RouteMapping routeMapping = null;
        // 遍历全部控制器
        for (Class controller : controllers) {
        	// 不包含，跳过
            if (excludeClasses.contains(controller)) {
                continue;
            }
            // 控制器路由
            routeMapping = (RouteMapping) controller.getAnnotation(RouteMapping.class);
            String route;
            // 没有配置路由，使用默认
            if (routeMapping == null) {
            	route = controllerKey(controller);
                this.add(route, controller);
                logger.debug("使用默认路由: routes.add(" + route + ", " + controller.getName() + ")");
            } else {
            	route = routeMapping.controller();
            	if (!StringUtils.isEmpty(route)) {
	                this.add(route, controller, routeMapping.view());
	                logger.debug("使用配置路由: routes.add(" + route + ", " + controller.getName() + "," + routeMapping.view() + ")");
            	} else {
	                logger.error("控制器[" + controller + "]的路由配置为空！");
            	}
            }
        }
    }
    /**
     * 获取控制器Key，如果以Controller结尾，截取前半部分名字
     * @param clazz
     * @return
     */
    private String controllerKey(Class<BaseController> clazz) {
    	String controllerKey = "/" + StringUtils.uncapitalize(clazz.getSimpleName());
    	// Controller结尾
    	if (controllerKey.endsWith(suffix)) {
	        controllerKey = controllerKey.substring(0, controllerKey.indexOf(suffix));
    	} 
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
