package com.shs.framework.config;
import java.util.LinkedList;
import java.util.List;

import com.shs.framework.plugins.IPlugin;

/**
 * Plugins.
 */
final public class Plugins {
	
	private final List<IPlugin> pluginList = new LinkedList<IPlugin>();
	
	public Plugins add(IPlugin plugin) {
		if (plugin != null)
			this.pluginList.add(plugin);
		return this;
	}
	
	public List<IPlugin> getPluginList() {
		return pluginList;
	}
}
