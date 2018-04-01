package com.thl.web.mvc.upload;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.thl.web.mvc.bean.FileParam;

public interface Upload {

	// 初始化操作
	public void init(ServletContext servletContext);
	
	// 判断是否为 multipart 请求
	public boolean isMultipart(HttpServletRequest request);
	
	// 创建 Param 对象
	public Map<String, List<?>> createParamMap(HttpServletRequest request) throws IOException;
	
	// 上传文件
	public void uploadFile(String basePath, FileParam fileParam);
	
	// 批量上传
	public void uploadFile(String basePath, List<FileParam> fileParams);
	
	
}
