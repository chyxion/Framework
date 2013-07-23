package com.shs.framework.core;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.json.JSONObject;
import com.shs.framework.config.Constants;
import com.shs.framework.config.AbstractConfig;
import com.shs.framework.config.Handlers;
import com.shs.framework.config.Interceptors;
import com.shs.framework.config.Plugins;
import com.shs.framework.dao.DbManager;
import com.shs.framework.handlers.AbstractHandler;
import com.shs.framework.utils.ClassSearcher;
import com.shs.framework.utils.JSONUtils;

public final class CoreFilter implements Filter {
	
	private AbstractHandler handler;
	private String encoding;
	private AbstractConfig baseConfig;
	private Constants constants;
	private static final Framework framework = Framework.me();
	private static Logger logger = Logger.getLogger(CoreFilter.class);
	private int contextPathLength;
	
	
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		// 编码
		request.setCharacterEncoding(encoding);
		response.setCharacterEncoding(encoding);
		response.setHeader("Pragma", "no-cache");	
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
		// 最新版本IE渲染
        response.setHeader("X-UA-Compatible", "IE=Edge");
        
		String target = request.getRequestURI();
		if (contextPathLength != 0)
			target = target.substring(contextPathLength);
		
		logger.debug("request uri[" + target + "]");
		boolean[] isHandled = {false};
		try {
			handler.handle(target, request, response, isHandled);
		} catch (Exception e) {
			String p = request.getQueryString();
			logger.error(p == null ? target : target + "?" + p, e);
			e.printStackTrace();
		}
		
		if (!isHandled[0]) {
			// gzip压缩
			String acceptEncoding = request.getHeader("Accept-Encoding");
			if (acceptEncoding != null && 
					acceptEncoding.toLowerCase().contains("gzip") && 
					Pattern.compile("\\.(?i)(js|css|html|jsp|jspx)$")
					.matcher(target).find()) {
				logger.debug("gzip compress request[" + target + "]");
				GzipResponseWrapper wrappedResponse = new GzipResponseWrapper(response);
				chain.doFilter(req, wrappedResponse);
				wrappedResponse.finish();
			} else { // 不做处理
				logger.debug("skip handle request[" + target + "]");
				chain.doFilter(req, res);
			}
		}
		logger.debug("after filter [" + target + "]");
	}
	
	public void destroy() {
		baseConfig.beforeStop();
		framework.stopPlugins();
	}
	
	public void init(FilterConfig filterConfig) throws ServletException {
		ServletContext servletContext = filterConfig.getServletContext();
		try {
			// Log4j配置
			URL log4jConfig = servletContext.getResource("/WEB-INF/config/log4j.xml");
			if (log4jConfig != null)
				DOMConfigurator.configure(log4jConfig);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
		// 初始化系统配置
		JSONObject joConfig = initConfig(servletContext);
		// 启动框架
		framework.init(baseConfig, servletContext);
		handler = framework.getHandler();
		constants = AppConfig.getConstants();
		constants.setJOConfig(joConfig);
		encoding = constants.getEncoding();
		// 启动后回调
		baseConfig.afterStart();
		String contextPath = servletContext.getContextPath();
		contextPathLength = (contextPath == null || "/".equals(contextPath) ? 0 : contextPath.length());
	}
	
	private JSONObject initConfig(ServletContext servletContext) {
		try {
			JSONObject joConfig;
			URL appConfig = servletContext.getResource("/WEB-INF/config/config.json");
			if (appConfig != null) {
				joConfig = JSONUtils.newJSONObject(appConfig.openStream());
				JSONObject joDataSource = joConfig.getJSONObject("database");
				DbManager.DRIVER = joDataSource.getString("driver");
				DbManager.URL = joDataSource.getString("url");
				DbManager.USER_NAME = joDataSource.getString("userName");
				DbManager.PASSWORD = joDataSource.getString("password");
				DbManager.setDialect(joDataSource.getString("dialect"));
				DbManager.LOWERCASE = joDataSource.optBoolean("lowercase");
			} else {
				joConfig = new JSONObject();
			}
			// 初始化配置类
			List<Class<? extends AbstractConfig>> cc = ClassSearcher.findInClassPath(AbstractConfig.class);
			if (cc.size() > 0) {
				baseConfig = cc.get(0).newInstance();
			} else { // 没有配置类
				baseConfig = new AbstractConfig() {
					@Override
					public void configPlugin(Plugins plugins) {
					}
					@Override
					public void configInterceptor(Interceptors interceptors) {
					}
					@Override
					public void configHandler(Handlers handlers) {
					}
					@Override
					public void configConstant(Constants constants) {
					}
				};
			}
			return joConfig;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
