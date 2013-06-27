package com.shs.framework.core;

import com.shs.framework.renderers.Renderer;

/**
 * ActionRender
 */
final class ActionRender extends Renderer {
	private static final long serialVersionUID = 1L;
	private String actionURL;
	
	public ActionRender(String actionUrl) {
		this.actionURL = actionUrl.trim();
	}
	
	public String getActionURL() {
		return actionURL;
	}
	
	public void render() {
	}
}
