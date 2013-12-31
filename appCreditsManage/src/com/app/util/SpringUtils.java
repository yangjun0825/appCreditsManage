package com.app.util;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

/**
 * renderText()函数用于Ajax请求返回页面输出文本
 * 
 * @author yangjun
 * 
 */
public class SpringUtils {

	public static final String TEXT_TYPE = "text/plain";

	public static final String XML_TYPE = "text/xml";

	public static final String JSON_TYPE = "application/json";

	// -- header 常量定义 --//
	private static final String HEADER_ENCODING = "encoding";

	private static final String HEADER_NOCACHE = "no-cache";

	private static final String DEFAULT_ENCODING = "UTF-8";

	private static final boolean DEFAULT_NOCACHE = true;

	private static final long EXPIRES_SECONDS = 3600L;// 缓存时间(单位为秒)

	// -- 绕过jsp直接输出文本的函数 --//
	/**
	 * 直接输出内容的简便函数.
	 * 
	 * eg. render("text/plain", "hello", "encoding:GBK"); render("text/plain", "hello", "no-cache:false");
	 * render("text/plain", "hello", "encoding:GBK", "no-cache:false");
	 * 
	 * @param headers
	 *            可变的header数组，目前接受的值为"encoding:"或"no-cache:",默认值分别为UTF-8和true.
	 */
	public static void render(HttpServletResponse responses, final String contentType, final String content,
			final String... headers) {
		HttpServletResponse response = initResponseHeader(responses, contentType, headers);
		try {
			response.getWriter().write(content);
			response.getWriter().flush();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 直接输出文本.
	 * 
	 * @see #render(String, String, String...)
	 */
	public static void renderText(HttpServletResponse response, final String text, final String... headers) {
		render(response, TEXT_TYPE, text, headers);
	}

	/**
	 * 直接输出JSON.
	 * 
	 * @param jsonString
	 *            json字符串.
	 * @see #render(String, String, String...)
	 */
	public static void renderJson(HttpServletResponse response, final String jsonString, final String... headers) {
		render(response, JSON_TYPE, jsonString, headers);
	}

	/**
	 * 直接输出XML.
	 * 
	 * @see #render(String, String, String...)
	 */
	public static void renderXml(HttpServletResponse response, final String xml, final String... headers) {
		render(response, XML_TYPE, xml, headers);
	}

	/**
	 * 分析并设置contentType与headers.
	 */
	public static HttpServletResponse initResponseHeader(HttpServletResponse response, final String contentType,
			final String... headers) {
		// 分析headers参数
		String encoding = DEFAULT_ENCODING;
		boolean noCache = DEFAULT_NOCACHE;
		long expiresSeconds = EXPIRES_SECONDS;
		for (String header : headers) {
			String headerName = StringUtils.substringBefore(header, ":");
			String headerValue = StringUtils.substringAfter(header, ":");
			if (StringUtils.equalsIgnoreCase(headerName, HEADER_ENCODING)) {
				encoding = headerValue;
			} else if (StringUtils.equalsIgnoreCase(headerName, HEADER_NOCACHE)) {
				noCache = Boolean.parseBoolean(headerValue);
			} else if (StringUtils.equalsIgnoreCase(headerName, "expiresSeconds")) {
				expiresSeconds = Long.parseLong(headerValue);
			} else {
				throw new IllegalArgumentException(headerName + "不是一个合法的header类型");
			}
		}
		// HttpServletResponse response = ;
		// 设置headers参数
		String fullContentType = contentType + ";charset=" + encoding;
		response.setContentType(fullContentType);
		if (noCache) {
			setDisableCacheHeader(response);
		} else {
			setExpiresHeader(response, expiresSeconds);
		}
		return response;
	}

	/**
	 * 设置禁止客户端缓存的Header.
	 */
	public static void setDisableCacheHeader(HttpServletResponse response) {
		// Http 1.0 header
		response.setDateHeader("Expires", 1L);
		response.addHeader("Pragma", "no-cache");
		// Http 1.1 header
		response.setHeader("Cache-Control", "no-cache, no-store, max-age=0");
	}

	/**
	 * 设置客户端缓存过期时间 的Header.
	 */
	public static void setExpiresHeader(HttpServletResponse response, long expiresSeconds) {
		// Http 1.0 header
		response.setDateHeader("Expires", System.currentTimeMillis() + expiresSeconds * 2);
		// Http 1.1 header
		response.setHeader("Cache-Control", "max-age=" + expiresSeconds * 2);// 缓存2个小时
	}
}
