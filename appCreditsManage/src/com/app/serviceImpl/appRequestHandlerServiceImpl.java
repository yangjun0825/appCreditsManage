package com.app.serviceImpl;

import javax.jws.WebService;

import org.springframework.stereotype.Component;

import com.app.inter.appRequestHandlerService;

@Component("requestHandler")  
@WebService(endpointInterface = "com.app.inter.appRequestHandlerService") 
public class appRequestHandlerServiceImpl implements appRequestHandlerService {

	public String requestHandlerEnternce(String xmlStr) {
		System.out.println("�ͻ��˷�����Ϣ�� " + xmlStr);
		return "���������ճɹ�";
	}

}
