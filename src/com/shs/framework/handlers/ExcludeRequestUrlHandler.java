package com.shs.framework.handlers;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.shs.framework.config.Constants;

/**
 * @version 0.1
 * @author chyxion
 * @describe: 
 * @date created: Mar 30, 2013 5:03:18 PM
 * @support: chyxion@163.com
 * @date modified: 
 * @modified by: 
 * @copyright: Shenghang Soft All Right Reserved.
 */
public class ExcludeRequestUrlHandler extends AbstractHandler {
	private Logger logger = Logger.getLogger(ExcludeRequestUrlHandler.class);
	private Constants constants;
	public ExcludeRequestUrlHandler(Constants constants) {
		this.setConstants(constants);
	}

	@Override
	public void handle(String target, HttpServletRequest request,
			HttpServletResponse response, boolean[] isHandled) {
		List<String> excludeRequestPartterns = constants.getExcludeReqestParrterns();
		boolean handled = false;
		for (String parttern : excludeRequestPartterns) {
			if (Pattern.compile(parttern).matcher(target).find()) {
				logger.debug("exluded uri request[" + target + "], with parttern[" + parttern + "]");
				handled = true;
				break;
			}
		}
		// 调用后续操纵器
		if (!handled)
			getNextHandler().handle(target, request, response, isHandled);
	}

	public void setConstants(Constants constants) {
		this.constants = constants;
	}

	public Constants getConstants() {
		return constants;
	}
}
