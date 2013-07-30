package com.shs.framework.core;
import com.shs.framework.exceptions.ValidateException;
import com.shs.framework.upload.MultipartRequest;
import com.shs.framework.upload.UploadFile;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 请求参数封装
 * @version 0.1
 * @author chyxion
 * @describe: 
 * @date created: Dec 10, 2012 11:16:31 AM
 * @support: chyxion@163.com
 * @date modified: 
 * @modified by: 
 * @copyright: Shenghang Soft All Right Reserved.
 */
@SuppressWarnings("unchecked")
public class Params {
	/**
	 * session 用户key
	 */
	/**
	 * 参数集合。
	 */
	private Map<String, String>  mapParams = new HashMap<String, String>();
    /**
     * 参数缓存
     */
    private Map<String, Object> mapCache = new HashMap<String, Object>();
    /**
     * http request
     */
    private HttpServletRequest request;
	// --------
	private MultipartRequest multipartRequest;
    /**
     * http response
     */
    private HttpServletResponse response;
    /**
     * site base path
     */
	private String urlParam;
	private String[] urlParams;
	/**
	 * 是否是Ajax请求
	 */
	private boolean ajaxRequest;
	/**
	 * Get upload file from multipart request.
	 */
	public List<UploadFile> getFiles(String saveDirectory, Integer maxPostSize, String encoding) {
		if (multipartRequest == null) {
			request =multipartRequest = new MultipartRequest(request, saveDirectory, maxPostSize, encoding);
			addMultipartParams(multipartRequest);
		}
		return multipartRequest.getFiles();
	}
	
	public UploadFile getFile(String parameterName, String saveDirectory, Integer maxPostSize, String encoding) {
		getFiles(saveDirectory, maxPostSize, encoding);
		return getFile(parameterName);
	}
	
	public List<UploadFile> getFiles(String saveDirectory, int maxPostSize) {
		if (multipartRequest == null) {
			request = multipartRequest = new MultipartRequest(request, saveDirectory, maxPostSize);
			addMultipartParams(multipartRequest);
		}
		return multipartRequest.getFiles();
	}
	
	public UploadFile getFile(String parameterName, String saveDirectory, int maxPostSize) {
		getFiles(saveDirectory, maxPostSize);
		return getFile(parameterName);
	}
	
	public List<UploadFile> getFiles(String saveDirectory) {
		if (multipartRequest == null) {
			request = multipartRequest = new MultipartRequest(request, saveDirectory);
			addMultipartParams(multipartRequest);
		}
		return multipartRequest.getFiles();
	}
	
	public UploadFile getFile(String name, String saveDirectory) {
		getFiles(saveDirectory);
		return getFile(name);
	}
	
	public List<UploadFile> getFiles() {
		if (multipartRequest == null) {
			request = multipartRequest = new MultipartRequest(request);
			addMultipartParams(multipartRequest);
		}
		return multipartRequest.getFiles();
	}
	/**
	 * 添加Payload参数
	 * @param mr
	 * @return
	 */
	private Params addMultipartParams(MultipartRequest mr) {
		com.oreilly.servlet.MultipartRequest r = multipartRequest.getMultipartRequest();
		Enumeration<String> names = r.getParameterNames();
		String name;
		while (names.hasMoreElements()) {
			name = names.nextElement();
			mapParams.put(name, r.getParameter(name));
		}
		return this;
	}
	public UploadFile getFile() {
		List<UploadFile> uploadFiles = getFiles();
		return uploadFiles.size() > 0 ? uploadFiles.get(0) : null;
	}
	
	public UploadFile getFile(String parameterName) {
		List<UploadFile> uploadFiles = getFiles();
		for (UploadFile uploadFile : uploadFiles) {
			if (uploadFile.getParameterName().equals(parameterName)) {
				return uploadFile;
			}
		}
		return null;
	}
	/**
	 *  获取参数
	 * @param name
	 * @return
	 */
	public String get(String name) {
		return mapParams.get(name);
	}
	public String getDecode(String name) {
		String v = get(name);
		return StringUtils.isEmpty(v) ? null : decodeParam(v);
	}
	public String getDecode(int index) {
		String v = get(index);
		return StringUtils.isEmpty(v) ? null : decodeParam(v);
	}
	private String decodeParam(String v) {
		try {
			return URLDecoder.decode(new String(v.getBytes("iso-8859-1"), "utf-8"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	public String get() {
		return urlParam;
	}
	public Params put(String name, String v) {
		mapParams.put(name, v);
		return this;
	}
	public Params put(String name, JSONObject v) {
		mapParams.put(name, v.toString());
		mapCache.put(name, v);
		return this;
	}
	public Params put(String name, JSONArray v) {
		mapParams.put(name, v.toString());
		mapCache.put(name, v);
		return this;
	}
	public String get(int index) {
		if (index < 1)
			return get();
		
		if (urlParams == null) {
			if (StringUtils.isEmpty(urlParam))	
				urlParams = new String[0];
			else
				urlParams = urlParam.split(AppConfig.getConstants().getUrlParamSeparator());
			
			for (int i = 0; i < urlParams.length; i++)
				if ("".equals(urlParams[i]))
					urlParams[i] = null;
		}
		// 从1开始，0是整体
		--index;
		return urlParams.length > index ? urlParams[index] : null;
	}
	public String get(int index, String dv) {
		String v = get(index);
		return StringUtils.isEmpty(v) ? dv : v;
	}
	public int getInt(int index) {
		return getInt(index, 0);
	}
	public int getInt(int index, int dv) {
		return toInt(get(index), dv);
	}
	public long getLong(int index, long dv) {
		return toLong(get(index), dv);
	}
	public long getLong(int index) {
		return getLong(index, 0);
	}
	public double getDouble(int index, double dv) {
		return toDouble(get(index), dv);
	}
	public double getDouble(int index) {
		return getDouble(index, 0);
	}
	private int toInt(String v, int dv) {
		if (v == null)
			return dv;
		return (v.startsWith("N") || v.startsWith("n")) ? 
			-Integer.parseInt(v.substring(1)) : 
				Integer.parseInt(v);
	}
	private long toLong(String v, long dv) {
		if (v == null)
			return dv;
		
		return 	(v.startsWith("N") || v.startsWith("n")) ? 
				-Long.parseLong(v.substring(1)) : 
					Long.parseLong(v);
	}
	private double toDouble(String v, double dv) {
		if (v == null)
			return dv;
		if (v.startsWith("N") || v.startsWith("n"))
			return -Double.parseDouble(v.substring(1));
		return Double.parseDouble(v);
	}
	public String getBase64(String name) {
		return new String(Base64.decode(get(name)));
	}
	public String get(String name, String defaultValue) {
		String v = mapParams.get(name);
		return v != null ? v : defaultValue;
	}
	public int getInt(String name) {
		String strV = get(name);
		if (StringUtils.isEmpty(strV)) {
			throw new RuntimeException("Int参数[" + name + "]为空.");
		}
		return Integer.parseInt(strV);
	}
	public int getInt(String name, int dv) {
		String strV = get(name);
		return StringUtils.isEmpty(strV) ? dv : Integer.parseInt(strV);
	}
	public String[] getValues(String name) {
		return request.getParameterValues(name);
	}
	public int[] getInts(String name) {
		String[] values = request.getParameterValues(name);
		if (values == null)
			return new int[0];
		int[] result = new int[values.length];
		for (int i=0; i<result.length; i++)
			result[i] = Integer.parseInt(values[i]);
		return result;
	}
	public double getDouble(String name) {
		String strV = get(name);
		if (StringUtils.isEmpty(strV)) {
			throw new RuntimeException("Double参数[" + name + "]为空.");
		}
		return Double.parseDouble(strV);
	}
	public double getDouble(String name, double dv) {
		String strV = get(name);
		if (StringUtils.isEmpty(strV)) {
			return dv;
		}
		return Double.parseDouble(strV);
	}
	public double[] getDoubles(String name) {
		String[] values = request.getParameterValues(name);
		if (values == null)
			return new double[0];
		double[] result = new double[values.length];
		for (int i = 0; i < result.length; i++)
			result[i] = Double.parseDouble(values[i]);
		return result;
	}
	/**
	 * Return true if the params value is blank otherwise return false
	 */
	public boolean isEmpty(String name) {
		return StringUtils.isEmpty(get(name));
	}
	
	/**
	 * Return true if the url praram value is blank otherwise return false
	 */
	public boolean isEmpty(int index) {
		return StringUtils.isEmpty(get(index));
	}
	/**
	 * return true if url params exists
	 * @param index
	 * @return
	 */
	public boolean has(int index) {
		return !isEmpty(index);
	}
	/**
	 * return true if params exists
	 * @param name
	 * @return
	 */
	public boolean has(String name) {
		return request.getParameterMap().containsKey(name);
	}
	/**
	 * 获取请求参数中的JSONobject
	 * @param name
	 * @return
	 */
	public JSONObject getJSONObject(String name) {
        // 优先从缓存中获取
        Object joRtn = mapCache.get(name);
        if (joRtn == null) {
        	String strV = get(name);
        	if (!StringUtils.isEmpty(strV))  {
	            try {
					joRtn = new JSONObject(strV);
				} catch (JSONException e) {
	        		throw new ValidateException("JSONObject参数 [" + 
	        				name + "][" + strV + "] 不是合法的JSONObject结构！");
				}
	            mapCache.put(name, joRtn);
        	} else {
        		String nameDot = name + ".";
        		JSONObject joP = new JSONObject();
        		for (String key : mapParams.keySet()) {
        			if (key.startsWith(nameDot)) {
        				try {
							joP.put(key.substring(key.indexOf(".") + 1), mapParams.get(key));
						} catch (JSONException e) {
							e.printStackTrace();
							throw new ValidateException("JSONObject参数 [" + 
									name + "]构造失败，错误信息 [" + e.getMessage() + "]！");
						}
        			}
        		}
        		if (joP.length() > 0) {
        			mapCache.put(name, joRtn = joP);
        		} else {
        			throw new ValidateException("JSONObject参数 [" + name + "] 为空！");
        		}
        	}
        }
		return (JSONObject) joRtn;
	}
	public JSONObject getJSONObject(String name, JSONObject dv) {
		return has(name) ? getJSONObject(name) : dv;
	}
	/**
	 * 将所有请求参数封装为JSONObject
	 * @return
	 */
	public JSONObject getJSONObject() {
		return new JSONObject(mapParams);
	}
	/**
	 * 返回参数Map
	 * @return
	 */
	public Map<String, String> getMap() {
		return mapParams;
	}
	public JSONArray getJSONArray(String name) {
        // 优先从缓存中获取
        Object jaRtn = mapCache.get(name);
        if (jaRtn == null) {
        	String strV = get(name);
        	if (StringUtils.isEmpty(strV)) 
        		throw new ValidateException("JSONArray参数 [" + name + "] 为空！");
            try {
				jaRtn = new JSONArray(strV);
			} catch (JSONException e) {
        		throw new ValidateException("JSONArray参数 [" + 
        				name + "][" + strV + "] 不是合法的JSONArray结构！");
			}
            mapCache.put(name, jaRtn);
        }
		return (JSONArray) jaRtn;
	}
	public JSONArray getJSONArray(String name, JSONArray dv) {
		return has(name) ? getJSONArray(name) : dv;
	}
	public boolean getBoolean(String name) {
		return Boolean.parseBoolean(get(name));
	}
    public HttpSession getSession() {
        return request.getSession(true);
    }
    public HttpServletRequest getRequest() {
        return request;
    }
    public Params setRequest(HttpServletRequest request) {
        this.request = request;
        return this;
    }
    public HttpServletResponse getResponse() {
        return response;
    }
    public Params setResponse(HttpServletResponse response) {
        this.response = response;
        return this;
    }
    public ServletContext getServletContext() {
        return getSession().getServletContext();
    }
    public String getRequestIP() {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
    }
    public String getBasePath() {
		return request.getScheme() + "://" + 
				request.getServerName() + ":" + 
				request.getServerPort() + 
				request.getContextPath()+ "/";
    }
	public Params setUrlParam(String urlParam) {
		if (!StringUtils.isEmpty(urlParam))
			this.urlParam = urlParam;
		return this;
	}
	/**
	 * set request ajax
	 * @param isAjaxRequest
	 * @return
	 */
	public Params setAjaxRequest(boolean isAjaxRequest) {
		this.ajaxRequest = isAjaxRequest;
		return this;
	}
	/**
	 * return true if request is ajax
	 * @return
	 */
	public boolean isAjaxRequest() {
		return this.ajaxRequest;
	}
	/**
	 * set request attribute
	 * @param name
	 * @param obj
	 * @return
	 */
	public Params setAttr(String name, Object obj) {
		request.setAttribute(name, obj);
		return this;
	}
	/**
	 * set session attribute
	 * @param name
	 * @param obj
	 * @return
	 */
	public Params setSessionAttr(String name, Object obj) {
		getSession().setAttribute(name, obj);
		return this;
	}
	/**
	 * set application attribute
	 * @param Object
	 * @return
	 */
	public Params setAppAttr(String name, Object obj) {
		getSession().getServletContext().setAttribute(name, obj);
		return this;
	}
	/**
	 * get session attribute
	 * @param <T>
	 * @param name
	 * @return
	 */
	public <T> T getSessionAttr(String name) {
		HttpSession s = request.getSession(false);
		return s != null ? (T) s.getAttribute(name) : null;
	}
	/**
	 * get requst attribute
	 * @param <T>
	 * @param name
	 * @return
	 */
	public <T> T getAttr(String name) {
		return (T) request.getAttribute(name);
	}
	/**
	 * get application attribute
	 * @param <T>
	 * @param name
	 * @return
	 */
	public <T> T getAppAttr(String name) {
		return (T) getSession().getServletContext().getAttribute(name);
	}
	/**
	 * initial params
	 * @param request
	 * @param response
	 * @return
	 */
	public Params init(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		// 取出前台传来的所有查询参数，装入params
		Enumeration<String> paramNames = request.getParameterNames();
		String ajaxParam = AppConfig.getConstants().getAjaxParam();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			if (ajaxParam.equalsIgnoreCase(paramName)) {
				setAjaxRequest(true);
			} else {
				this.mapParams.put(paramName, request.getParameter(paramName));
			}
		}
		return this;
	}
}
