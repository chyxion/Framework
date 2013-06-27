package com.shs.framework.renderers;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;

/**
 * Error500Render.
 */
public class Error500Renderer extends Renderer {
	
	private static final long serialVersionUID = 4864834986049401413L;
	private static final String contentType = "text/html;charset=" + getEncoding();
	private static final String defaultHtml = "<html><head><title>500 Internal Server Error</title></head><body bgcolor='white'><center><h1>500 Internal Server Error</h1></center><hr><center>error</center></body></html>";
	
	public Error500Renderer(String view) {
		this.view = view;
	}
	
	public Error500Renderer() {
		
	}
	
	public void render() {
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		
		// render with view
		if (view != null) {
			RendererFactory.me().getRender(view).setContext(request, response).render();
			return;
		}
		
		// render with defaultHtml
		PrintWriter writer = null;
		try {
			response.setContentType(contentType);
	        writer = response.getWriter();
	        writer.write(defaultHtml);
	        writer.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			if (writer != null)
				writer.close();
		}
	}
}





