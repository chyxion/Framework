package com.shs.framework.core;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.shs.framework.aop.Before;
import com.shs.framework.aop.ClearInterceptor;
import com.shs.framework.aop.ClearLayer;
import com.shs.framework.aop.IActionInterceptor;

/**
 * InterceptorBuilder
 */
class InterceptorBuilder {
	
	private static final IActionInterceptor[] NULL_INTERCEPTOR_ARRAY = new IActionInterceptor[0];
	
	@SuppressWarnings("unchecked")
	void addToInterceptorsMap(IActionInterceptor[] defaultInters) {
		for (IActionInterceptor inter : defaultInters)
			intersMap.put((Class<IActionInterceptor>)inter.getClass(), inter);
	}
	
	/**
	 * Build interceptors of Controller
	 */
	IActionInterceptor[] buildControllerInterceptors(Class<? extends BaseController> controllerClass) {
		Before before = controllerClass.getAnnotation(Before.class);
		return before != null ? createInterceptors(before) : NULL_INTERCEPTOR_ARRAY;
	}
	
	/**
	 * Build interceptors of Method
	 */
	IActionInterceptor[] buildMethodInterceptors(Method method) {
		Before before = method.getAnnotation(Before.class);
		return before != null ? createInterceptors(before) : NULL_INTERCEPTOR_ARRAY;
	}
	
	/**
	 * Build interceptors of Action
	 */
	IActionInterceptor[] buildActionInterceptors(IActionInterceptor[] defaultInters, IActionInterceptor[] controllerInters, Class<? extends BaseController> controllerClass, IActionInterceptor[] methodInters, Method method) {
		ClearLayer controllerClearType = getControllerClearType(controllerClass);
		if (controllerClearType != null) {
			defaultInters = NULL_INTERCEPTOR_ARRAY;
		}
		
		ClearLayer methodClearType = getMethodClearType(method);
		if (methodClearType != null) {
			controllerInters = NULL_INTERCEPTOR_ARRAY;
			if (methodClearType == ClearLayer.ALL) {
				defaultInters = NULL_INTERCEPTOR_ARRAY;
			}
		}
		
		int size = defaultInters.length + controllerInters.length + methodInters.length;
		IActionInterceptor[] result = (size == 0 ? NULL_INTERCEPTOR_ARRAY : new IActionInterceptor[size]);
		
		int index = 0;
		for (int i=0; i<defaultInters.length; i++) {
			result[index++] = defaultInters[i];
		}
		for (int i=0; i<controllerInters.length; i++) {
			result[index++] = controllerInters[i];
		}
		for (int i=0; i<methodInters.length; i++) {
			result[index++] = methodInters[i];
		}
		
		return result;
	}
	
	private ClearLayer getMethodClearType(Method method) {
		ClearInterceptor clearInterceptor = method.getAnnotation(ClearInterceptor.class);
		return clearInterceptor != null ? clearInterceptor.value() : null ;
	}
	
	private ClearLayer getControllerClearType(Class<? extends BaseController> controllerClass) {
		ClearInterceptor clearInterceptor = controllerClass.getAnnotation(ClearInterceptor.class);
		return clearInterceptor != null ? clearInterceptor.value() : null ;
	}
	
	private Map<Class<IActionInterceptor>, IActionInterceptor> intersMap = new HashMap<Class<IActionInterceptor>, IActionInterceptor>();
	
	/**
	 * Create interceptors with Annotation of Before. Singleton version.
	 */
	private IActionInterceptor[] createInterceptors(Before beforeAnnotation) {
		IActionInterceptor[] result = null;
		@SuppressWarnings("unchecked")
		Class<IActionInterceptor>[] interceptorClasses = (Class<IActionInterceptor>[]) beforeAnnotation.value();
		if (interceptorClasses != null && interceptorClasses.length > 0) {
			result = new IActionInterceptor[interceptorClasses.length];
			for (int i=0; i<result.length; i++) {
				result[i] = intersMap.get(interceptorClasses[i]);
				if (result[i] != null)
					continue;
				
				try {
					result[i] = (IActionInterceptor)interceptorClasses[i].newInstance();
					intersMap.put(interceptorClasses[i], result[i]);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return result;
	}
}




