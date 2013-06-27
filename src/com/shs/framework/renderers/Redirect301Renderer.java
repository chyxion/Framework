package com.shs.framework.renderers;

import javax.servlet.http.HttpServletResponse;

/**
 * Redirect301Render.
 */
public class Redirect301Renderer extends Renderer {
	
	private static final long serialVersionUID = -6822589387282014944L;
	private String url;
	private boolean withQueryString;
	
	public Redirect301Renderer(String url) {
		this.url = url;
		this.withQueryString = false;
	}
	
	public Redirect301Renderer(String url, boolean withQueryString) {
		this.url = url;
		this.withQueryString = withQueryString;
	}
	
	public void render() {
		if (withQueryString) {
			String queryString = request.getQueryString();
			if (queryString != null)
				url = url + "?" + queryString;
		}
		
		response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
		// response.sendRedirect(url);	// always 302
		response.setHeader("Location", url);
		response.setHeader("Connection", "close");
	}
}
