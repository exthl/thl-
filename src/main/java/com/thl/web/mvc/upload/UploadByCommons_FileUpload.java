package com.thl.web.mvc.upload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.thl.core.log.Logger;
import com.thl.core.log.LoggerFactory;
import com.thl.core.util.CollectionUtil;
import com.thl.core.util.FileUtil;
import com.thl.core.util.StreamUtil;
import com.thl.core.util.StringUtil;
import com.thl.web.core.ConfigHelper;
import com.thl.web.mvc.bean.FileParam;
import com.thl.web.mvc.bean.FormParam;

public class UploadByCommons_FileUpload implements Upload {

	private static final Logger LOGGER = LoggerFactory.getLogger(Upload.class);
	private static ServletFileUpload servletFileUpload;

	@Override
	public void init(ServletContext servletContext) {
		// 获取服务器中的存储目录
		File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");

		// 创建 ServletFileUpload 对象
		servletFileUpload = new ServletFileUpload( //
				new DiskFileItemFactory(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD, repository));
		// 获取上传 大小限制
		int uploadLimit = ConfigHelper.getUploadLimit();

		// 设置
		if (uploadLimit > 0) {
			servletFileUpload.setSizeMax(uploadLimit * 1024 * 1024);
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public boolean isMultipart(HttpServletRequest request) {
		return servletFileUpload.isMultipartContent(request);
	}

	@Override
	public Map<String, List<?>> createParamMap(HttpServletRequest request) throws IOException {
		Map<String, List<?>> paramMap = new HashMap<>();
		List<FormParam> formParamList = new ArrayList<>();
		List<FileParam> fileParamList = new ArrayList<>();
		try {
			Map<String, List<FileItem>> fileItemListMap = servletFileUpload.parseParameterMap(request);
			if (CollectionUtil.isEmpty(fileItemListMap)) {
				return paramMap;
			}
			for (Map.Entry<String, List<FileItem>> entry : fileItemListMap.entrySet()) {
				String fieldName = entry.getKey();
				List<FileItem> fileItemList = entry.getValue();
				if (CollectionUtil.isEmpty(fileItemList)) {
					continue;
				}
				for (FileItem fileItem : fileItemList) {
					if (fileItem.isFormField()) {
						String fieldValue = fileItem.getString("UTF-8");
						formParamList.add(new FormParam(fieldName, fieldValue));
					} else {
						String fileName = FileUtil//
								.getRealFileName(new String(fileItem.getName().getBytes(), "UTF-8"));
						if (StringUtil.isNotEmpty(fileName)) {
							long fileSize = fileItem.getSize();
							String contentType = fileItem.getContentType();
							InputStream inputStream = fileItem.getInputStream();
							FileParam fileParam = new FileParam(fieldName, fileName, fileSize, contentType,
									inputStream);
							fileParamList.add(fileParam);
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("create param failure", e, true);
		}
		paramMap = new HashMap<>();
		paramMap.put("file", fileParamList);
		paramMap.put("form", formParamList);
		return paramMap;
	}

	@Override
	public void uploadFile(String basePath, FileParam fileParam) {
		String message = "upload file failure";
		if (fileParam == null) {
			LOGGER.error(message, new Exception(message), true);
		}

		try {
			String filePath = basePath + "\\" + fileParam.getFileName();
			// 创建文件
			FileUtil.createaFile(filePath);
			InputStream inputStream = new BufferedInputStream(fileParam.getInputStream());
			OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filePath));
			StreamUtil.copyStream(inputStream, outputStream);
		} catch (FileNotFoundException e) {
			LOGGER.error(message, new Exception(message), true);
		}

	}

	@Override
	public void uploadFile(String basePath, List<FileParam> fileParams) {
		if (CollectionUtil.isNotEmpty(fileParams)) {
			for (FileParam fileParam : fileParams) {
				uploadFile(basePath, fileParam);
			}
		}
	}

}
