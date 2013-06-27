package com.shs.framework.renderers;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
/**
 * Error404Render.
 */
public class Error404Renderer extends Renderer {
	
	private static final long serialVersionUID = 1764764489766904795L;
	private static final String contentType = "text/html;charset=" + getEncoding();
	private static final String defaultHtml = "<!DOCTYPE html><html><head><title>404 Not Found</title></head><body><h1>404 Not Found</h1><hr></body></html>";
	
	public Error404Renderer(String view) {
		this.view = view;
	}
	
	public Error404Renderer() {
		
	}
	
	public void render() {
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		
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




