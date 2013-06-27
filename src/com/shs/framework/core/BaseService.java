package com.shs.framework.core;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.shs.framework.dao.BaseDAO;
import com.shs.framework.exceptions.ValidateException;
import com.shs.framework.validate.ErrorHandler;
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
    protected final int INT = 0;
    protected final int CHAR = 1;
    protected final int STRING = 2;
    protected final int DOUBLE = 3;
    protected final int BOOLEAN = 4;
    protected final int NOT_NULL = 5;
    protected final int NOT_BLANK = 6;
    protected final int EXIST = 7;
    /**
     * JSONObject
     */
    protected final int JO = 8;
    /**
     * JSONArray
     */
    protected final int JA = 9;
    protected final int MAX_VALUE = 10;
    protected final int MIN_VALUE = 11;
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
    /**
     * 执行验证
     * @param name
     * @param types
     * @throws Exception
     */
    protected void validate(String name, int type) {
        	doValidate(name, type, null);
    }
    protected void validate(String name, int type, String msg) {
    	doValidate(getParam(name), name, type, msg);
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
					throw new ValidateException("在参数JSONObject [" + nameSplits[i-1] + 
							"] 中未能找到未能找到名为 [" + nameSplits[i] + "]的对象！");
				}
            }
        } else {
        	v = params.get(name);
        }
        return v;
    }
    /**
     * 验证不为null
     * @param name
     * @throws Exception
     */
    protected void validate(String name) {
    	doValidate(getParam(name), name, NOT_NULL, null);
    }
    protected void errorHandle() {
    	
    }
    protected BaseService require(String name, String message) {
    	if (getParam(name) == null) {
    		throwE(message, "参数[" + name + "]不能为空！");
    	}
    	return this;
    }
    protected void max(String name, double v, String msg) {
    	//getParam(name)
    }
    protected void min(String name, double v, String msg) {
    }
    protected void maxLength(String name, int length, String message) {
    	
    }
    protected void minLength(String name, int length, String message) {
    	
    }
    protected void notBlank(String name, String messge) {
    	
    }
    protected void notNull(String name, String message) {
    	
    }
    protected void notEmpty(String name, String message) {
    	
    }
    /**
     * 执行参数验证
     * @param name
     * @param type
     * @throws Exception
     */
    private void doValidate(String name, int type, String msg) { 
    	doValidate(getParam(name), name, type, msg);
    }
    private void throwE(String msg, String defaultMsg) {
    	throw new ValidateException(StringUtils.isEmpty(msg) ? defaultMsg : msg);
    }
    private void doValidate(Object value, String name, int type, String msg) {
        try {
			if (INT == type) {
				try {
					Integer.parseInt((String) value);
				} catch (NullPointerException e) {
					throwE(msg, "整数类型参数 ["+ name + "] 为空！");
				} catch (NumberFormatException e) {
					throwE(msg, "整数类型参数 ["+ name + "] 的值 [" + value + "] 非法！");
				}
			} else if (CHAR == type) {
			    String ch = (String) value;
			    if (ch.length() > 1)
			        throw new ValidateException(String.format("字符参数 [%s] 长度不能超过 [1]，实际长度为 [%d]!", name, ch.length()));
			} else if (DOUBLE == type) {
				try {
					Double.parseDouble((String) value);
				} catch (NullPointerException e) {
					throwE(msg, "浮点数类型参数 ["+ name + "] 为空！");
				} catch (NumberFormatException e) {
					throwE(msg, "浮点数类型参数 ["+ name + "] 的值 [" + value + "] 非法！");
				}
			} else if (BOOLEAN == type) {
				try {
					Boolean.parseBoolean((String) value);
				} catch (NullPointerException e) {
					throwE(msg, "布尔类型参数 ["+ name + "] 为空！");
				} catch (NumberFormatException e) {
					throwE(msg, "布尔类型参数 ["+ name + "] 的值 [" + value + "] 非法！");
				}
			} else if (JO == type) {
				if (value instanceof JSONObject) {
					// noop
				} else {
					if (value != null) {
						try {
							new JSONObject((String)value);
						} catch (JSONException e) {
							throwE(msg, "JSONObject参数 ["+ name + "] 的值 [" + 
									value + "] 不是合法的JSONObject格式！");
						}
					} else {
						throwE(msg, "JSONObject参数 ["+ name + "] 为空！");
					}
				}
			} else if (JA == type) {
				if (value instanceof JSONArray) {
					// noop
				} else {
					if (value != null) {
						try {
							new JSONArray((String)value);
						} catch (JSONException e) {
							throwE(msg, "JSONArray参数 ["+ name + "] 的值 [" + 
									value + "] 不是合法的JSONArray格式！");
						}
					} else {
						throwE(msg, "JSONArray参数 ["+ name + "] 为空！");
					}
				}
			} else if (NOT_NULL == type) {
				if (value == null) {
			        throwE(msg, "参数 ["+ name + "] 不能为空！");
				}
			} else if (NOT_BLANK == type) {
			    if(StringUtils.isEmpty((String) value))
			        throwE(msg, "参数 ["+ name + "] 不能为空！");
			} 
		} catch (Exception e) {
			throw new ValidateException(e);
		}
    }
    public void onError(ErrorHandler eh) {
    	
    }
}
