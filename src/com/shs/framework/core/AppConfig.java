package com.shs.framework.core;
import java.util.List;
import org.apache.log4j.Logger;

import com.shs.framework.config.AutoBindRoutes;
import com.shs.framework.config.Constants;
import com.shs.framework.config.Handlers;
import com.shs.framework.config.Interceptors;
import com.shs.framework.config.AbstractConfig;
import com.shs.framework.config.Plugins;
import com.shs.framework.config.Routes;
import com.shs.framework.dao.ConnectionManager;
import com.shs.framework.plugins.IPlugin;
import com.shs.framework.plugins.druid.DruidPlugin;

public class AppConfig {
	
	private static final Constants constants = new Constants();
	private static final Routes routes = new Routes() {@Override public void config() {}};
	private static final Plugins plugins = new Plugins();
	private static final Interceptors interceptors = new Interceptors();
	private static final Handlers handlers = new Handlers();
	private static Logger logger = Logger.getLogger(AppConfig.class);
	
	// prevent new Config();
	private AppConfig() {
	}
	
	/*
	 * Config order: constant, route, plugin, interceptor, handler
	 */
	public static void config(AbstractConfig config) {
		// 配置常量
		config.configConstant(constants);				
	
		AutoBindRoutes ar = new AutoBindRoutes();
		ar.addJARs(constants.getRouteJARs());
		// 自动绑定路由
		routes.add(ar);
		// 数据库连接池
		DruidPlugin dp = 
				new DruidPlugin(ConnectionManager.URL, 
				ConnectionManager.USER_NAME, 
				ConnectionManager.PASSWORD, 
				ConnectionManager.DRIVER);
		ConnectionManager.setDataSourceProvider(dp);
		plugins.add(dp);
		config.configPlugin(plugins);					
		// 启动插件
		startPlugins();	
		config.configInterceptor(interceptors);
		config.configHandler(handlers);
	}
	
	public static final Constants getConstants() {
		return constants;
	}
	
	public static final Routes getRoutes() {
		return routes;
	}
	
	public static final Plugins getPlugins() {
		return plugins;
	}
	
	public static final Interceptors getInterceptors() {
		return interceptors;
	}
	
	public static Handlers getHandlers() {
		return handlers;
	}
	
	private static void startPlugins() {
		List<IPlugin> pluginList = plugins.getPluginList();
		if (pluginList != null) {
			for (IPlugin plugin : pluginList) {
				try {
					if (!plugin.start()) {
						String message = "Plugin start error: " + plugin.getClass().getName();
						logger.error(message);
						throw new RuntimeException(message);
					}
				}
				catch (Exception e) {
					String message = "Plugin start error: " + plugin.getClass().getName() + ". \n" + e.getMessage();
					logger.error(message, e);
					throw new RuntimeException(message, e);
				}
			}
		}
	}
}
