package com.thl.web.mvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.thl.core.util.ArrayUtil;
import com.thl.core.util.StreamUtil;
import com.thl.core.util.StringUtil;
import com.thl.core.util.URLUtil;
import com.thl.web.mvc.bean.FormParam;

public class RequestHelper {

	public static Map<String, List<?>> createParamMap(HttpServletRequest request) throws IOException {
		List<FormParam> formParams = new ArrayList<>();
		formParams.addAll(parseParameters(request));
		formParams.addAll(parseInputStream(request));
		Map<String, List<?>> paramMap = new HashMap<>();
		paramMap.put("form", formParams);
		paramMap.put("file", new ArrayList<>());
		return paramMap;
	}
	
	private static List<FormParam> parseParameters(HttpServletRequest request) {
		List<FormParam> formParams = new ArrayList<>();
		// ��ȡ POST ��ʽ�����еĲ��� �� ��������
		// ʵ���֪��GET �� POST ��ʽ�ύ�Ĳ��� ���������ﱻ��ȡ��
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			String paramValue = request.getParameter(paramName);
			// System.out.println("[DispatcherServlet] " + paramName + " : " + paramValue);
			FormParam formParam = new FormParam(paramName, paramValue);
			formParams.add(formParam);
		}
		
		return formParams;
	}

	private static List<FormParam>  parseInputStream(HttpServletRequest request) throws IOException {
		List<FormParam> formParams = new ArrayList<>();
		// ��ȡ GET ��ʽ�����еĲ��� �� ��������
		// δ֪
		String requestBody = URLUtil.decodeURL(StreamUtil.getString(request.getInputStream()));
		if (StringUtil.isNotEmpty(requestBody)) {
			String[] params = requestBody.split("&");
			if (ArrayUtil.isNotEmpty(params)) {
				for (String param : params) {
					String paramName = param.split("=")[0];
					String paramValue = param.split("=")[1];
					// System.out.println("[DispatcherServlet] " + paramName + " : " + paramValue);
					FormParam formParam = new FormParam(paramName, paramValue);
					formParams.add(formParam);
				}
			}
		}
		return formParams;
	}

}
