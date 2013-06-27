package com.shs.framework.handlers;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * @version 0.1
 * @author chyxion
 * @describe: 抽象操作器，在BaseConfig中配置需要操纵的action
 * @date created: Mar 30, 2013 3:52:14 PM
 * @support: chyxion@163.com
 * @date modified: 
 * @modified by: 
 * @copyright: Shenghang Soft All Right Reserved.
 */
public abstract class AbstractHandler {
	
	private AbstractHandler nextHandler;
	
	/**
	 * Handle target
	 * @param target url target of this web http request
	 * @param request HttpServletRequest of this http request
	 * @param response HttpServletRequest of this http request
	 * @param isHandled Framework will invoke doFilter() method if isHandled[0] == false,
	 * 			it is usually to tell Filter should handle the static resource.
	 */
	public abstract void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled);

	public void setNextHandler(AbstractHandler nextHandler) {
		this.nextHandler = nextHandler;
	}

	public AbstractHandler getNextHandler() {
		return nextHandler;
	}
}




