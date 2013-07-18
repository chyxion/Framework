package com.shs.framework.core;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.shs.framework.dao.BaseDAO;
import com.shs.framework.exceptions.ValidateException;

/**
 * Description: 基础Service，提供验证等
 * User: chyxion
 * Date: 12/24/12
 * Time: 1:35 PM
 * Support: chyxion@163.com
 */
public class BaseService {
	protected Logger logger = Logger.getLogger(getClass());
	protected BaseDAO dao = new BaseDAO();
    /**
     * 参数对象
     */
    protected Params params;
    
    public Params getParams() {
		return params;
	}
	public BaseService setParams(Params params) {
		this.params = params;
		return this;
	}
    private Object getParam(String name) {
    	Object v;
        if (name.contains(".")) {
        	String[] nameSplits = name.split("\\.");
            JSONObject joValue = params.getJSONObject(nameSplits[0]);
            v = joValue;
            for (int i = 1; i < nameSplits.length; ++i) {
				try {
					v = ((JSONObject) v).get(nameSplits[i]);
				} catch (JSONException e) {
					v = null;
					break;
				}
            }
        } else {
        	v = params.get(name);
        }
        return v;
    }
    protected BaseService require(String name, String message) {
    	if (getParam(name) == null) {
    		throwE(message, "参数[" + name + "]不能为空！");
    	}
    	return this;
    }
    protected BaseService requireDouble(String name, String message) {
    	String v = (String) getParam(name);
    	if (v == null) {
    		throwE(message, "数值类型参数[" + name + "]不能为空！");
    	} else {
    		try {
				Double.parseDouble(v);
			} catch (NumberFormatException e) {
				throwE(message, "数值类型参数[" + name + "]的值[" + v + "]非法！");
			}
    	}
    	return this;
    }
    protected BaseService requireJSONObject(String name, String message) {
    	if (!(getParam(name) instanceof JSONObject)) {
    		throwE(message, "JSONObject参数[" + name + "]不能为空！");
    	}
    	return this;
    }
    protected BaseService requireJSONArray(String name, String message) {
    	if (!(getParam(name) instanceof JSONArray)) {
    		throwE(message, "JSONArray参数[" + name + "]不能为空！");
    	}
    	return this;
    }

    protected BaseService max(String name, double v, String message) {
    	if (Double.parseDouble((String) getParam(name)) > v) {
    		throwE(message, "参数[" + name + "]不能大于[" + v + "]！");
    	}
    	return this;
    }
    protected BaseService min(String name, double v, String message) {
    	if (Double.parseDouble((String) getParam(name)) < v) {
    		throwE(message, "参数[" + name + "]不能小于[" + v + "]！");
    	}
    	return this;
    }
    protected BaseService maxLength(String name, int length, String message) {
    	if (((String) getParam(name)).length() > length) {
    		throwE(message, "参数[" + name + "]长度不能大于[" + length + "]！");
    	}
    	return this;
    }
    protected BaseService minLength(String name, int length, String message) {
    	if (((String) getParam(name)).length() < length) {
    		throwE(message, "参数[" + name + "]长度不能小于[" + length + "]！");
    	}
    	return this;
    }
    protected BaseService notBlank(String name, String message) {
    	if (StringUtils.isBlank((String) getParam(name))) {
    		throwE(message, "参数[" + name + "]不能为空！");
    	}
    	return this;
    }
    protected BaseService notNull(String name, String message) {
    	return require(name, message);
    }
    private void throwE(String msg, String defaultMsg) {
    	throw new ValidateException(StringUtils.isEmpty(msg) ? defaultMsg : msg);
    }
}
