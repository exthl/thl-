package com.thl.core.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.thl.core.log.Logger;
import com.thl.core.log.LoggerFactory;

public class ClassUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClassUtil.class);
	
	// ��ȡ�������
	public static ClassLoader getClassLoader(){
		return Thread.currentThread().getContextClassLoader();
	}
	
	public static Class<?> loadClass(String className) {
		return loadClass(className, false);
	}
	// ������
	public static Class<?> loadClass(String className, boolean isInitialized) {
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className, isInitialized, getClassLoader());
		} catch (ClassNotFoundException e) {
			LOGGER.error("load class fild failure", e, true);
		}
		return clazz;
	}
	
	// ����ָ�����µ�������
	public static Set<Class<?>> getClassSet(String packageName) {
		Set<Class<?>> classSet = new HashSet<>();
		
		try {
			Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".", "/"));
			
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				// ��ȡ�ļ�Э������
				String protocol = url.getProtocol();
				// ������ļ�
				if (protocol.equals("file")) {
					// ȥ��Э�飬���µ�����·��
					String packagePath = url.getPath();
					// �����ֽ�����ӵ�������
					classSet = addClassToSet(packagePath, packageName);
					// �����jar��
				} else if (protocol.equals("jar")) {
					// ȡ��jar �� url ����
					JarURLConnection jarUrlConnection = (JarURLConnection) url.openConnection();
					if (jarUrlConnection != null) {
						// ��ȡ jar ���ļ�
						JarFile jarFile = jarUrlConnection.getJarFile();
						if (jarFile != null) {
							// ������ map ����ȡ jar ���ļ��� entry ��Ҳ���� jar ���е������ļ�
							Enumeration<JarEntry> jarEntrys = jarFile.entries();
							while (jarEntrys.hasMoreElements()) {
								JarEntry jarEntry = jarEntrys.nextElement();
								// ��ȡ�ļ����ƣ���ʽ�� path/path/../fileName.fileType
								String jarEntryName = jarEntry.getName();
								if (jarEntryName.endsWith(".class")) {
									String className = jarEntryName
											// ��ȡ����
											.substring(0, jarEntryName.indexOf("."))
											// �������е� '/' �滻�� '.'�� ���ɰ���ʽ��·��
											.replaceAll("/", ".");
									// ���
									classSet.add(loadClass(className));
								}
							}
						}
						
					}
				}
			}
			
			
		} catch (IOException e) {
			LOGGER.error("load class set from \"" + packageName + "\" failure", e, true);
		}
		
		return classSet;
	}
	
	private static Set<Class<?>> addClassToSet(String packagePath, String packageName) {
		// ͨ���ļ����������˳����������� File ����
		File[] files = new File(packagePath).listFiles(
				(FileFilter) (file) -> file.isDirectory() 
					|| (file.isFile() && file.getName().endsWith(".class"))
		);
		Set<Class<?>> set = new HashSet<>();
		if (files != null) {
			for (File file : files) {
				String fileName = file.getName();
				// ���ļ��ͻ�ȡ�����ļ���ӵ�������
				if (file.isFile()) {
					String className = fileName.substring(0, fileName.indexOf("."));
					if (StringUtil.isNotEmpty(className)) {
						className = packageName + "." + className;
						set.add(loadClass(className));
					}
					
				// �����·������ת��Ϊ��·�����Ӱ�����Ȼ����������ݹ飩
				} else if (file.isDirectory()) {
					String subPackagePath = fileName;
					if (StringUtil.isNotEmpty(subPackagePath)) {
						subPackagePath = packagePath + "/" + subPackagePath;
					}
					String subPackageName = fileName;
					if (StringUtil.isNotEmpty(subPackageName)) {
						subPackageName = packageName + "." + subPackageName;
					}
					set.addAll(addClassToSet(subPackagePath, subPackageName));
				}
			}
		}
		return set;
	}

	
}
