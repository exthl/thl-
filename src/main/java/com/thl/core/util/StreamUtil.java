package com.thl.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.thl.core.log.Logger;
import com.thl.core.log.LoggerFactory;

public class StreamUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(StreamUtil.class);

	public static String getString(InputStream in) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			LOGGER.error("read data from input stream failure", e, true);
		}
		return sb.toString();
	}

	public static void copyStream(InputStream inputStream, OutputStream outputStream) {
		try {
			int len;
			byte[] buffer = new byte[1024 * 4];
			while ((len = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
			}
			outputStream.flush();
		} catch (IOException e) {
			LOGGER.error("copy stream failure", e, true);
		} finally {
			try {
				inputStream.close();
				outputStream.close();
			} catch (IOException e1) {
				LOGGER.error("close stream failure", e1, true);
			}
		}
	}

}
