package com.shs.framework.core;
import java.lang.reflect.Field;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import com.shs.framework.config.Constants;
import com.shs.framework.dao.IEventHandler;
import com.shs.framework.handlers.AbstractHandler;
import com.shs.framework.renderers.Error404Exception;
import com.shs.framework.renderers.Error500Exception;
import com.shs.framework.renderers.Renderer;
import com.shs.framework.renderers.RendererFactory;

/**
 * ActionHandler
 */
final class ActionHandler extends AbstractHandler {
	
	private final boolean devMode;
	private final Class<? extends IEventHandler> eventHandlerClass;
	private final ActionMapping actionMapping;
	private static final RendererFactory renderFactory = RendererFactory.me();
	private static final Logger logger = Logger.getLogger(ActionHandler.class);
	
	public ActionHandler(ActionMapping actionMapping, Constants constants) {
		this.actionMapping = actionMapping;
		this.devMode = constants.getDevMode();
		eventHandlerClass = constants.getDAOEventClass();
	}
	
	/**
	 * handle
	 * 1: Action action = actionMapping.getAction(target)
	 * 2: new ActionInvocation(...).invoke()
	 * 3: render(...)
	 */
	public final void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		if (target.contains(".")) {
			return;
		}
		isHandled[0] = true;
		String[] urlParams = {null};
		Action action = actionMapping.getAction(target, urlParams);
		
		if (action == null) {
			String qs = request.getQueryString();
			logger.warn("Action not found: " + (qs == null ? target : target + "?" + qs));
			renderFactory.getError404Renderer().setContext(request, response).render();
			return;
		}
		
		try {
			BaseController controller = action.getControllerClass().newInstance();
			// 初始化控制器
			controller.init(request, response, urlParams[0]);
			// 如果有服务域，注入
			for (Field field : action.getControllerClass().getDeclaredFields()) {
				field.setAccessible(true);
				Class<?> fc = field.getType();
				if (fc.getGenericSuperclass() == BaseService.class && field.get(controller) == null) {
					BaseService service = ((BaseService) fc.newInstance()).setParams(controller.getParams());
					// 设置dao事件
					if (eventHandlerClass != null) {
						service.dao.setEventHandler(eventHandlerClass.newInstance().setExtraParam(controller.getParams()));
					}
					field.set(controller, service);
				}
			}
			if (devMode) {
				boolean isMultipartRequest = ActionReporter.reportCommonRequest(controller, action);
				if (isMultipartRequest) ActionReporter.reportMultipartRequest(controller, action);
			}
			
			new ActionInvocation(action, controller).invoke();
			Renderer render = controller.getRender();
			if (render instanceof ActionRender) {
				String actionURL = ((ActionRender)render).getActionURL();
				if (target.equals(actionURL))
					throw new RuntimeException("The forward action url is the same as before.");
				else
					handle(actionURL, request, response, isHandled);
				return;
			}
			
			if (render == null)
				render = renderFactory.getDefaultRenderer(action.getViewPath() + action.getMethodName());
			render.setContext(request, response, action.getViewPath()).render();
		} catch (Error404Exception e) {
			String qs = request.getQueryString();
			logger.warn("Resource not found: " + (qs == null ? target : target + "?" + qs));
			e.getError404Render().setContext(request, response).render();
		} catch (Error500Exception e) {
			String qs = request.getQueryString();
			logger.error(qs == null ? target : target + "?" + qs, e);
			e.getError500Render().setContext(request, response).render();
			e.printStackTrace();
		} catch (Exception e) {
			try {
				Throwable c = e.getCause();
				renderFactory.getTextRender(
					new JSONObject()
				.put("success", false)
				.put("message", c != null ? c.getMessage() : e.getMessage())
				.toString()).setContext(request, response).render();
			} catch (JSONException je) {
				logger.error(je);
			}
			e.printStackTrace();
			String qs = request.getQueryString();
			logger.error(qs == null ? target : target + "?" + qs, e);
			logger.error(e);
		}
	}
}





