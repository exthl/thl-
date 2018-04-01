package com.thl.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.thl.core.log.Logger;
import com.thl.core.log.LoggerFactory;

public class URLUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(URLUtil.class);
	
	
	public static String encodeURL(String url) {
		String encodeUrl = null;
		try {
			encodeUrl = URLEncoder.encode(url, "utf-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("encode failure", e, true);
		}
		return encodeUrl;
	}
	
	public static String decodeURL(String url) {
		String decodeUrl = null;
		try {
			decodeUrl = URLDecoder.decode(url, "utf-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("decode failure", e, true);
		}
		return decodeUrl;
	}
	
	
}
