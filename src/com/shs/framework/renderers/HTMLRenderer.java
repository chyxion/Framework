package com.shs.framework.renderers;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * HtmlRender.
 */
public class HTMLRenderer extends Renderer {
	
	private static final long serialVersionUID = -1805855373995133760L;
	private static final String contentType = "text/html;charset=" + encoding;
	private String text;
	
	public HTMLRenderer(String text) {
		this.text = text;
	}
	
	public void render() {
		PrintWriter writer = null;
		try {
	        
			response.setContentType(contentType);
	        writer = response.getWriter();
	        writer.write(text);
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
