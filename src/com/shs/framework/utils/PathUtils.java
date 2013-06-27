package com.shs.framework.utils;
import java.io.File;
import java.io.IOException;
import org.apache.commons.lang.StringUtils;

/**
 * new File("..\path\abc.txt") 中的三个方法获取路径的方法
 * 1： getPath() 获取相对路径，例如   ..\path\abc.txt
 * 2： getAbslutlyPath() 获取绝对路径，但可能包含 ".." 或 "." 字符，例如  D:\otherPath\..\path\abc.txt
 * 3： getCanonicalPath() 获取绝对路径，但不包含 ".." 或 "." 字符，例如  D:\path\abc.txt
 */
public class PathUtils {
	
	private static String webRootPath;
	
	@SuppressWarnings("unchecked")
	public static String getPath(Class clazz) {
		return new File(clazz.getResource("").getPath()).getAbsolutePath();
	}
	
	public static String getPath(Object obj) {
		return new File(obj.getClass().getResource("").getPath()).getAbsolutePath();
	}
	
	public static String getRootClassPath() {
		return new File(PathUtils.class.getClassLoader().getResource("").getPath()).getAbsolutePath();
	}
	
	public static String getPackagePath(Object object) {
		Package p = object.getClass().getPackage();
		return p != null ? p.getName().replaceAll("\\.", "/") : "";
	}
	
	public static String getWebRootPath() {
		return webRootPath != null ? webRootPath : (webRootPath = webRoot());
	}
	
	public static void setWebRootPath(String path) {
		if (!StringUtils.isEmpty(path)) {
			if (path.endsWith(File.separator)) {
				webRootPath = path.substring(0, path.length() - 1);
			} else {
				webRootPath = path;
			}
		}
	}
	
	private static String webRoot() {
		try {
			return new File(PathUtils.class.getResource("/").getFile()).getParentFile().getParentFile().getCanonicalPath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}


