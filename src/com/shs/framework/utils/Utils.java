package com.shs.framework.utils;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
/**
 * @class describe: 
 * @version 0.1
 * @date created: Apr 5, 2012 10:40:17 AM
 * @author chyxion
 * @support: chyxion@163.com
 * @date modified: 
 * @modified by: 
 * @copyright: Shenghang Soft All Right Reserved.
 */
public class Utils {
	/**
	 * 删除字符串中的空白符
	 * @param str
	 * @return
	 */
	public static String deleteBlank(String str) {
		return Pattern.compile("\\s*|\t|\r|\n").matcher(str).replaceAll("");
	}
	/**
	 * 解密base64
	 */
	public static String decodeBase64(String value){
		return new String(Base64.decode(value));
	}
	/**
	 * 加密base64
	 * @param value
	 * @return
	 */
	public static String encodeBase64(String value){
		return deleteBlank(Base64.encode(value.getBytes()));
	}

    /**
     * MD5加密
     * @param password
     * @return 返回加密后的结果
     */
	public static String encrypt(String content) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update((content).getBytes("utf-8"));
			StringBuffer sbReturn = new StringBuffer();
			
			for (byte b : md.digest()) {
				sbReturn.append(Integer.toString((b & 0xff) + 0x100, 16)
						.substring(1));
			}
			return sbReturn.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
    public static String basePath(HttpServletRequest request) {
		return request.getScheme() + "://" + 
				request.getServerName() + ":" + 
				request.getServerPort() + 
				request.getContextPath()+ "/";
    }
}
