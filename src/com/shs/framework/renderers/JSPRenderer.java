package com.shs.framework.renderers;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
/**
 * JspRender.
 */
@SuppressWarnings("unchecked")
public class JSPRenderer extends Renderer {
	private static final long serialVersionUID = 1L;

	public JSPRenderer(String view) {
		this.view = view;
	}
	
	public void render() {
		try {
			if (!Pattern.compile("\\.(?i)(jsp|jspx)$").matcher(view).find()) 
				view += ".jsp";
			
			if (!view.startsWith("/")) 
				view = "/" + view;
			
			request.getRequestDispatcher(BASE_VIEW_PATH + view).forward(request, response);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private Object handleObject(Object value, int depth) {
		if(value == null || (depth--) <= 0)
			return value;
		
		if (value instanceof List)
			return handleList((List)value, depth);
		else if(value instanceof Map)
			return handleMap((Map)value, depth);
		else if (value instanceof Object[])
			return handleArray((Object[])value, depth);
		else
			return value;
	}
	
	private Map handleMap(Map map, int depth) {
		if (map == null || map.size() == 0)
			return map;
		
		Map<Object, Object> result = map;
		for (Map.Entry<Object, Object> e : result.entrySet()) {
			Object key = e.getKey();
			Object value = e.getValue();
			value = handleObject(value, depth);
			result.put(key, value);
		}
		return result;
	}
	
	private List handleList(List list, int depth) {
		if (list == null || list.size() == 0)
			return list;
		
		List result = new ArrayList(list.size());
		for (Object value : list)
			result.add(handleObject(value, depth));
		return result;
	}
	
	private List handleArray(Object[] array, int depth) {
		if (array == null || array.length == 0)
			return new ArrayList(0);
		
		List result = new ArrayList(array.length);
		for (int i=0; i<array.length; i++)
			result.add(handleObject(array[i], depth));
		return result;
	}
}


