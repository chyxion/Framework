package com.shs.framework.renderers;
import java.io.File;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;
import com.shs.framework.config.Constants;
import com.shs.framework.utils.PathUtils;

import static com.shs.framework.core.Constant.DEFAULT_FILE_RENDER_BASE_PATH;

/**
 * RenderFactory.
 */
public class RendererFactory {
	
	private Constants constants;
	private static IMainRendererFactory mainRenderFactory;
	private static ServletContext servletContext;
	
	static ServletContext getServletContext() {
		return servletContext;
	}
	
	// singleton
	private static final RendererFactory me = new RendererFactory();
	
	private RendererFactory() {
		
	}
	
	public static RendererFactory me() {
		return me;
	}
	
	public static void setMainRenderFactory(IMainRendererFactory mainRenderFactory) {
		if (mainRenderFactory != null)
			RendererFactory.mainRenderFactory = mainRenderFactory;
	}
	
	public void init(Constants constants, ServletContext servletContext) {
		this.constants = constants;
		RendererFactory.servletContext = servletContext;
		
		// init Render
		Renderer.init(constants.getEncoding(), constants.getDevMode());
		initFreeMarkerRender(servletContext);
		initFileRender(servletContext);
		
		// create mainRenderFactory
		if (mainRenderFactory == null) {
			ViewType defaultViewType = constants.getViewType();
			if (defaultViewType == ViewType.FREE_MARKER)
				mainRenderFactory = new FreeMarkerRendererFactory();
			else if (defaultViewType == ViewType.JSP)
				mainRenderFactory = new JSPRendererFactory();
			else if (defaultViewType == ViewType.JSON)
				mainRenderFactory = new JSONRendererFactory();
			else
				throw new RuntimeException("View Type can not be null.");
		}
	}
	
	private void initFreeMarkerRender(ServletContext servletContext) {
		try {
			Class.forName("freemarker.template.Template");	// detect freemarker.jar
			FreeMarkerRenderer.init(servletContext, 
					Locale.getDefault(), 
					constants.getFreeMarkerTemplateUpdateDelay());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void initFileRender(ServletContext servletContext) {
		FileRenderer.init(getFileRenderPath(), servletContext);
	}
	
	private String getFileRenderPath() {
		String result = constants.getFileRenderPath();
		if (result == null) {
			result = PathUtils.getWebRootPath() + DEFAULT_FILE_RENDER_BASE_PATH;
		}
		if (!result.endsWith(File.separator) && !result.endsWith("/")) {
			result = result + File.separator;
		}
		return result;
	}
	
	/**
	 * Return Render by default ViewType which config in JFinalConfig
	 */
	public Renderer getRender(String view) {
		return mainRenderFactory.getRenderer(view);
	}
	
	public Renderer getFreeMarkerRenderer(String view) {
		return new FreeMarkerRenderer(view);
	}
	public Renderer getFreeMarkerRenderer(String view, String contentType) {
		return new FreeMarkerRenderer(view, contentType);
	}
	public Renderer getFreeMarkerRenderer(String view, Map<String, Object> data) {
		return new FreeMarkerRenderer(view, data);
	}
	public Renderer getJSPRenderer(String view) {
		return new JSPRenderer(view);
	}
	
	public Renderer getTextRender(String text) {
		return new TextRenderer(text);
	}
	
	public Renderer getTextRender(String text, String contentType) {
		return new TextRenderer(text, contentType);
	}
	
	public Renderer getDefaultRenderer(String view) {
		ViewType viewType = constants.getViewType();
		if (viewType == ViewType.FREE_MARKER) {
			return new FreeMarkerRenderer(view + constants.getFreeMarkerViewExtension());
		}
		else if (viewType == ViewType.JSP) {
			return new JSPRenderer(view + constants.getJSPViewExtension());
		} else if (viewType == ViewType.JSON) {
			return new JSONRenderer();
		} else {
			return mainRenderFactory.getRenderer(view + mainRenderFactory.getViewExtension());
		}
	}
	
	public Renderer getError404Renderer() {
		String error404View = constants.getError404View();
		return error404View != null ? new Error404Renderer(error404View) : new Error404Renderer();
	}
	
	public Renderer getError404Renderer(String view) {
		return new Error404Renderer(view);
	}
	
	public Renderer getError500Renderer() {
		String error500View = constants.getError500View();
		return error500View != null ? new Error500Renderer(error500View) : new Error500Renderer();
	}
	
	public Renderer getError500Renderer(String view) {
		return new Error500Renderer(view);
	}
	
	public Renderer getFileRenderer(String filePath) {
		return new FileRenderer(filePath);
	}
	
	public Renderer getFileRenderer(String filePath, String name) {
		return new FileRenderer(filePath, name);
	}
	public Renderer getFileRenderer(File file) {
		return new FileRenderer(file);
	}
	
	public Renderer getFileRenderer(File file, String name) {
		return new FileRenderer(file, name);
	}
	public Renderer getRedirectRenderer(String url) {
		return new RedirectRenderer(url);
	}
	
	public Renderer getRedirectRenderer(String url, boolean withQueryString) {
		return new RedirectRenderer(url, withQueryString);
	}
	
	public Renderer getRedirect301Renderer(String url) {
		return new Redirect301Renderer(url);
	}
	
	public Renderer getRedirect301Render(String url, boolean withQueryString) {
		return new Redirect301Renderer(url, withQueryString);
	}
	
	public Renderer getNullRenderer() {
		return new NullRenderer();
	}
	
	public Renderer getHTMLRenderer(String htmlText) {
		return new HTMLRenderer(htmlText);
	}
	
	// --------
	private static final class FreeMarkerRendererFactory implements IMainRendererFactory {
		public Renderer getRenderer(String view) {
			return new FreeMarkerRenderer(view);
		}
		public String getViewExtension() {
			return ".html";
		}
	}
	
	private static final class JSPRendererFactory implements IMainRendererFactory {
		public Renderer getRenderer(String view) {
			return new JSPRenderer(view);
		}
		public String getViewExtension() {
			return ".jsp";
		}
	}
	private static final class JSONRendererFactory implements IMainRendererFactory {
		public Renderer getRenderer(String view) {
			return new JSONRenderer();
		}
		public String getViewExtension() {
			return ".json";
		}
	}
}


