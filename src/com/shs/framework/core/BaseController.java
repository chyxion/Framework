package com.shs.framework.core;
import static com.shs.framework.core.Constant.I18N_LOCALE;
import java.io.File;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import com.shs.framework.i18n.I18N;
import com.shs.framework.renderers.Error404Exception;
import com.shs.framework.renderers.Error500Exception;
import com.shs.framework.renderers.Renderer;
import com.shs.framework.renderers.RendererFactory;

@SuppressWarnings("unchecked")
public abstract class BaseController {
	private static final long serialVersionUID = 1L;
	private Renderer render;
	protected final String CONTENT_TYPE_EXCEL = "application/msexcel";
	protected final String CONTENT_TYPE_WORD = "application/msword";
	protected final String CONTENT_TYPE_PDF = "application/pdf";
	protected final String CONTENT_TYPE_BIN = "application/octet-stream";
	protected final String CONTENT_TYPE_HTML = "text/html;charset=utf-8";
	protected final String CONTENT_TYPE_XML = "text/xml;charset=utf-8";
	protected final String CONTENT_TYPE_PLAIN = "text/plain;charset=utf-8";
    /**
     * 日志
     */
	protected Logger logger = Logger.getLogger(getClass());
	protected HttpServletRequest request;
	protected HttpServletResponse response;
    /**
     * HTTP请求方法
     */
    protected String requestMethod;
    /**
     * GET请求
     */
    protected boolean isGET;
    /**
     * POST请求
     */
    protected boolean isPOST;
	/**
	 * 封装参数
	 */
	protected Params params = new Params();
	void init(HttpServletRequest request, HttpServletResponse response, String urlParam) {
		this.request = request;
		this.response = response;
        // 获取请求方法
        requestMethod = request.getMethod();
        if ("GET".equalsIgnoreCase(requestMethod))
            isGET = true;
        else if ("POST".equalsIgnoreCase(requestMethod))
            isPOST = true;
        
		params.init(request, response);
        params.setUrlParam(urlParam);
        // 登录用户
	}
	public HttpSession getSession() {
		return request.getSession();
	}
	public ServletContext getServletContext() {
		return getSession().getServletContext();
	}
	/**
	 * Stores an attribute in this request
	 * @param name a String specifying the name of the attribute
	 * @param value the Object to be stored
	 */
	public BaseController setAttr(String name, Object value) {
		request.setAttribute(name, value);
		return this;
	}
	
	/**
	 * Removes an attribute from this request
	 * @param name a String specifying the name of the attribute to remove
	 */
	public BaseController removeAttr(String name) {
		request.removeAttribute(name);
		return this;
	}
	
	/**
	 * Stores attributes in this request, key of the map as attribute name and value of the map as attribute value
	 * @param attrMap key and value as attribute of the map to be stored
	 */
	public BaseController setAttrs(Map<String, Object> attrMap) {
		for (Map.Entry<String, Object> entry : attrMap.entrySet())
			request.setAttribute(entry.getKey(), entry.getValue());
		return this;
	}
	
	/**
	 * Returns the value of the named attribute as an Object, or null if no attribute of the given name exists.
	 * @param name a String specifying the name of the attribute
	 * @return an Object containing the value of the attribute, or null if the attribute does not exist
	 */
	public <T> T getAttr(String name) {
		return (T) request.getAttribute(name);
	}
	
	/**
	 * Return a Object from session.
	 * @param key a String specifying the key of the Object stored in session
	 */
	public <T> T getSessionAttr(String key) {
		HttpSession s = getSession(false);
		return s != null ? (T) s.getAttribute(key) : null;
	}
	
	/**
	 * Store Object to session.
	 * @param key a String specifying the key of the Object stored in session
	 * @param value a Object specifying the value stored in session
	 */
	public BaseController setSessionAttr(String key, Object value) {
		getSession().setAttribute(key, value);
		return this;
	}
	
	/**
	 * Remove Object in session.
	 * @param key a String specifying the key of the Object stored in session
	 */
	public BaseController removeSessionAttr(String key) {
		HttpSession session = request.getSession(false);
		if (session != null)
			session.removeAttribute(key);
		return this;
	}
	
	/**
	 * Get cookie value by cookie name.
	 */
	public String getCookie(String name, String defaultValue) {
		Cookie cookie = getCookieObj(name);
		return cookie != null ? cookie.getValue() : defaultValue;
	}
	
	/**
	 * Get cookie value by cookie name.
	 */
	public String getCookie(String name) {
		return getCookie(name, null);
	}
	
	/**
	 * Get cookie object by cookie name.
	 */
	public Cookie getCookieObj(String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
			for (Cookie cookie : cookies)
				if (cookie.getName().equals(name))
					return cookie;
		return null;
	}
	
	/**
	 * Get all cookie objects.
	 */
	public Cookie[] getCookies() {
		Cookie[] result = request.getCookies();
		return result != null ? result : new Cookie[0];
	}
	
	/**
	 * Set Cookie to response.
	 */
	public BaseController setCookie(Cookie cookie) {
		response.addCookie(cookie);
		return this;
	}
	
	/**
	 * Set Cookie to response.
	 * @param name cookie name
	 * @param value cookie value
	 * @param maxAgeInSeconds -1: clear cookie when close browser. 0: clear cookie immediately.  n>0 : max age in n seconds.
	 * @param path see Cookie.setPath(String)
	 */
	public BaseController setCookie(String name, String value, int maxAgeInSeconds, String path) {
		setCookie(name, value, maxAgeInSeconds, path, null);
		return this;
	}
	
	/**
	 * Set Cookie to response.
	 * @param name cookie name
	 * @param value cookie value
	 * @param maxAgeInSeconds -1: clear cookie when close browser. 0: clear cookie immediately.  n>0 : max age in n seconds.
	 * @param path see Cookie.setPath(String)
	 * @param domain the domain name within which this cookie is visible; form is according to RFC 2109
	 */
	public BaseController setCookie(String name, String value, int maxAgeInSeconds, String path, String domain) {
		Cookie cookie = new Cookie(name, value);
		if (domain != null)
			cookie.setDomain(domain);
		cookie.setMaxAge(maxAgeInSeconds);
		cookie.setPath(path);
		response.addCookie(cookie);
		return this;
	}
	
	/**
	 * Set Cookie with path = "/".
	 */
	public BaseController setCookie(String name, String value, int maxAgeInSeconds) {
		setCookie(name, value, maxAgeInSeconds, "/", null);
		return this;
	}
	
	/**
	 * Remove Cookie with path = "/".
	 */
	public BaseController removeCookie(String name) {
		setCookie(name, null, 0, "/", null);
		return this;
	}
	
	/**
	 * Remove Cookie.
	 */
	public BaseController removeCookie(String name, String path) {
		setCookie(name, null, 0, path, null);
		return this;
	}
	
	/**
	 * Remove Cookie.
	 */
	public BaseController removeCookie(String name, String path, String domain) {
		setCookie(name, null, 0, path, domain);
		return this;
	}
	
	// i18n features --------
	/**
	 * Write Local to cookie
	 */
	public BaseController setLocaleToCookie(Locale locale) {
		setCookie(I18N_LOCALE, locale.toString(), I18N.getI18nMaxAgeOfCookie());
		return this;
	}
	
	public BaseController setLocaleToCookie(Locale locale, int maxAge) {
		setCookie(I18N_LOCALE, locale.toString(), maxAge);
		return this;
	}
	
	public String getText(String key) {
		return I18N.getText(key, getLocaleFromCookie());
	}
	
	public String getText(String key, String defaultValue) {
		return I18N.getText(key, defaultValue, getLocaleFromCookie());
	}
	
	private Locale getLocaleFromCookie() {
		Cookie cookie = getCookieObj(I18N_LOCALE);
		if (cookie != null) {
			return I18N.localeFromString(cookie.getValue());
		}
		else {
			Locale defaultLocale = I18N.getDefaultLocale();
			setLocaleToCookie(defaultLocale);
			return I18N.localeFromString(defaultLocale.toString());
		}
	}
	
	// ----------------
	// render below ---
	private static final RendererFactory rendererFactory = RendererFactory.me();
	
	/**
	 * Hold Render object when invoke renderXxx(...)
	 */
	
	public Renderer getRender() {
		return render;
	}
	
	/**
	 * Render with any Render which extends Render
	 */
	public void render(Renderer render) {
		this.render = render;
	}
	
	/**
	 * Render with view use default type Render 
	 */
	public void render(String view) {
		render = rendererFactory.getRender(view);
	}
	
	/**
	 * Render with jsp view
	 */
	public void jsp(String view) {
		render = rendererFactory.getJSPRenderer(view);
	}
	
	/**
	 * Render with freemarker view
	 */
	public void freeMarker(String view) {
		render = rendererFactory.getFreeMarkerRenderer(view);
	}
	public void freeMarker(String view, String contentType) {
		render = rendererFactory.getFreeMarkerRenderer(view, contentType);
	}
	public void freeMarker(String view, Map<String, Object> data) {
		render = rendererFactory.getFreeMarkerRenderer(view, data);
	}
	/**
	 * Render with text. The contentType is: "text/plain".
	 */
	public void text(String text) {
		render = rendererFactory.getTextRender(text);
	}
	
	/**
	 * Render with text and content type.
	 * <p>
	 * Example: renderText("<user id='5888'>James</user>", "application/xml");
	 */
	public void text(String text, String contentType) {
		render = rendererFactory.getTextRender(text, contentType);
	}
	
	/**
	 * Forward to an action
	 */
	public void forwardAction(String actionUrl) {
		render = new ActionRender(actionUrl);
	}
	
	/**
	 * Render with file
	 */
	public void file(String filePath) {
		render = rendererFactory.getFileRenderer(filePath);
	}
	public void file(String filePath, String name) {
		render = rendererFactory.getFileRenderer(filePath, name);
	}
	/**
	 * Render with file
	 */
	public void file(File file) {
		render = rendererFactory.getFileRenderer(file);
	}
	
	public void file(File file, String name) {
		render = rendererFactory.getFileRenderer(file, name);
	}
	/**
	 * Redirect to url
	 */
	public void redirect(String url) {
		render = rendererFactory.getRedirectRenderer(url);
	}
	
	/**
	 * Redirect to url
	 */
	public void redirect(String url, boolean withQueryString) {
		render = rendererFactory.getRedirectRenderer(url, withQueryString);
	}
	
	/**
	 * Render with view and status use default type Render configured in AppConfig
	 */
	public void render(String view, int status) {
		render = rendererFactory.getRender(view);
		response.setStatus(status);
	}
	
	/**
	 * Render with url and 301 status
	 */
	public void redirect301(String url) {
		render = rendererFactory.getRedirect301Renderer(url);
	}
	
	/**
	 * Render with url and 301 status
	 */
	public void redirect301(String url, boolean withQueryString) {
		render = rendererFactory.getRedirect301Render(url, withQueryString);
	}
	
	/**
	 * Render with view and 404 status
	 */
	public void renderError404(String view) {
		throw new Error404Exception(rendererFactory.getError404Renderer(view));
	}
	
	/**
	 * Render with view and 404 status configured in JFinalConfig
	 */
	public void renderError404() {
		throw new Error404Exception(rendererFactory.getError404Renderer());
	}
	
	/**
	 * Render with view and 500 status
	 */
	public void renderError500(String view) {
		throw new Error500Exception(rendererFactory.getError500Renderer(view));
	}
	
	/**
	 * Render with view and 500 status configured in JFinalConfig
	 */
	public void renderError500() {
		throw new Error500Exception(rendererFactory.getError500Renderer());
	}
	
	/**
	 * Render nothing, no response to browser
	 */
	public void renderNull() {
		render = rendererFactory.getNullRenderer();
	}
	
	/**
	 * Render with html text. The contentType is: "text/html".
	 */
	public void html(String htmlText) {
		render = rendererFactory.getHTMLRenderer(htmlText);
	}
	

	/**
	 * 输出结果
	 */
	public void out(String data) {
		out(data, CONTENT_TYPE_PLAIN);
	}
	/**
	 * 输出文件
	 * @param file
	 */
	public void out(File file, String fileName) {
		file(file, fileName);
	}
	public void out(File file) {
		file(file);
	}
	/**
	 * 输出结果，指定格式
	 * @param data
	 * @param contentType
	 */
	public void out(String data, String contentType) {
		text(data, contentType);
	}
	/**
	 * 输出成功信息
	 * @param data
	 */
	public void success(Object data) {
		try {
			out(new JSONObject()
				.put("success", true)
				.put("data", data).toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 如果提供total属性（Ext Store），则添加success，
	 * 否则，当做普通数据输出
	 * @param data
	 * @throws Exception
	 */
	public void success(JSONObject data) {
		try {
			if (data.isNull("total")) {
				out(new JSONObject()
					.put("success", true)
					.put("data", data).toString());
			} else {
				data.put("success", true);
				out(data.toString());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public void success(Map<String, Object> data) {
		success(new JSONObject(data));
	}
	/**
	 * 成功输出ExtStore
	 * @param store
	 */
	public void success(ExtStore store) {
		out(store.toString());
	}
	/**
	 * 成功输出
	 * @param data
	 * @param contentType
	 */
	public void success(Object data, String contentType) {
		try {
			out(new JSONObject()
				.put("success", true)
				.put("data", data)
				.toString(), 
				contentType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 输出失败信息
	 * @param message
	 * @
	 */
	public void fail(Object message) {
		fail(message, CONTENT_TYPE_PLAIN);
	}
	public void fail(Object message, String contentType) {
		try {
			out(new JSONObject()
			.put("success", false)
			.put("message", message)
			.toString(), contentType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 默认输出成功信息
	 */
	public void success() {
		try {
			out(new JSONObject()
				.put("success", true)
				.toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 输出失败异常信息
	 * @
     */
	public void fail(Exception e) {
		fail(e.getMessage());
	}
	public HttpServletRequest getRequest() {
		return request;
	}
	public Params getParams() {
		return params;
	}
	public HttpSession getSession(boolean createSession) {
		return request.getSession(createSession);
	}
}












