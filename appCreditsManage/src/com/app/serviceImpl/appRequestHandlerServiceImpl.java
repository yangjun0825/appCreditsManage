package com.app.serviceImpl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jws.WebService;

import org.apache.commons.codec.digest.DigestUtils;
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
import com.app.version.service.VersionService;
import com.app.vo.Head;

@Component("requestHandler")  
@WebService(endpointInterface = "com.app.inter.appRequestHandlerService") 
public class appRequestHandlerServiceImpl implements appRequestHandlerService {

	private Head head = new Head();
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CreditsService creditsService;
	
	@Autowired
	private VersionService versionService;
	
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
				
				xmlStr = xmlStr.replaceAll(" ", "+");
				String rqXmlstr = new String(Base64.decode(xmlStr), "UTF-8");
				
				if(logger.isDebugEnabled()) {
					logger.debug("[rqXmlstr] = " + rqXmlstr);
				}
				
				Document document = Util.stringToDocument(rqXmlstr);
				Element rootElment = document.getRootElement();
				Util.parseHeaderXml(head, rootElment);
				
				String code = head.getCode();
				
				String imei = head.getImei();
				
				String enCodeStr = imei + "  " + imei;
				
				enCodeStr = DigestUtils.md5Hex(enCodeStr);
				
				if(logger.isDebugEnabled()) {
					logger.debug("[code] = " + code + " [imei] = " + imei + " [enCodeStr] = " + enCodeStr);
				}
				
				if(StringUtils.isBlank(imei) || !enCodeStr.equals(code)) {
					result = Util.getResponseForFalse(rqXmlstr, head, "100", "请确保您是用手机并且不存在作弊行为!");
				} else {
					String bizCode = head.getBizcode();
					
					if(StringUtils.isBlank(bizCode) || "null".equals(bizCode)) {
						bizCode = "";
					}
					
					//String rqXmlstr = Util.getDecodeStrForXml(rootElment, xmlStr);
					
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
								case tjt006:
									result = versionService.versionUpdate(rqXmlstr, head);
									break;
								case tjt007:
									result = userService.userInfoModify(rqXmlstr, head);
									break;
								case tjt008:
									result = userService.userAutoRegister(rqXmlstr, head);
									break;
								default:
									flag = true;
									break;
							}
						} catch (Exception e){
							logger.debug("[SystemError] = " + e.getMessage());
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
						return Util.getResponseForFalse(xmlStr, head, "100", "网络异常，请稍后重试");
					}
				}
			}
			
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.debug("[SystemError] = " + e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.debug("[SystemError] = " + e.getMessage());
		}// 
		
		if(logger.isDebugEnabled()) {
			logger.debug("[resultBe] = " + result);
		}
		
		try {
			result = Base64.encodeBytes(result.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.debug("[SystemError] = " + e.getMessage());
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("[resultAe] = " + result);
		}
		
		logger.debug("exit appRequestHandlerServiceImpl.requestHandlerEnternce(String xmlStr)");
		return result;
	}

}
