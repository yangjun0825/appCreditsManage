package com.app.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.app.vo.Head;

public class Util {

	/** 
	* @Title: getInterHeadByXmlStr 
	* @Description: 获取头部数据
	* @param  xmlStr
	* @return Head    返回类型 
	* @throws 
	*/
	public Head getInterHeadByXmlStr(String xmlStr) {
		Document document = stringToDocument(xmlStr);
		Element rootElment = document.getRootElement();
		Head head = new Head();
		head.setBizcode(rootElment.elementText("bizcode"));
		head.setTransid(rootElment.elementText("transid"));
		head.setTimestamp(rootElment.elementText("timestamp"));
		head.setImei(rootElment.elementText("imei"));
		try {
			if (rootElment.element("imsi") != null) {
				String imsi1 = rootElment.elementText("imsi");
				if (null == imsi1 && "".equals(imsi1)) {
					imsi1 = "";
				}
				head.setImsi(imsi1);
			} else {
				head.setImsi("");
			}
			if (rootElment.element("ct") != null) {
				String ct1 = rootElment.elementText("ct");
				if (null == ct1 && "".equals(ct1)) {
					ct1 = "";
				}
				head.setCt(ct1);
			} else {
				head.setCt("");
			}
			if (rootElment.element("version") != null) {
				String version = rootElment.elementText("version");
				if (null == version && "".equals(version)) {
					version = "";
				}
				head.setVersion(version);
			} else {
				head.setVersion("");
			}
		} catch (Exception e) {
			head.setCt("");
			head.setImsi("");
			head.setVersion("");
		}
		return head;
	}
	
	/**
	 * stringToDocument 将字符串转为Document
	 * @param str xml格式的字符串
	 * @return Document
	 * @throws DocumentException
	 */
	public static Document stringToDocument(String str) {
		Document document = null;
		try {
			document = DocumentHelper.parseText(str);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return document;
	}
	
	/** 
	* @Title: parseHeaderXml 
	* @Description: 解析app传入字符
	* @param @param head
	* @param @param rootElment    设定文件 
	* @return void    返回类型 
	* @throws 
	*/
	public static void parseHeaderXml(Head head, Element rootElment) {
		head.setBizcode(rootElment.elementText("bizcode"));
		head.setTransid(rootElment.elementText("transid"));
		head.setTimestamp(rootElment.elementText("timestamp"));
		head.setImei(rootElment.elementText("imei"));
		head.setImsi(rootElment.elementText("imsi"));
		if (rootElment.element("ct") != null) {
			String ct1 = rootElment.elementText("ct");
			if (null == ct1 && "".equals(ct1)) {
				ct1 = "";
			}
			head.setCt(ct1);
		}
		if (rootElment.element("version") != null) {
			String version = rootElment.elementText("version");
			if (null == version && "".equals(version)) {
				version = "";
			}
			head.setVersion(version);
		}
		if (rootElment.element("code") != null) {
			String code = rootElment.elementTextTrim("code");
			if (null == code && "".equals(code)) {
				code = "";
			}
			head.setCode(code);
		}
	}
	

	/**
	 * 获取头部请求参数节点
	 * 
	 * @param xmlStr
	 * @return
	 */
	public static List<Element> getRequestDataByXmlStr(String xmlStr, String startNode) {
		Document document = stringToDocument(xmlStr);
		Element rootElment = document.getRootElement();
		List<Element> list = rootElment.selectNodes(startNode);
		return list;
	}
	
	/** 
	* @Title: getResponseForFalse 
	* @Description: 返回错误消息
	* @param @param xmlStr
	* @param @param head
	* @param @param errCode
	* @param @param errStr
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws 
	*/
	public static String getResponseForFalse(String xmlStr, Head head, String errCode, String errStr) {
		String imsi = head.getImsi();
		String imei = head.getImei();
		if (null == imsi || "null".equals(imsi)) {
			imsi = "";
		}
		if (null == imei || "null".equals(imei)) {
			imei = "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<tjtresponse><head><bizcode>" + head.getBizcode() + "</bizcode><transid>" + head.getTransid()
				+ "</transid><timestamp>" + getDateFormat(new Date(), "yyyyMMddHHmmssSSS") + "</timestamp>");
		sb.append("<imei>" + imei + "</imei><imsi>" + imsi + "</imsi><result>1</result><resultcode>" + errCode
				+ "</resultcode><resultmsg>" + errStr + "</resultmsg></head></tjtresponse>");
		return sb.toString();
	}
	
	/** 
	* @Title: getResponseForTrue 
	* @Description: 返回正确消息
	* @param  head
	* @param  encodeStr
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws 
	*/
	public static String getResponseForTrue(Head head, String encodeStr) {
		StringBuffer sb = new StringBuffer();
		String imsi = head.getImsi();
		String imei = head.getImei();
		if (null == imsi || "null".equals(imsi)) {
			imsi = "";
		}
		if (null == imei || "null".equals(imei)) {
			imei = "";
		}
		sb.append("<tjtresponse><head><bizcode>" + head.getBizcode() + "</bizcode><transid>" + head.getTransid()
				+ "</transid><timestamp>" + getDateFormat(new Date(), "yyyyMMddHHmmssSSS") + "</timestamp>");
		sb.append("<imei>" + imei + "</imei><imsi>" + imsi
				+ "</imsi><result>0</result><resultcode>0</resultcode><resultmsg></resultmsg></head>");
		sb.append("<svccont>" + encodeStr + "</svccont></tjtresponse>");
		return sb.toString();
	}
	
	/** 
	* @Title: getDateFormat 
	* @Description: 获取格式化日期字符
	* @param @param date
	* @param @param format
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws 
	*/
	public static String getDateFormat(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String result = sdf.format(date);
		return result;
	}
	
	/**
	 * 解密svccont节点
	 * 
	 * @param rootElment
	 * @param xmlStr
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static String getDecodeStrForXml(Element rootElment, String xmlStr) throws UnsupportedEncodingException,
			IOException {
		String key = rootElment.elementText("svccont");
		String decStr = new String(Base64.decode(key), "UTF-8");
		xmlStr = xmlStr.replace(key, decStr);
		System.out.println(xmlStr);
		return xmlStr;
	}
}
