package com.shs.framework.config;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import com.shs.framework.core.Constant;
import com.shs.framework.dao.IEventHandler;
import com.shs.framework.renderers.IMainRendererFactory;
import com.shs.framework.renderers.Renderer;
import com.shs.framework.renderers.RendererFactory;
import com.shs.framework.renderers.ViewType;
import com.shs.framework.utils.PathUtils;

final public class Constants {
	
	private String error404View;
	private String error500View;
	private String fileRenderPath;
	private String dirUpload;
	private boolean devMode;
	private String encoding = Constant.DEFAULT_ENCODING;
	private String urlParamSeparator = Constant.DEFAULT_URL_PARA_SEPARATOR;
	private ViewType viewType = Constant.DEFAULT_VIEW_TYPE;
	private String jspViewExtension = Constant.DEFAULT_JSP_EXTENSION;
	private String freeMarkerViewExtension = Constant.DEFAULT_FREE_MARKER_EXTENSION;
	private Integer maxPostSize = Constant.DEFAULT_MAX_POST_SIZE;
	private int freeMarkerTemplateUpdateDelay = Constant.DEFAULT_FREEMARKER_TEMPLATE_UPDATE_DELAY;	// just for not devMode
	// Ajax请求标识名
	private String ajaxParam = "__ajax";
	/**
	 * WEB-INF/config/config.json的自动加载
	 */
	private JSONObject joConfig;
	/**
	 * 路由jar，如果控制器在jar包中，添加jar包名到该list中，方能自动绑定
	 */
	private List<String> routeJARs = new LinkedList<String>();
	/**
	 * 数据库访问层BaseDAO的事件类
	 */
	private Class<? extends IEventHandler> daoEventClass;
	/**
	 * 数据库访问层BaseDAO的事件类
	 */
	public Class<? extends IEventHandler> getDAOEventClass() {
		return daoEventClass;
	}
	/**
	 * 数据库访问层BaseDAO的事件类
	 */
	public void setDAOEventClass(Class<? extends IEventHandler> daoEventClass) {
		this.daoEventClass = daoEventClass;
	}
	/**
	 * WEB-INF/config/config.json的自动加载JSONObject对象
	 */
	public JSONObject getJOConfig() {
		return joConfig;
	}
	/**
	 * 路由jar，如果控制器在jar包中，调用此方法添加jar包，方能自动绑定
	 */
	public Constants addRouteJAR(String jar) {
		routeJARs.add(jar);
		return this;
	}
	public List<String> getRouteJARs() {
		return routeJARs;
	}
	public void setJOConfig(JSONObject joConfig) {
		this.joConfig = joConfig;
	}

	public String getAjaxParam() {
		return ajaxParam;
	}
	/**
	 * 设置Ajax请求标志，响应数据自动为JSON格式
	 * @param ajaxParam
	 */
	public void setAjaxParam(String ajaxParam) {
		this.ajaxParam = ajaxParam;
	}
	/**
	 * 排除非过滤模式, 正则表达式
	 */
	private List<String> excludeRequestPatterns = new LinkedList<String>();
	/**
	 * 添加不包含的请求模式，比如，
	 *	constants.addExcludeReqestPattern("doNotHandleThis/.*");
	 *  这样所有以doNotHandleThis开头的请求都不受到框架过滤
	 * @param p
	 */
	public void addExcludeReqestPattern(String p) {
		excludeRequestPatterns.add(p);
	}
	public List<String> getExcludeReqestParrterns() {
		return excludeRequestPatterns;
	}
	
	/**
	 * Set development mode.
	 * @param devMode the development mode
	 */
	public void setDevMode(boolean devMode) {
		this.devMode = devMode;
	}
	
	/**
	 * Set encoding. The default encoding is UTF-8.
	 * @param encoding the encoding
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public String getEncoding() {
		return encoding;
	}
	
	public boolean getDevMode() {
		return devMode;
	}
	
	public String getUrlParamSeparator() {
		return urlParamSeparator;
	}
	
	public ViewType getViewType() {
		return viewType;
	}
	
	/**
	 * Set view type. The default value is ViewType.FREE_MARKER
	 * Controller.render(String view) will use the view type to render the view.
	 * @param viewType the view type 
	 */
	public void setViewType(ViewType viewType) {
		if (viewType == null)
			throw new IllegalArgumentException("viewType can not be null");
		
		if (viewType != ViewType.OTHER)	// setMainRenderFactory will set ViewType.OTHER
			this.viewType = viewType;
	}
	
	/**
	 * Set urlPara separator. The default value is "-"
	 * @param urlParaSeparator the urlPara separator
	 */
	public void setUrlParaSeparator(String urlParaSeparator) {
		if (StringUtils.isEmpty(urlParaSeparator) || urlParaSeparator.contains("/"))
			throw new IllegalArgumentException("urlParaSepartor can not be blank and can not contains \"/\"");
		this.urlParamSeparator = urlParaSeparator;
	}
	
	public String getJSPViewExtension() {
		return jspViewExtension;
	}
	
	/**
	 * Set Jsp view extension. The default value is ".jsp"
	 * @param jspViewExtension the Jsp view extension
	 */
	public void setJSPViewExtension(String jspViewExtension) {
		this.jspViewExtension = jspViewExtension.startsWith(".") ? jspViewExtension : "." + jspViewExtension;
	}
	
	public String getFreeMarkerViewExtension() {
		return freeMarkerViewExtension;
	}
	
	/**
	 * Set FreeMarker view extension. The default value is ".html" not ".ftl"
	 * @param freeMarkerViewExtension the FreeMarker view extension
	 */
	public void setFreeMarkerViewExtension(String freeMarkerViewExtension) {
		this.freeMarkerViewExtension = freeMarkerViewExtension.startsWith(".") ? freeMarkerViewExtension : "." + freeMarkerViewExtension;
	}
	
	
	public String getError404View() {
		return error404View;
	}
	
	/**
	 * Set error 404 view.
	 * @param error404View the error 404 view
	 */
	public void setError404View(String error404View) {
		this.error404View = error404View;
	}
	
	public String getError500View() {
		return error500View;
	}
	
	/**
	 * Set error 500 view.
	 * @param error500View the error 500 view
	 */
	public void setError500View(String error500View) {
		this.error500View = error500View;
	}
	
	public String getFileRenderPath() {
		return fileRenderPath;
	}
	
	/**
	 * Set the path of file render of controller.
	 * <p>
	 * The path is start with root path of this web application.
	 * The default value is "/download" if you do not config this parameter.
	 */
	public void setFileRenderPath(String fileRenderPath) {
		if (StringUtils.isEmpty(fileRenderPath))
			throw new IllegalArgumentException("The argument fileRenderPath can not be blank");
		
		if (!fileRenderPath.startsWith("/") && !fileRenderPath.startsWith(File.separator))
			fileRenderPath = File.separator + fileRenderPath;
		this.fileRenderPath = PathUtils.getWebRootPath() + fileRenderPath;
	}
	
	/**
	 * Set the save directory for upload file. You can use PathUtil.getWebRootPath()
	 * to get the web root path of this application, then create a path based on
	 * web root path conveniently.
	 */
	public void setUploadedFileSaveDirectory(String dir) {
		if (StringUtils.isEmpty(dir))
			throw new IllegalArgumentException("uploadedFileSaveDirectory can not be blank");
		
		if (dir.endsWith("/") || dir.endsWith("\\"))
			this.dirUpload = dir;
		else
			this.dirUpload = dir + File.separator;
	}
	
	public String getDirUpload() {
		return dirUpload;
	}
	
	public Integer getMaxPostSize() {
		return maxPostSize;
	}
	
	/**
	 * Set max size of http post. The upload file size depend on this value.
	 */
	public void setMaxPostSize(Integer maxPostSize) {
		if (maxPostSize != null && maxPostSize > 0) {
			this.maxPostSize = maxPostSize;
		}
	}
	
	// i18n -----
	private String i18nResourceBaseName;
	
	private Locale defaultLocale;
	
	private Integer i18nMaxAgeOfCookie;
	
	public void setI18n(String i18nResourceBaseName, Locale defaultLocale, Integer i18nMaxAgeOfCookie) {
		this.i18nResourceBaseName = i18nResourceBaseName;
		this.defaultLocale = defaultLocale;
		this.i18nMaxAgeOfCookie = i18nMaxAgeOfCookie;
	}
	
	public void setI18n(String i18nResourceBaseName) {
		this.i18nResourceBaseName = i18nResourceBaseName;
	}
	
	public String getI18nResourceBaseName() {
		return i18nResourceBaseName;
	}
	
	public Locale getI18nDefaultLocale() {
		return defaultLocale;
	}
	
	public Integer getI18nMaxAgeOfCookie() {
		return this.i18nMaxAgeOfCookie;
	}
	// -----
	
	/**
	 * FreeMarker template update delay for not devMode.
	 */
	public void setFreeMarkerTemplateUpdateDelay(int delayInSeconds) {
		if (delayInSeconds < 0)
			throw new IllegalArgumentException("template_update_delay must more than -1.");
		this.freeMarkerTemplateUpdateDelay = delayInSeconds;
	}
	
	public int getFreeMarkerTemplateUpdateDelay() {
		return freeMarkerTemplateUpdateDelay;
	}
	
	/**
	 * Set the base path for all views
	 */
	public void setBaseViewPath(String baseViewPath) {
		if (!StringUtils.isBlank(baseViewPath)) {
			if (!baseViewPath.startsWith("/"))			// add prefix "/"
				baseViewPath = "/" + baseViewPath;
			
			if (baseViewPath.endsWith("/"))				// remove "/" in the end of baseViewPath
				baseViewPath = baseViewPath.substring(0, baseViewPath.length() - 1);
			Renderer.BASE_VIEW_PATH = baseViewPath;
		}
	}
	
	/**
	 * Set the mainRenderFactory then your can use your custom render in controller as render(String).
	 */
	public void setMainRenderFactory(IMainRendererFactory mainRenderFactory) {
		if (mainRenderFactory == null)
			throw new IllegalArgumentException("mainRenderFactory can not be null.");
		
		this.viewType = ViewType.OTHER;
		RendererFactory.setMainRenderFactory(mainRenderFactory);
	}
}







