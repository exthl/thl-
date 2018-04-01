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

	// ��ʼ������
	public static void init(ServletContext servletContext) {
		upload.init(servletContext);
	}

	// �ж��Ƿ�Ϊ multipart ����
	public static boolean isMultipart(HttpServletRequest request) {
		return upload.isMultipart(request);
	}

	// ���� Param ����
	public static Map<String, List<?>> createParamMap(HttpServletRequest request) throws IOException {
		return upload.createParamMap(request);
	}

	// �ϴ��ļ�
	public static void uploadFile(String basePath, FileParam fileParam) {
		upload.uploadFile(basePath, fileParam);
	}

	// �����ϴ�
	public static void uploadFile(String basePath, List<FileParam> fileParams) {
		upload.uploadFile(basePath, fileParams);
	}

}
