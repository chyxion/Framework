package com.shs.framework.core;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import com.shs.framework.aop.IActionInterceptor;

/**
 * ActionReporter
 */
final class ActionReporter {
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * Report action before action invoking when the common request coming
	 */
	static final boolean reportCommonRequest(BaseController controller, Action action) {
		String content_type = controller.getRequest().getContentType();
		if (content_type == null || content_type.toLowerCase().indexOf("multipart") == -1) {	// if (content_type == null || content_type.indexOf("multipart/form-data") == -1) {
			doReport(controller, action);
			return false;
		}
		return true;
	}
	
	/**
	 * Report action after action invoking when the multipart request coming
	 */
	static final void reportMultipartRequest(BaseController controller, Action action) {
		doReport(controller, action);
	}
	
	private static final void doReport(BaseController controller, Action action) {
		StringBuffer sb = new StringBuffer("\nAction Report -------- ")
			.append(sdf.format(new Date())).append(" ------------------------------\n");
		Class<? extends BaseController> cc = action.getControllerClass();
		sb.append("Controller  : ").append(cc.getName()).append(".(").append(cc.getSimpleName()).append(".java:1)");
		sb.append("\nMethod      : ").append(action.getMethodName()).append("\n");
		
		String urlParam = controller.getParams().get();
		if (urlParam != null) {
			sb.append("UrlParam     : ").append(urlParam).append("\n");
		}
		
		IActionInterceptor[] inters = action.getInterceptors();
		if (inters.length > 0) {
			sb.append("Interceptor : ");
			for (int i=0; i<inters.length; i++) {
				if (i > 0)
					sb.append("\n              ");
				IActionInterceptor inter = inters[i];
				Class<? extends IActionInterceptor> ic = inter.getClass();
				sb.append(ic.getName()).append(".(").append(ic.getSimpleName()).append(".java:1)");
			}
			sb.append("\n");
		}
		
		// print all parameters
		HttpServletRequest request = controller.getRequest();
		Enumeration<String> e = request.getParameterNames();
		if (e.hasMoreElements()) {
			sb.append("Parameter   : ");
			while (e.hasMoreElements()) {
				String name = e.nextElement();
				String[] values = request.getParameterValues(name);
				if (values.length == 1) {
					sb.append(name).append("=").append(values[0]);
				}
				else {
					sb.append(name).append("[]={");
					for (int i=0; i<values.length; i++) {
						if (i > 0)
							sb.append(",");
						sb.append(values[i]);
					}
					sb.append("}");
				}
				sb.append("  ");
			}
			sb.append("\n");
		}
		sb.append("--------------------------------------------------------------------------------\n");
		System.out.print(sb);
	}
}
