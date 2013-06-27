package com.shs.framework.core;
import java.io.File;
import com.shs.framework.renderers.ViewType;

/**
 * Global constants definition
 */
public interface Constant {
	
	String VIEW_PATH = "/WEB-INF/views";
	
	ViewType DEFAULT_VIEW_TYPE = ViewType.JSON;
	
	String DEFAULT_ENCODING = "utf-8";
	
	String DEFAULT_URL_PARA_SEPARATOR = "-";
	
	String DEFAULT_FILE_CONTENT_TYPE = "application/octet-stream";
	
	String DEFAULT_JSP_EXTENSION = ".jsp";
	
	String DEFAULT_FREE_MARKER_EXTENSION = ".html";			// The original is ".ftl", Recommend ".html"
	
	// "WEB-INF/download" + File.separator maybe better otherwise it can be downloaded by browser directly
	String DEFAULT_FILE_RENDER_BASE_PATH = File.separator + "download" + File.separator;
	
	int DEFAULT_MAX_POST_SIZE = 1024 * 1024 * 10;  			// Default max post size of multipart request: 10 Meg
	
	String I18N_LOCALE = "__I18N_LOCALE__";					// The i18n name of cookie
	
	int DEFAULT_I18N_MAX_AGE_OF_COOKIE = 999999999;
	
	int DEFAULT_FREEMARKER_TEMPLATE_UPDATE_DELAY = 3600;	// For not devMode only
	
	int DEFAULT_SECONDS_OF_TOKEN_TIME_OUT = 900;			// 900 seconds ---> 15 minutes
	int MIN_SECONDS_OF_TOKEN_TIME_OUT = 300;				// 300 seconds ---> 5 minutes
}







