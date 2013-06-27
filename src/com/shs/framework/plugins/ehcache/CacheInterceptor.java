package com.shs.framework.plugins.ehcache;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.http.HttpServletRequest;
import com.shs.framework.aop.IActionInterceptor;
import com.shs.framework.core.ActionInvocation;
import com.shs.framework.core.BaseController;
import com.shs.framework.renderers.Renderer;

/**
 * CacheInterceptor.
 */
public class CacheInterceptor implements IActionInterceptor {
	
	private static final String renderKey = "$renderKey$";
	private static volatile ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<String, ReentrantLock>();
	
	private ReentrantLock getLock(String key) {
		ReentrantLock lock = lockMap.get(key);
		if (lock != null)
			return lock;
		
		lock = new ReentrantLock();
		ReentrantLock previousLock = lockMap.putIfAbsent(key, lock);
		return previousLock == null ? lock : previousLock;
	}
	
	final public void intercept(ActionInvocation ai) {
		BaseController controller = ai.getController();
		String cacheName = buildCacheName(ai, controller);
		String cacheKey = buildCacheKey(ai, controller);
		Map<String, Object> cacheData = CacheKit.get(cacheName, cacheKey);
		if (cacheData == null) {
			Lock lock = getLock(cacheName);
			lock.lock();					// prevent cache snowslide
			try {
				cacheData = CacheKit.get(cacheName, cacheKey);
				if (cacheData == null) {
					ai.invoke();
					cacheAction(cacheName, cacheKey, controller);
					return ;
				}
			}
			finally {
				lock.unlock();
			}
		}
		
		useCacheDataAndRender(cacheData, controller);
	}
	
	private String buildCacheName(ActionInvocation ai, BaseController controller) {
		CacheName cacheName = ai.getMethod().getAnnotation(CacheName.class);
		if (cacheName != null)
			return cacheName.value();
		cacheName = controller.getClass().getAnnotation(CacheName.class);
		if (cacheName != null)
			return cacheName.value();
		return ai.getActionKey();
	}
	
	private String buildCacheKey(ActionInvocation ai, BaseController controller) {
		StringBuilder sb = new StringBuilder(ai.getActionKey());
		String urlPara = controller.getParams().get();
		if (urlPara != null)
			sb.append("/").append(urlPara);
		
		String queryString = controller.getRequest().getQueryString();
		if (queryString != null)
			sb.append("?").append(queryString);
		return sb.toString();
	}
	
	private void cacheAction(String cacheName, String cacheKey, BaseController controller) {
		HttpServletRequest request = controller.getRequest();
		Map<String, Object> cacheData = new HashMap<String, Object>();
		Enumeration<String> names = request.getAttributeNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			cacheData.put(name, request.getAttribute(name));
		}
		
		cacheData.put(renderKey, controller.getRender());		// cache render
		CacheKit.put(cacheName, cacheKey, cacheData);
	}
	
	private void useCacheDataAndRender(Map<String, Object> data, BaseController controller) {
		HttpServletRequest request = controller.getRequest();
		Set<Entry<String, Object>> set = data.entrySet();
		for (Iterator<Entry<String, Object>> it=set.iterator(); it.hasNext();) {
			Entry<String, Object> entry = it.next();
			request.setAttribute(entry.getKey(), entry.getValue());
		}
		
		controller.render((Renderer)request.getAttribute(renderKey));		// set render from cache
		request.removeAttribute(renderKey);
	}
}




