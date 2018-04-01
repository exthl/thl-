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

	// ��ȡ���� service
	public static Set<Class<?>> getServices() {
		return getClassSetByAnnotation(Service.class);
	}

	// ��ȡ���� controller
	public static Set<Class<?>> getControllers() {
		return getClassSetByAnnotation(Controller.class);
	}

	// ��ȡ���� repository
	public static Set<Class<?>> getRepositories() {
		return getClassSetByAnnotation(Repository.class);
	}

	// ��ȡ���� bean
	public static Set<Class<?>> getBeans() {
		return getClassSetByAnnotation(Bean.class);
	}

	// ��ȡĳ���ࣨ�ӿڣ����������ࣨʵ���ࣩ
	public static Set<Class<?>> getClassSetBySupper(Class<?> superClass) {
		Set<Class<?>> classSet = new HashSet<>();
		for (Class<?> clazz : CLASS_SET) {
			if (superClass.isAssignableFrom(clazz) && !superClass.equals(clazz)) {
				classSet.add(clazz);
			}
		}
		return classSet;
	}

	// ��ȡ���ĳ��ע�����
	public static Set<Class<?>> getClassSetByAnnotation(Class<? extends Annotation> annotationClass) {
		Set<Class<?>> classSet = new HashSet<>();
		for (Class<?> clazz : CLASS_SET) {
			if (clazz.isAnnotationPresent(annotationClass)) {
				classSet.add(clazz);
			}
		}
		return classSet;
	}

	// ��ȡ����Ӧ����
	public static Set<Class<?>> getBeanClassSet() {
		Set<Class<?>> beanClassSet = new HashSet<>();
		beanClassSet.addAll(getServices());
		beanClassSet.addAll(getControllers());
		beanClassSet.addAll(getRepositories());
		beanClassSet.addAll(getBeans());
		return beanClassSet;
	}

}
