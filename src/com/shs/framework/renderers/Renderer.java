package com.shs.framework.renderers;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * @version 0.1
 * @author chyxion
 * @describe: 渲染器
 * @date created: Mar 3, 2013 5:03:07 PM
 * @support: chyxion@163.com
 * @date modified: 
 * @modified by: 
 * @copyright: Shenghang Soft All Right Reserved.
 */
public abstract class Renderer implements Serializable {
	
	private static final long serialVersionUID = 1L;
	protected String view;
	protected String actionPath; 
	public transient static String BASE_VIEW_PATH = "/WEB-INF/views";
	protected transient HttpServletRequest request;
	protected transient HttpServletResponse response;
	
	protected transient static String encoding = "utf-8";
	private transient static boolean devMode;
	
	static final void init(String encoding, boolean devMode) {
		Renderer.encoding = encoding;
		Renderer.devMode = devMode;
	}
	
	public static final String getEncoding() {
		return encoding;
	}
	
	public static final boolean getDevMode() {
		return devMode;
	}
	
	public final Renderer setContext(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		return this;
	}
	
	public final Renderer setContext(HttpServletRequest request, HttpServletResponse response, String viewPath) {
		this.request = request;
		this.response = response;
		this.actionPath = viewPath;
		if (view != null && !view.startsWith("/"))
			view = viewPath + view;
		return this;
	}
	
	/**
	 * Render to client
	 */
	public abstract void render();
}
