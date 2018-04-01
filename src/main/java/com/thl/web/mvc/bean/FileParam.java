package com.thl.web.mvc.bean;

import java.io.InputStream;

public class FileParam {

	// �ϴ�ʱ�� ������ key
	private String fieldName;
	// �ļ���
	private String fileName;
	// �ļ���С
	private long fileSize;
	// Э������
	private String contentType;
	// �ļ�������
	private InputStream inputStream;
	
	public FileParam(String fieldName, String fileName, long fileSize, String contentType, InputStream inputStream) {
		super();
		this.fieldName = fieldName;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.contentType = contentType;
		this.inputStream = inputStream;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileName() {
		return fileName;
	}

	public long getFileSize() {
		return fileSize;
	}

	public String getContentType() {
		return contentType;
	}

	public InputStream getInputStream() {
		return inputStream;
	}
	
}
