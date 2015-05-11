package org.tttalk.openfire.plugin;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;

import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kevinsawicki.http.HttpRequest;

public class Utils {
	private static final Logger log = LoggerFactory.getLogger(Utils.class);

	private static final String TTTALK_APP_SECRET = "tttalk.app.secret";

	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static boolean checkSign(Map<String, String> params) {
		if (params.containsKey("sign") && params.containsKey("loginid")) {
			String appkey = params.get("loginid");
			String sign = params.remove("sign");

			String sign2 = genSign(params, appkey);
			return sign2.equals(sign);
		}
		return false;
	}

	public static String genSign(Map<String, String> params, String appkey) {
		// sign
		StringBuilder sb = new StringBuilder();
		sb.append(appkey);

		String[] keyArray = params.keySet().toArray(new String[params.size()]);
		Arrays.sort(keyArray);

		for (String key : keyArray) {
			String value = params.get(key);
			if (!Utils.isEmpty(value)) {
				sb.append(key).append(value);
			}
		}
		sb.append(getAppSecret());
		log.info(sb.toString());
		String sign = Utils.sha1(sb.toString());

		return sign;
	}

	private static String getFormattedText(byte[] bytes) {
		int len = bytes.length;
		StringBuilder buf = new StringBuilder(len * 2);

		for (int j = 0; j < len; j++) {
			buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
			buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
		}
		return buf.toString();
	}

	public static String sha1(String str) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
			messageDigest.update(str.getBytes());
			return getFormattedText(messageDigest.digest());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isEmpty(String s) {
		return s == null || s.length() == 0 || s.equals("null");
	}

	public static String getAppSecret() {
		return JiveGlobals.getProperty(TTTALK_APP_SECRET,
				"2a9304125e25edaa5aff574153eafc95c97672c6");
	}

	public static String encodeParameters(Map<String, String> params) {
		StringBuffer buf = new StringBuffer();
		String[] keyArray = params.keySet().toArray(new String[0]);
		Arrays.sort(keyArray);
		int j = 0;
		for (String key : keyArray) {
			String value = params.get(key);
			if (j++ != 0) {
				buf.append("&");
			}
			if (!Utils.isEmpty(value)) {
				try {
					buf.append(URLEncoder.encode(key, "UTF-8")).append("=")
							.append(URLEncoder.encode(value, "UTF-8"));
				} catch (java.io.UnsupportedEncodingException neverHappen) {
					// throw new RuntimeException(neverHappen.getMessage(),
					// neverHappen);
				}
			}
		}

		return buf.toString();
	}

	public static String get(String url, Map<String, String> params) {
		url += "?" + Utils.encodeParameters(params);

		log.info("get request=" + url);
		String body = HttpRequest.get(url).body();
		log.info("get response=" + body);
		return body;
	}

}
