package com.shs.framework.config;
import java.util.ArrayList;
import java.util.List;

import com.shs.framework.handlers.AbstractHandler;

/**
 * Handlers.
 */
final public class Handlers {
	
	private final List<AbstractHandler> handlerList = new ArrayList<AbstractHandler>();
	
	public Handlers add(AbstractHandler handler) {
		if (handler != null)
			handlerList.add(handler);
		return this;
	}
	
	public List<AbstractHandler> getHandlerList() {
		return handlerList;
	}
}
