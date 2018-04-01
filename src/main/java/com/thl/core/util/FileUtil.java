package com.thl.core.util;

import java.io.File;
import java.io.IOException;

import com.thl.core.log.Logger;
import com.thl.core.log.LoggerFactory;

public class FileUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

	// 获取真实名称
	public static String getRealFileName(String filePath) {
		if (StringUtil.isEmpty(filePath)) {
			return null;
		}
		if (filePath.contains("/")) {
			filePath = filePath.substring(filePath.lastIndexOf("/") + 1);
		}
		if (filePath.contains("\\")) {
			filePath = filePath.substring(filePath.lastIndexOf("\\") + 1);
		}
		return filePath;
	}
	// 获取真实名称
	public static String getFilePath(String filePath) {
		if (StringUtil.isEmpty(filePath)) {
			return null;
		}
		if (filePath.contains("/")) {
			filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
		}
		if (filePath.contains("\\")) {
			filePath = filePath.substring(0, filePath.lastIndexOf("\\") + 1);
		} 
		return filePath;
	}

	// 创建文件
	public static File createaFile(String filePath) {
		return createaFile(filePath, false);
		
	}
	// 创建文件
	public static File createaFile(String filePath, boolean isDir) {
		File file = null;
		try {
			file = new File(filePath);
			if (isDir) {
				createDir(file);
			} else {
				String parentDirStr = getFilePath(filePath);
				File parentDir = new File(parentDirStr);
				if (!parentDir.exists()) {
					createDir(parentDir);
				}
				file.createNewFile();
			}
		} catch (Exception e) {
			String message = "create file failure";
			LOGGER.error(message, new IOException(message), true);
		}
		return file;
	}

	public static void createDir(File path) {
		if (path.exists()) {
			LOGGER.log("the path " + path.getPath() + " is exist");
		}
		if (!path.mkdirs()) {
			String message = "create dir failure";
			LOGGER.error(message, new IOException(message), true);
		}
	}

}
