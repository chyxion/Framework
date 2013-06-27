package com.shs.framework.aop;
import com.shs.framework.core.ActionInvocation;
/**
 * @version 0.1
 * @author chyxion
 * @describe: 动作拦截接口
 * @date created: Mar 30, 2013 3:57:00 PM
 * @support: chyxion@163.com
 * @date modified: 
 * @modified by: 
 * @copyright: Shenghang Soft All Right Reserved.
 */
public interface IActionInterceptor {
	void intercept(ActionInvocation ai);
}
