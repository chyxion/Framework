package com.shs.framework.renderers;

/**
 * Error404Exception.
 */
public class Error404Exception extends RuntimeException {
	
	private static final long serialVersionUID = 7620194943724436754L;
	private Renderer error404Render;
	
	public Error404Exception(Renderer error404Render) {
		this.error404Render = error404Render;
	}
	
	public Renderer getError404Render() {
		return error404Render;
	}
}