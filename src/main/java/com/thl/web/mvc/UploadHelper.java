package com.thl.web.mvc;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.thl.web.mvc.bean.FileParam;
import com.thl.web.mvc.upload.Upload;
import com.thl.web.mvc.upload.UploadByCommons_FileUpload;

public class UploadHelper {

	private static Upload upload = new UploadByCommons_FileUpload();

	// 初始化操作
	public static void init(ServletContext servletContext) {
		upload.init(servletContext);
	}

	// 判断是否为 multipart 请求
	public static boolean isMultipart(HttpServletRequest request) {
		return upload.isMultipart(request);
	}

	// 创建 Param 对象
	public static Map<String, List<?>> createParamMap(HttpServletRequest request) throws IOException {
		return upload.createParamMap(request);
	}

	// 上传文件
	public static void uploadFile(String basePath, FileParam fileParam) {
		upload.uploadFile(basePath, fileParam);
	}

	// 批量上传
	public static void uploadFile(String basePath, List<FileParam> fileParams) {
		upload.uploadFile(basePath, fileParams);
	}

}
