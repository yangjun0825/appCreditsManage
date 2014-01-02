package com.app.bean;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.ServiceException;

import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;



public class Test {
    public static void main(String[] args) throws Exception {  
    	JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();  
        org.apache.cxf.endpoint.Client client = dcf.createClient("http://192.168.1.248:8828/appCreditsManage/services/requestServerHandler?wsdl");  
        QName name=new QName("http://inter.app.com/","requestHandlerEnternce");  
        String xmlStr = "";  
        //注册
//        xmlStr = "<tjtrequest>"
//       	 + "<bizcode>tjt001</bizcode>"
//    	 + "<transid>6F9619FF-8B86-D011-B42D-00C04FC964FF</transid>"
//    	 + "<timestamp>201207081223501234</timestamp>"
//    	 + "<imei>434343434334322</imei>"
//    	 + "<imsi>460030912121001</imsi>"
//    	 + "<svccont>PHByYT48aXRlbT48YWNjb3V0PmJpZTwvYWNjb3V0Pjxwd2Q+MTExMTExPC9wd2Q+PC9pdGVtPjwvcHJhPg==</svccont>"
//    	 + "</tjtrequest>";
        
        //登录
        xmlStr = "<tjtrequest>"
          	 + "<bizcode>tjt002</bizcode>"
       	 + "<transid>6F9619FF-8B86-D011-B42D-00C04FC964FF</transid>"
       	 + "<timestamp>201207081223501234</timestamp>"
       	 + "<imei>434343434334322</imei>"
       	 + "<imsi>460030912121001</imsi>"
       	 + "<svccont>PHByYT48aXRlbT48YWNjb3V0PmJpZTwvYWNjb3V0Pjxwd2Q+MTExMTEyPC9wd2Q+PC9pdGVtPjwvcHJhPg==</svccont>"
       	 + "</tjtrequest>";
        
        Object[] objects=client.invoke(name,xmlStr);   
        System.out.println("返回结果: " + objects[0].toString());  
    }  
}
