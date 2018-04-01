package com.thl.web.mvc.upload;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.thl.web.mvc.bean.FileParam;

public interface Upload {

	// ��ʼ������
	public void init(ServletContext servletContext);
	
	// �ж��Ƿ�Ϊ multipart ����
	public boolean isMultipart(HttpServletRequest request);
	
	// ���� Param ����
	public Map<String, List<?>> createParamMap(HttpServletRequest request) throws IOException;
	
	// �ϴ��ļ�
	public void uploadFile(String basePath, FileParam fileParam);
	
	// �����ϴ�
	public void uploadFile(String basePath, List<FileParam> fileParams);
	
	
}
