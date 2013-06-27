package com.shs.framework.config;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;

import com.shs.framework.utils.PathUtils;
public abstract class AbstractConfig {
	
	/**
	 * Config constant
	 */
	public abstract void configConstant(Constants constants);
	
	/**
	 * Config plugin
	 */
	public abstract void configPlugin(Plugins plugins);
	
	/**
	 * Config interceptor applied to all actions.
	 */
	public abstract void configInterceptor(Interceptors interceptors);
	
	/**
	 * Config handler
	 */
	public abstract void configHandler(Handlers handlers);
	
	/**
	 * Call back after start
	 */
	public void afterStart(){};
	
	/**
	 * Call back before stop
	 */
	public void beforeStop(){};
	
	private Properties properties;
	
	/**
	 * Load property file
	 * Example: loadPropertyFile("db_username_pass.txt");
	 * @param file the file in WEB-INF directory
	 */
	public Properties loadPropertyFile(String file) {
		if (StringUtils.isEmpty(file))
			throw new IllegalArgumentException("Parameter of file can not be blank");
		if (file.contains(".."))
			throw new IllegalArgumentException("Parameter of file can not contains \"..\"");
		
		InputStream inputStream = null;
		String fullFile;	// String fullFile = PathUtil.getWebRootPath() + file;
		if (file.startsWith(File.separator))
			fullFile = PathUtils.getWebRootPath() + File.separator + "WEB-INF" + file;
		else
			fullFile = PathUtils.getWebRootPath() + File.separator + "WEB-INF" + File.separator + file;
		
		try {
			inputStream = new FileInputStream(new File(fullFile));
			Properties p = new Properties();
			p.load(inputStream);
			properties = p;
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Properties file not found: " + fullFile);
		} catch (IOException e) {
			throw new IllegalArgumentException("Properties file can not be loading: " + fullFile);
		}
		finally {
			try {if (inputStream != null) inputStream.close();} catch (IOException e) {e.printStackTrace();}
		}
		if (properties == null)
			throw new RuntimeException("Properties file loading failed: " + fullFile);
		return properties;
	}
	
	public String getProperty(String key) {
		checkPropertyLoading();
		return properties.getProperty(key);
	}
	
	public String getProperty(String key, String defaultValue) {
		checkPropertyLoading();
		return properties.getProperty(key, defaultValue);
	}
	
	public Integer getPropertyToInt(String key) {
		checkPropertyLoading();
		Integer resultInt = null;
		String resultStr = properties.getProperty(key);
		if (resultStr != null)
			resultInt =  Integer.parseInt(resultStr);
		return resultInt;
	}
	
	public Integer getPropertyToInt(String key, Integer defaultValue) {
		Integer result = getPropertyToInt(key);
		return result != null ? result : defaultValue;
	}
	
	public Boolean getPropertyToBoolean(String key) {
		checkPropertyLoading();
		String resultStr = properties.getProperty(key);
		Boolean resultBool = null;
		if (resultStr != null) {
			if (resultStr.trim().equalsIgnoreCase("true"))
				resultBool = true;
			else if (resultStr.trim().equalsIgnoreCase("false"))
				resultBool = false;
		}
		return resultBool;
	}
	
	public Boolean getPropertyToBoolean(String key, boolean defaultValue) {
		Boolean result = getPropertyToBoolean(key);
		return result != null ? result : defaultValue;
	}
	
	private void checkPropertyLoading() {
		if (properties == null)
			throw new RuntimeException("You must load properties file by invoking loadPropertyFile(String) method in configConstant(Constants) method before.");
	}
}









