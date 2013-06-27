package com.shs.framework.renderers;

/**
 * IMainRenderFactory. Create Render for Controller.render(String view);
 */
public interface IMainRendererFactory {
	
	/**
	 * Return the render.
	 * @param view the view for this render.
	 */
	Renderer getRenderer(String view);
	
	/**
	 * The extension of the view.
	 * <p>
	 * It must start with dot char "."
	 * Example: ".html" or ".ftl"
	 * </p>
	 */
	String getViewExtension();
}


