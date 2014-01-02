package com.app.serviceImpl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.app.credits.service.CreditsService;
import com.app.inter.appRequestHandlerService;
import com.app.user.service.UserService;
import com.app.util.Base64;
import com.app.util.InterfaceType;
import com.app.util.Util;
import com.app.vo.Head;

@Component("requestHandler")  
@WebService(endpointInterface = "com.app.inter.appRequestHandlerService") 
public class appRequestHandlerServiceImpl implements appRequestHandlerService {

	private Head head = new Head();
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CreditsService creditsService;
	
	private Log logger = LogFactory.getLog(appRequestHandlerServiceImpl.class);	
	
	/* (非 Javadoc) 
	* <p>Title: requestHandlerEnternce</p> 
	* <p>Description:接口入口 </p> 
	* @param xmlStr
	* @return 
	* @see com.app.inter.appRequestHandlerService#requestHandlerEnternce(java.lang.String) 
	*/
	public String requestHandlerEnternce(String xmlStr) {
		
		logger.debug("enter appRequestHandlerServiceImpl.requestHandlerEnternce(String xmlStr) " + "[xmlStr] = " + xmlStr);
		
		//String rqXmlstr = xmlStr.replaceAll(" ", "+");
		String result = "";
		try {
			//rqXmlstr = new String(Base64.decode(rqXmlstr), "UTF-8");
			
//			if(logger.isDebugEnabled()) {
//				logger.debug("[rqXmlstr] = " + rqXmlstr);
//			}
			
			// 是否返回错误信息
			boolean flag = false;
			
			if(StringUtils.isNotBlank(xmlStr)) {
				Document document = Util.stringToDocument(xmlStr);
				Element rootElment = document.getRootElement();
				Util.parseHeaderXml(head, rootElment);
				String bizCode = head.getBizcode();
				
				if(StringUtils.isBlank(bizCode) || "null".equals(bizCode)) {
					bizCode = "";
				}
				
				String rqXmlstr = Util.getDecodeStrForXml(rootElment, xmlStr);
				
				if (!flag || StringUtils.isNotBlank(bizCode)) {
					
					try {
						switch (InterfaceType.getInterfaceType(bizCode)) {
							case tjt001:
								result = userService.userRegister(rqXmlstr, head);
								break;
							case tjt002:
								result = userService.userLogin(rqXmlstr, head);
								break;
							case tjt003:
								result = creditsService.userCreditsSysn(rqXmlstr, head);
								break;
							case tjt004:
								result = creditsService.userCreditsRecords(rqXmlstr, head);
								break;
							case tjt005:
								result = userService.userFeedBack(rqXmlstr, head);
								break;
							default:
								flag = true;
								break;
						}
					} catch (Exception e){
						flag = true;
					}
					
				} else {
					flag = true;
				}
				
				if (flag) {
					head.setBizcode("10000");
					head.setTransid("100000012345678");
					SimpleDateFormat sdfUUID = new SimpleDateFormat("yyyyMMddHHmmssSSS");
					String str = sdfUUID.format(new Date());
					head.setTimestamp(str);
					head.setImei("100000012345678");
					head.setImsi("100000012345678");
					head.setCt("100000012345678");
					return Util.getResponseForFalse(xmlStr, head, "101", "程序产生不可知异常");
				}
				
			}
			
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// 
		
		if(logger.isDebugEnabled()) {
			logger.debug("[result] = " + result);
		}
		
		logger.debug("exit appRequestHandlerServiceImpl.requestHandlerEnternce(String xmlStr)");
		return result;
	}

}
