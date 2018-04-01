package com.thl.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thl.core.log.Logger;
import com.thl.core.log.LoggerFactory;

public class JSONUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(JSONUtil.class);
	
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	public static <T> String toJSON(T t) {
		String json = null;
		try {
			json = OBJECT_MAPPER.writeValueAsString(t);
		} catch (JsonProcessingException e) {
			LOGGER.error("converse to json failure", e, true);
		}
		return json;
	}
	
	public static <T> T fromJSON(String json, Class<T> clazz) {
		T t = null;
		try {
			t = OBJECT_MAPPER.readValue(json, clazz);
		} catch (Exception e) {
			LOGGER.error("converser to Pojo failure", e, true);
		}
		return t;
	}
	
}
