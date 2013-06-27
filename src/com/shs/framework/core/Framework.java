package com.shs.framework.core;
import java.io.File;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;

import com.shs.framework.config.AbstractConfig;
import com.shs.framework.config.Constants;
import com.shs.framework.handlers.AbstractHandler;
import com.shs.framework.handlers.ExcludeRequestUrlHandler;
import com.shs.framework.i18n.I18N;
import com.shs.framework.plugins.IPlugin;
import com.shs.framework.renderers.RendererFactory;
import com.shs.framework.upload.OreillyCos;
import com.shs.framework.utils.PathUtils;

public final class Framework {
	private Constants constants;
	private ActionMapping actionMapping;
	private AbstractHandler handler;
	private ServletContext servletContext;
	
	AbstractHandler getHandler() {
		return handler;
	}
	
	private static final Framework me = new Framework();
	
	// singleton
	private Framework() {
	}
	
	public static Framework me() {
		return me;
	}
	
	boolean init(AbstractConfig config, ServletContext servletContext) {
		this.servletContext = servletContext;
		
		initPathUtil();
		
		AppConfig.config(config);	// start plugin and init logger factory in this method
		constants = AppConfig.getConstants();
		
		initActionMapping();
		initHandler();
		initRender();
		initOreillyCos();
		initI18n();
		
		return true;
	}
	
	private void initI18n() {
		String i18nResourceBaseName = constants.getI18nResourceBaseName();
		if (i18nResourceBaseName != null) {
			I18N.init(i18nResourceBaseName, constants.getI18nDefaultLocale(), constants.getI18nMaxAgeOfCookie());
		}
	}
	
	private void initHandler() {
		List<AbstractHandler> handlerList = AppConfig.getHandlers().getHandlerList();
		handlerList.add(0, new ExcludeRequestUrlHandler(constants));
		handler = new ActionHandler(actionMapping, constants);
		for (int i = handlerList.size() - 1; i >= 0; --i) {
			AbstractHandler h = handlerList.get(i);
			h.setNextHandler(handler);
			handler = h;
		}
	}
	
	private void initOreillyCos() {
		Constants ct = constants;
		if (OreillyCos.isMultipartSupported()) {
			String dirUpload = ct.getDirUpload();
			if (StringUtils.isBlank(dirUpload)) {
				dirUpload = PathUtils.getWebRootPath() + File.separator + "upload" + File.separator;
				ct.setUploadedFileSaveDirectory(dirUpload);
			}
			OreillyCos.init(dirUpload, ct.getMaxPostSize(), ct.getEncoding());
		}
	}
	
	private void initPathUtil() {
		PathUtils.setWebRootPath(servletContext.getRealPath("/"));
	}
	
	private void initRender() {
		RendererFactory.me().init(constants, servletContext);
	}
	
	private void initActionMapping() {
		actionMapping = new ActionMapping(AppConfig.getRoutes(), AppConfig.getInterceptors());
		actionMapping.buildActionMapping();
	}
	
	void stopPlugins() {
		List<IPlugin> plugins = AppConfig.getPlugins().getPluginList();
		if (plugins != null) {
			for (int i=plugins.size()-1; i >= 0; i--) {		// stop plugins
				boolean success = false;
				try {
					success = plugins.get(i).stop();
				} 
				catch (Exception e) {
					success = false;
					e.printStackTrace();
				}
				if (!success) {
					System.err.println("Plugin stop error: " + plugins.get(i).getClass().getName());
				}
			}
		}
	}
	
	public ServletContext getServletContext() {
		return this.servletContext;
	}
	
	public List<String> getAllActionKeys() {
		return actionMapping.getAllActionKeys();
	}
	
	public Constants getConstants() {
		return AppConfig.getConstants();
	}
	
	public Action getAction(String url, String[] urlPara) {
		return actionMapping.getAction(url, urlPara);
	}
}
