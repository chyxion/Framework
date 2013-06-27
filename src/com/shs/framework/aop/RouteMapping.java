package com.shs.framework.aop;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * @version 0.1
 * @author chyxion
 * @describe: 路由绑定
 * @date created: Apr 15, 2013 10:09:20 AM
 * @support: chyxion@163.com
 * @date modified: 
 * @modified by: 
 * @copyright: Shenghang Soft All Right Reserved.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RouteMapping {
    String controller();
    String view() default "";
}
