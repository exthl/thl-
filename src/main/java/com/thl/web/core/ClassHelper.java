package com.thl.web.core;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import com.thl.core.util.ClassUtil;
import com.thl.web.annotation.Controller;
import com.thl.web.annotation.Repository;
import com.thl.web.annotation.Service;
import com.thl.web.ioc.Bean;

public class ClassHelper {

	private static final Set<Class<?>> CLASS_SET;

	static {
		CLASS_SET = ClassUtil.getClassSet(ConfigHelper.getBasePackage());
	}

	public static Set<Class<?>> getClassSet() {
		return CLASS_SET;
	}

	// 获取所有 service
	public static Set<Class<?>> getServices() {
		return getClassSetByAnnotation(Service.class);
	}

	// 获取所有 controller
	public static Set<Class<?>> getControllers() {
		return getClassSetByAnnotation(Controller.class);
	}

	// 获取所有 repository
	public static Set<Class<?>> getRepositories() {
		return getClassSetByAnnotation(Repository.class);
	}

	// 获取所有 bean
	public static Set<Class<?>> getBeans() {
		return getClassSetByAnnotation(Bean.class);
	}

	// 获取某父类（接口）的所有子类（实现类）
	public static Set<Class<?>> getClassSetBySupper(Class<?> superClass) {
		Set<Class<?>> classSet = new HashSet<>();
		for (Class<?> clazz : CLASS_SET) {
			if (superClass.isAssignableFrom(clazz) && !superClass.equals(clazz)) {
				classSet.add(clazz);
			}
		}
		return classSet;
	}

	// 获取标记某个注解的类
	public static Set<Class<?>> getClassSetByAnnotation(Class<? extends Annotation> annotationClass) {
		Set<Class<?>> classSet = new HashSet<>();
		for (Class<?> clazz : CLASS_SET) {
			if (clazz.isAnnotationPresent(annotationClass)) {
				classSet.add(clazz);
			}
		}
		return classSet;
	}

	// 获取所有应用类
	public static Set<Class<?>> getBeanClassSet() {
		Set<Class<?>> beanClassSet = new HashSet<>();
		beanClassSet.addAll(getServices());
		beanClassSet.addAll(getControllers());
		beanClassSet.addAll(getRepositories());
		beanClassSet.addAll(getBeans());
		return beanClassSet;
	}

}
