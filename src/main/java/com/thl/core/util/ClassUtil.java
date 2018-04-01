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
	
	// 获取类加载器
	public static ClassLoader getClassLoader(){
		return Thread.currentThread().getContextClassLoader();
	}
	
	public static Class<?> loadClass(String className) {
		return loadClass(className, false);
	}
	// 加载类
	public static Class<?> loadClass(String className, boolean isInitialized) {
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className, isInitialized, getClassLoader());
		} catch (ClassNotFoundException e) {
			LOGGER.error("load class fild failure", e, true);
		}
		return clazz;
	}
	
	// 加载指定包下的所有类
	public static Set<Class<?>> getClassSet(String packageName) {
		Set<Class<?>> classSet = new HashSet<>();
		
		try {
			Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".", "/"));
			
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				// 获取文件协议类型
				String protocol = url.getProtocol();
				// 如果是文件
				if (protocol.equals("file")) {
					// 去掉协议，留下单纯的路径
					String packagePath = url.getPath();
					// 将类字节码添加到集合中
					classSet = addClassToSet(packagePath, packageName);
					// 如果是jar包
				} else if (protocol.equals("jar")) {
					// 取得jar 的 url 链接
					JarURLConnection jarUrlConnection = (JarURLConnection) url.openConnection();
					if (jarUrlConnection != null) {
						// 获取 jar 包文件
						JarFile jarFile = jarUrlConnection.getJarFile();
						if (jarFile != null) {
							// 类似于 map ，获取 jar 包文件的 entry ，也就是 jar 包中的所有文件
							Enumeration<JarEntry> jarEntrys = jarFile.entries();
							while (jarEntrys.hasMoreElements()) {
								JarEntry jarEntry = jarEntrys.nextElement();
								// 获取文件名称，格式是 path/path/../fileName.fileType
								String jarEntryName = jarEntry.getName();
								if (jarEntryName.endsWith(".class")) {
									String className = jarEntryName
											// 抽取类名
											.substring(0, jarEntryName.indexOf("."))
											// 将名称中的 '/' 替换成 '.'， 构成包形式的路径
											.replaceAll("/", ".");
									// 添加
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
		// 通过文件过滤器过滤出符合条件的 File 对象
		File[] files = new File(packagePath).listFiles(
				(FileFilter) (file) -> file.isDirectory() 
					|| (file.isFile() && file.getName().endsWith(".class"))
		);
		Set<Class<?>> set = new HashSet<>();
		if (files != null) {
			for (File file : files) {
				String fileName = file.getName();
				// 是文件就获取包和文件添加到集合中
				if (file.isFile()) {
					String className = fileName.substring(0, fileName.indexOf("."));
					if (StringUtil.isNotEmpty(className)) {
						className = packageName + "." + className;
						set.add(loadClass(className));
					}
					
				// 如果是路径，就转换为子路径与子包名，然后继续，（递归）
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
