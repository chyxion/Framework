package com.shs.framework.config;
import java.util.LinkedList;
import java.util.List;

import com.shs.framework.aop.IActionInterceptor;

/**
 * The interceptors applied to all actions.
 */
final public class Interceptors {
	
	private final List<IActionInterceptor> interceptorList = new LinkedList<IActionInterceptor>();
	
	public Interceptors add(IActionInterceptor globalInterceptor) {
		if (globalInterceptor != null)
			this.interceptorList.add(globalInterceptor);
		return this;
	}
	
	public IActionInterceptor[] getInterceptorArray() {
		IActionInterceptor[] result = interceptorList.toArray(new IActionInterceptor[interceptorList.size()]);
		return result == null ? new IActionInterceptor[0] : result;
	}
}
