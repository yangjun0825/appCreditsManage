package com.app.serviceImpl;

import javax.jws.WebService;

import org.springframework.stereotype.Component;

import com.app.inter.appRequestHandlerService;

@Component("requestHandler")  
@WebService(endpointInterface = "com.app.inter.appRequestHandlerService") 
public class appRequestHandlerServiceImpl implements appRequestHandlerService {

	public String requestHandlerEnternce(String xmlStr) {
		System.out.println("客户端发送消息： " + xmlStr);
		return "服务器接收成功";
	}

}
