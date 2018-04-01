package com.thl.web.mvc;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thl.core.util.CollectionUtil;
import com.thl.core.util.JSONUtil;
import com.thl.core.util.ReflectionUtil;
import com.thl.web.core.ConfigHelper;
import com.thl.web.core.HelperLoader;
import com.thl.web.ioc.BeanHelper;
import com.thl.web.mvc.bean.Data;
import com.thl.web.mvc.bean.FileParam;
import com.thl.web.mvc.bean.FormParam;
import com.thl.web.mvc.bean.Param;
import com.thl.web.mvc.bean.View;

@WebServlet(urlPatterns = "/*", loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void init(ServletConfig config) throws ServletException {
		// 初始化 一些静态代码块
		HelperLoader.init();
		// 获取 ServletContext 用于注册 Servlet
		ServletContext context = config.getServletContext();
		registerServlet(context);

		// 初始化 上传组件
		UploadHelper.init(context);

		System.out.println("Message From [com.thl] : dispatche start");
	}

	// Service 方法优先于 doGet 和 doPost 方法之前对 request 进行处理的函数，
	// 在处理结束后会转到 doGet 和 doPost 方法
	@SuppressWarnings("unchecked")
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// String requestPath = request.getServletPath();
		request.setCharacterEncoding(ConfigHelper.getEncode());

		String requestPath = request.getPathInfo();
		// System.out.println(requestPath);

		// 去除掉 favicon.ico 请求的 干扰
		if (requestPath.contains("/favicon.ico")) {
			return;
		}

		// 根据路径获取处理器
		Handler handler = ControllerHelper.getHandler(requestPath);

		if (handler == null) {
			response.setStatus(404);
			return;
		}

		// 获取提交的参数
		Map<String, List<?>> paramMap = new HashMap<>();
		if (UploadHelper.isMultipart(request)) {
			paramMap = UploadHelper.createParamMap(request);
		} else {
			paramMap = RequestHelper.createParamMap(request);
		}

		List<FormParam> formParams = (List<FormParam>) paramMap.get("form");
		formParams = addMore(formParams, request, response);
		
		List<FileParam> fileParams = (List<FileParam>) paramMap.get("file");
		
		Param param = new Param(formParams, fileParams);
		
		// 获取 Action 所在的 Controller
		Class<?> controllerClass = handler.getControllerClass();
		Object controllerBean = BeanHelper.getBean(controllerClass);
		// 获取 Action 中的方法
		Method actionMethod = handler.getActionMethod();

		Object result = null;
		// 捕获 Action 方法提交的异常，转移到统一的页面中
		try {
			// 简化 Action 方法，没有需要获取参数时 可以不写参数
			if (actionMethod.getParameterCount() <= 0) {
				result = ReflectionUtil.invokeMethod(controllerBean, actionMethod);
			} else {
				result = ReflectionUtil.invokeMethod(controllerBean, actionMethod, param);
			}
		} catch (Exception e) {
			handleString(ConfigHelper.getExceptionPage(), request, response);
		}

		// 处理返回值
		if (result == null) {
			return;
		}
		if (result instanceof View) {
			handleView((View) result, request, response);
		} else if (result instanceof Data) {
			handleData((Data) result, request, response);
		} else if (result instanceof String) {
			handleString((String) result, request, response);
		}

	}
	
	private List<FormParam> addMore(List<FormParam> formParams, HttpServletRequest request, HttpServletResponse response) {
		FormParam requestParam = new FormParam("javax.servlet.HttpServletRequest", request);
		formParams.add(requestParam);
		FormParam responseParam = new FormParam("javax.servlet.HttpServletResponse", response);
		formParams.add(responseParam);
		return formParams;
	}

	private void handleString(String result, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		if (result.startsWith("/")) {
			String finalPath = request.getContextPath() + result;
			response.sendRedirect(finalPath);
		} else if(result.startsWith("http:")){
			response.sendRedirect(result);
		} else {
			String finalPath = ConfigHelper.getJspPath() + "/" + result;
			request.getRequestDispatcher(finalPath).forward(request, response);
		}
	}

	private void handleData(Data data, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		Object model = data.getModel();
		if (model != null) {
			response.setContentType("application/json");
			response.setCharacterEncoding(ConfigHelper.getEncode());
			String json = JSONUtil.toJSON(model);
			PrintWriter writer = response.getWriter();
			writer.write(json);
			writer.flush();
			writer.close();
		}
	}

	private void handleView(View view, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// 存储 session 域的对象
		Map<String, Object> sessionModel = view.getSessionModel();
		if (CollectionUtil.isNotEmpty(sessionModel)) {
			for (Map.Entry<String, Object> entry : sessionModel.entrySet()) {
				request.getSession().setAttribute(entry.getKey(), entry.getValue());
			}
		}
		// 存储 request 域的对象
		Map<String, Object> requestModel = view.getRequestModel();
		if (CollectionUtil.isNotEmpty(requestModel)) {
			for (Map.Entry<String, Object> entry : requestModel.entrySet()) {
				request.setAttribute(entry.getKey(), entry.getValue());
			}
		}
		String path = view.getPath();
		handleString(path, request, response);
	}

	private void registerServlet(ServletContext context) {
		// 注册处理jsp 的 Servlet
		ServletRegistration jspRegister = context.getServletRegistration("jsp");
		jspRegister.addMapping(ConfigHelper.getJspPath() + "/" + "*");
		jspRegister.addMapping("/index.jsp");
		jspRegister.addMapping("/message.jsp");
		// 注册处理静态资源的 Servlet
		ServletRegistration defaultRegister = context.getServletRegistration("default");
		defaultRegister.addMapping(ConfigHelper.getStaticPath() + "/" + "*");
		defaultRegister.addMapping("*.js");
		defaultRegister.addMapping("*.css");
		defaultRegister.addMapping("*.png");
	}

}
