package com.thl.web.mvc.bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;

import com.thl.core.util.CollectionUtil;
import com.thl.core.util.DateUtil;
import com.thl.core.util.EnumUtil;
import com.thl.core.util.StringUtil;

public class Param {

	private List<FormParam> formParams;
	private List<FileParam> fileParams;

	public Param() {
		this.formParams = new ArrayList<>();
		this.fileParams = new ArrayList<>();
	}

	public Param(List<FormParam> formParams) {
		this.formParams = formParams;
		this.fileParams = new ArrayList<>();
	}

	public Param(List<FormParam> formParams, List<FileParam> fileParams) {
		this.formParams = formParams;
		this.fileParams = fileParams;
	}

	// 获取表单字段
	public Map<String, Object> getFormMap() {
		Map<String, Object> formMap = new HashMap<>();
		if (CollectionUtil.isNotEmpty(this.formParams)) {
			for (FormParam formParam : this.formParams) {
				String key = formParam.getFieldName();
				Object value = formParam.getFieldValue();
				if (formMap.containsKey(key)) {
					value = formMap.get(key) + StringUtil.SPEARATOR + value;
				}
				formMap.put(key, value);

			}
		}
		return formMap;
	}

	// 获取文件字段
	public Map<String, List<FileParam>> getFileMap() {
		Map<String, List<FileParam>> fileMap = new HashMap<>();
		if (CollectionUtil.isNotEmpty(this.fileParams)) {
			for (FileParam fileParam : fileParams) {
				String fieldName = fileParam.getFieldName();
				List<FileParam> fileParamList;
				if (fileMap.containsKey(fieldName)) {
					fileParamList = fileMap.get(fieldName);
				} else {
					fileParamList = new ArrayList<>();
				}
				fileParamList.add(fileParam);
				fileMap.put(fieldName, fileParamList);
			}
		}
		return fileMap;
	}

	// -------------------------------------------------------

	// 获取由表单字段构成的对象，如：user.id 和 user.name 构成的 User 对象
	// 使用 Common BeanUtil 包 中的 BeanUtils 的方法
	public <T> T getObject(Class<T> clazz) {
		T t = null;
		try {
			t = clazz.newInstance();
			for (Map.Entry<String, Object> entry : getFormMap().entrySet()) {
				String key = entry.getKey();
				// System.out.println(key + " : " + entry.getValue());
				if (!key.contains(".")) {
					continue;
				}

				String name = key.split("\\.")[0];
				String className2 = clazz.getName();
				if (!StringUtil.firstToUpper(name).equals(//
						className2.substring(className2.lastIndexOf(".") + 1))) {
					continue;
				}

				String fieldKey = key.substring(key.indexOf(".") + 1);

				String fieldName = null;
				if (fieldKey.contains(".")) {
					fieldName = fieldKey.substring(0, fieldKey.indexOf("."));
				} else {
					fieldName = fieldKey;
				}

				Field field = clazz.getDeclaredField(fieldName);
				Class<?> fieldClass = field.getType();

				if (EnumUtil.isEnum(fieldClass)) {
					Enum<?> enums = EnumUtil.toEnum(entry.getValue(), fieldKey.split("\\.")[1], fieldClass);
					BeanUtils.setProperty(t, fieldName, enums);
					continue;
				}

				if (DateUtil.isDate(fieldClass)) {
					String source = (String) entry.getValue();
					Date date = null;
					if (DateUtil.hasDate(source)) {
						if (DateUtil.hasTime(source)) {
							date = DateUtil.parseDateTime(source);
						} else {
							date = DateUtil.parseDate(source);
						}
					} else {
						date = DateUtil.parseTime(source);
					}
					BeanUtils.setProperty(t, fieldKey, date);
					continue;
				}

				BeanUtils.setProperty(t, fieldKey, entry.getValue());
			}

		} catch (Exception e) {
			// throw new RuntimeException("The Object is not found");
			e.printStackTrace();
		}
		return t;
	}

	// 判断是否为空
	public boolean isEmpty() {
		boolean flag = CollectionUtil.isEmpty(this.fileParams) //  
				&& !(CollectionUtil.isNotEmpty(this.formParams) //
				// 有默认加入的 request 与 response 对象
				&& this.formParams.size() > 2);
		
		return flag;
	}

	// Servlet 中的 request 与 response
	public HttpServletResponse getResponse() {
		return (HttpServletResponse) getObject("javax.servlet.HttpServletResponse");
	}

	public HttpServletRequest getRequest() {
		return (HttpServletRequest) getObject("javax.servlet.HttpServletRequest");
	}
	
	public HttpSession getSession() {
		return getRequest().getSession();
	}
	
	

	// 文件字段
	public List<FileParam> getFileList(String fieldName) {
		return getFileMap().get(fieldName);
	}

	public FileParam getFile(String fieldName) {
		List<FileParam> fileParamList = getFileList(fieldName);
		if (CollectionUtil.isNotEmpty(fileParamList) && fileParamList.size() == 1) {
			return fileParamList.get(0);
		}
		return null;
	}

	// 表单字段
	// 根据默认格式转换
	public Date getDate(String key) {
		return DateUtil.parseDate(getString(key));
	}

	public Date getDateTime(String key) {
		return DateUtil.parseDateTime(getString(key));
	}

	public Date getTime(String key) {
		return DateUtil.parseTime(getString(key));
	}

	// 根据传入格式转换
	public Date getDate(String key, String pattern) {
		return DateUtil.parse(getString(key), pattern);
	}

	public Float getFloat(String key) {
		return Float.parseFloat(getString(key));
	}

	public Boolean getBoolean(String key) {
		return Boolean.parseBoolean(getString(key));
	}

	public Integer getInt(String key) {
		return Integer.parseInt(getString(key));
	}

	public Long getLong(String key) {
		return Long.parseLong(getString(key));
	}

	public String getString(String key) {
		return String.valueOf(getObject(key)).trim();
	}

	private Object getObject(String key) {
		if (getFormMap().containsKey(key)) {
			Object obj = getFormMap().get(key);
			if (obj != null) {
				return obj;
			}
		}
		throw new RuntimeException("The value is not found");
	}

}
