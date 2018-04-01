package com.thl.web.mvc.bean;

import java.io.InputStream;

public class FileParam {

	// 上传时候 参数的 key
	private String fieldName;
	// 文件名
	private String fileName;
	// 文件大小
	private long fileSize;
	// 协议类型
	private String contentType;
	// 文件输入流
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
