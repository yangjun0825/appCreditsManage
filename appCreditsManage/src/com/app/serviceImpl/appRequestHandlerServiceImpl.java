package com.app.serviceImpl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.app.inter.appRequestHandlerService;
import com.app.util.Base64;

@Component("requestHandler")  
@WebService(endpointInterface = "com.app.inter.appRequestHandlerService") 
public class appRequestHandlerServiceImpl implements appRequestHandlerService {

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
		
		String rqXmlstr = xmlStr.replaceAll(" ", "+");
		try {
			rqXmlstr = new String(Base64.decode(rqXmlstr), "UTF-8");
			
			if(logger.isDebugEnabled()) {
				logger.debug("[rqXmlstr] = " + rqXmlstr);
			}
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// 
		
		
		
		logger.debug("exit appRequestHandlerServiceImpl.requestHandlerEnternce(String xmlStr)");
		return "服务器成功接收";
	}

}
