package com.shs.framework.renderers;

import java.io.IOException;

/**
 * RedirectRender with status: 302 Found.
 */
public class RedirectRenderer extends Renderer {
	
	private static final long serialVersionUID = 1L;
	private String url;
	private boolean withQueryString;
	
	public RedirectRenderer(String url) {
		this.url = url;
		this.withQueryString = false;
	}
	
	public RedirectRenderer(String url, boolean withQueryString) {
		this.url = url;
		this.withQueryString =  withQueryString;
	}
	
	public void render() {
		if (withQueryString) {
			String queryString = request.getQueryString();
			if (queryString != null)
				url = url + "?" + queryString;
		}
		try {
			response.sendRedirect(url);	// always 302
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

