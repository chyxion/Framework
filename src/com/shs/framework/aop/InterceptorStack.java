package com.shs.framework.aop;

import java.util.ArrayList;
import java.util.List;

import com.shs.framework.core.ActionInvocation;

/**
 * InterceptorStack.
 */
public abstract class InterceptorStack implements IActionInterceptor {
	
	private IActionInterceptor[] inters;
	private List<IActionInterceptor> interList;
	
	public InterceptorStack() {
 		config();
 		
		if (interList == null)
			throw new RuntimeException("You must invoke addInterceptors(...) to config your InterceptorStack");
		
		inters = interList.toArray(new IActionInterceptor[interList.size()]);
		interList.clear();
		interList = null;
	}
	
	protected final InterceptorStack addInterceptors(IActionInterceptor... interceptors) {
		if (interceptors == null || interceptors.length == 0)
			throw new IllegalArgumentException("Interceptors can not be null");
		
		if (interList == null)
			interList = new ArrayList<IActionInterceptor>();
		
		for (IActionInterceptor ref : interceptors)
			interList.add(ref);
		
		return this;
	}
	
	public final void intercept(ActionInvocation ai) {
		new ActionInvocationWrapper(ai, inters).invoke();
	}
	
	public abstract void config();
}



