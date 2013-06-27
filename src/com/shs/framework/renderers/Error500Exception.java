package com.shs.framework.renderers;

/**
 * Error500Exception.
 */
public class Error500Exception extends RuntimeException {
	
	private static final long serialVersionUID = -7521710800649772411L;
	private Renderer error500Render;
	
	public Error500Exception(Renderer error500Render) {
		this.error500Render = error500Render;
	}
	
	public Renderer getError500Render() {
		return error500Render;
	}
}