package com.shs.framework.renderers;
import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import com.shs.framework.utils.Utils;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * FreeMarkerRender.
 */
public class FreeMarkerRenderer extends Renderer {
	
	private static final long serialVersionUID = 1L;
	private transient String contentType = "text/html;charset=" + encoding;
	private transient static final Configuration config = new Configuration();
	private transient Map<String, Object> data;
	private transient Writer writer;
	
	public FreeMarkerRenderer(String view) {
		this.view = view;
		this.data = new HashMap<String, Object>();
	}
	public FreeMarkerRenderer(String view, String contentType) {
		this.view = view;
		this.contentType = contentType;
		this.data = new HashMap<String, Object>();
	}
	public FreeMarkerRenderer(String view, Map<String, Object> data) {
		this.view = view;
		this.data = data;
	}
	public static Configuration getConfiguration() {
		return config;
	}
	
	/**
	 * Set freemarker's property.
	 * The value of template_update_delay is 5 seconds.
	 * Example: FreeMarkerRender.setProperty("template_update_delay", "1600");
	 */
	public static void setProperty(String propertyName, String propertyValue) {
		try {
			FreeMarkerRenderer.getConfiguration().setSetting(propertyName, propertyValue);
		} catch (TemplateException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void setProperties(Properties properties) {
		try {
			FreeMarkerRenderer.getConfiguration().setSettings(properties);
		} catch (TemplateException e) {
			throw new RuntimeException(e);
		}
	}
	
    static void init(ServletContext servletContext, Locale locale, int updateDelay) {
		config.setServletContextForTemplateLoading(servletContext, BASE_VIEW_PATH);
        // - Set update delay to 0 for now, to ease debugging and testing.
        //   Higher value should be used in production environment.
        
        if (getDevMode()) {
        	config.setTemplateUpdateDelay(0);
       	}
        else {
        	config.setTemplateUpdateDelay(updateDelay);
        }
        
        // - Set an error handler that prints errors so they are readable with
        //   a HTML browser.
        // config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        
        // - Use beans wrapper (recommmended for most applications)
        config.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
        // - Set the default charset of the template files
        config.setDefaultEncoding(encoding);		
		config.setEncoding(locale, encoding);
        config.setOutputEncoding(encoding);			
        config.setLocale(locale);		// config.setLocale(Locale.US);
        config.setLocalizedLookup(false);
        
        // 去掉int型输出时的逗号, 例如: 123,456
        config.setNumberFormat("#0.#####");
    }
    
	public void render() {
        Enumeration<String> attrs = request.getAttributeNames();
        // 收集request中的属性
		while (attrs.hasMoreElements()) {
			String attrName = attrs.nextElement();
			data.put(attrName, request.getAttribute(attrName));
		}
		// 添加session到数据
		HttpSession hs = request.getSession();
		Map<String, Object> s = new HashMap<String, Object>();
		Enumeration<String> names = hs.getAttributeNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			s.put(name, hs.getAttribute(name));
		}
		data.put("session", s);
		// 基路径
		data.put("BASE_PATH", Utils.basePath(request));
		// 采用绝对路径
    	if (!view.startsWith("/")) view = "/" + view;
    	
        try {
			Template ftpl = config.getTemplate(view);
			ftpl.setEncoding(encoding);
			// 默认是输出响应
			if (writer == null)  {
				response.setContentType(contentType);
				writer = response.getWriter();
			}
			ftpl.process(data, writer);		// Merge the data-model and the template
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	public FreeMarkerRenderer setWriter(Writer writer) {
		this.writer = writer;
		return this;
	}
}




