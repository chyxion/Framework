package com.shs.framework.utils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
/**
 * @version 0.1
 * @author chyxion
 * @describe: 日期组件
 * @date created: Jan 23, 2013 2:19:10 PM
 * @support: chyxion@163.com
 * @date modified: 
 * @modified by: 
 * @copyright: Shenghang Soft All Right Reserved.
 */
public class DateUtils {
	/**
	 * 当前日期时间
	 * @return
	 */
	public static String nowDefault() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	/**
	 * 14位现在日期时间
	 */
	public static String now14() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}
	/**
	 * 8位当前日期
	 */
	public static String now8() {
		return new SimpleDateFormat("yyyyMMdd").format(new Date());
	}
	/**
	 * 返回当前时间
	 * 如 140356
	 * @return
	 */
	public static String nowTime() {
		return new SimpleDateFormat("HHmmss").format(new Date());
	}
	/**
	 * 格式化日期
	 * @param date
	 * @param format
	 * @return
	 */
	public static String format(Date date, String format) {
		return new SimpleDateFormat(format).format(date);
	}
	/**
	 * 当前时分
	 * @return
	 */
	public static String nowHHmm() {
		return new SimpleDateFormat("HHmm").format(new Date());
	}
	/**
	 * 当前分秒
	 * @return
	 */
	public static String nowmmss() {
		return new SimpleDateFormat("mmss").format(new Date());
	}
	/**
	 * 今天
	 * @return
	 */
	public static String today() {
		return new SimpleDateFormat("yyyyMMdd").format(new Date());
	}
	/**
	 * 本年月
	 * @return
	 */
	public static String now6(){
		return new SimpleDateFormat("yyyyMM").format(new Date());
	}
	/**
	 * 本年
	 * @return
	 */
	public static String thisYear() {
		return new SimpleDateFormat("yyyy").format(new Date());
	}
	/**
	 * 本月，只是月，没有年，比如06
	 * @return
	 */
	public static String thisMonth() {
		return new SimpleDateFormat("MM").format(new Date());
	}
	/**
	 * 日期，比如28
	 * @return
	 */
	public static String thisDay() {
		return new SimpleDateFormat("dd").format(new Date());
	}
	/**
	 * 本月
	 * @return
	 */
	public static String thisYearMonth() {
		return now6(); 
	}
	/**
	 * 将20120403140103格式的日期转换成 2012-04-03 14:01:03
	 * @param date
	 * @return
	 */
	public static String formatDefault(String date) throws Exception {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("yyyyMMddHHmmss").parse(date));
	}
	/** 
     * 获得本周一的日期 
     *   
     * @return 
     */  
    public static String getMondayOfWeek(int days) {    
        int mondayPlus = getMondayPlus();    
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(Calendar.DATE, days);
        currentDate.add(GregorianCalendar.DATE, mondayPlus);    
        Date monday = currentDate.getTime();    
  
        return new SimpleDateFormat("yyyyMMdd").format(monday);        
    }  
    
    /** 
     * 获得本周星期日的日期 
     * @return 
     */  
    public static String getSundayOfWeek(int days) {    
        int mondayPlus = getMondayPlus();    
        GregorianCalendar currentDate = new GregorianCalendar(); 
        currentDate.add(Calendar.DATE, days);
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 6);    
        Date sunday = currentDate.getTime();    
        return new SimpleDateFormat("yyyyMMdd").format(sunday);    
    }
    /**
     * 获取当前时间为本年的第几周
     * @return
     */
	public static int getWeekOfYear(){
		return Calendar.getInstance(TimeZone.getDefault()).get(Calendar.WEEK_OF_YEAR);
    }
    /** 
     * 获得当前日期与本周日相差的天数 
     * @return 
     */  
    private static int getMondayPlus() {    
        Calendar cd = Calendar.getInstance();    
        // 获得今天是一周的第几天，星期日是第一天，星期二是第二天......    
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1; // 因为按中国礼拜一作为第一天所以这里减1    
        //如果今天不是周日，则取本周的起始日期;
        //如果今天是周日，则通过-7来取上周的起始日期。
        if(dayOfWeek != 0){
        	if (dayOfWeek == 1) {    
 	            return 0;    
 	        } else {    
 	            return 1 - dayOfWeek;    
 	        }  
        }else{
        	if (dayOfWeek == 1) {    
 	            return 0 - 7;    
 	        } else {    
 	            return 1 - dayOfWeek - 7;    
 	        } 
        }   
    }
    public static Date addMinutes(Date date, int amount) {
		return org.apache.commons.lang.time.DateUtils.addMinutes(date, amount);
    }
    public static Date addHours(Date date, int amount) {
		return org.apache.commons.lang.time.DateUtils.addHours(date, amount);
    }
	public static Date addDays(Date date, int amount) {
		return org.apache.commons.lang.time.DateUtils.addDays(date, amount);
	}  
	public static Date addWeeks(Date date, int amount) {
		return org.apache.commons.lang.time.DateUtils.addWeeks(date, amount);
	}
	public static Date addMonths(Date date, int amount) {
		return org.apache.commons.lang.time.DateUtils.addMonths(date, amount);
	}
	public static Date addYears(Date date, int amount) {
		return org.apache.commons.lang.time.DateUtils.addYears(date, amount);
	}
}
