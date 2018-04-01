package com.thl.web.mvc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.thl.core.util.ArrayUtil;
import com.thl.core.util.CollectionUtil;
import com.thl.web.core.ClassHelper;
import com.thl.web.mvc.annotation.Action;

public class ControllerHelper {

	private static final Map<Request, Handler> ACTION_MAP = new HashMap<>();
	
	static {
		Set<Class<?>> controllerSet = ClassHelper.getControllers();
		if (CollectionUtil.isNotEmpty(controllerSet)) {
			for (Class<?> clazz : controllerSet) {
				Method[] methods = clazz.getDeclaredMethods();
				if (ArrayUtil.isNotEmpty(methods)) {
					for (Method method : methods) {
						if (method.isAnnotationPresent(Action.class)) {
							
							Action action = method.getAnnotation(Action.class);
							String requestPath = action.value();
							
							if (clazz.isAnnotationPresent(Action.class)) {
								Action classAction = clazz.getAnnotation(Action.class);
								requestPath = classAction.value() + requestPath;
							}
							
							Request request = new Request(requestPath);
							Handler handler = new Handler(clazz, method);
							ACTION_MAP.put(request, handler);
						}
					}
				}
			}
		}
		
	}
	
	public static Handler getHandler(String requestPath) {
		Request request = new Request(requestPath);
		return ACTION_MAP.get(request);
	}
	
}
