package com.shs.framework.renderers;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * @version 0.1
 * @author chyxion
 * @describe: 默认JSON渲染器
 * @date created: Apr 9, 2013 10:47:13 AM
 * @support: chyxion@163.com
 * @date modified: 
 * @modified by: 
 * @copyright: Shenghang Soft All Right Reserved.
 */
public class JSONRenderer extends Renderer {
	private static final long serialVersionUID = 1L;
	@Override
	public void render() {
		try {
			new TextRenderer(new JSONObject().put("success", true).toString())
				.setContext(request, response).render();
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
}
