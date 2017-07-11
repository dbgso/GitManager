package com.github.dbgso.util;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class StringUtils {

	public static final String[] ENCODING = new String[] { "UTF-8", "Shift_JIS", "EUC_JP", "UTF-16" };

	public static String encode(byte[] data) {
		for (String enc : StringUtils.ENCODING) {
			try {
				String encode = new String(data, enc);
				byte[] bytes = encode.getBytes(enc);
				if (Arrays.equals(data, bytes))
					return encode;
			} catch (UnsupportedEncodingException e) {
			}
		}
		return new String(data);
	}

}
