package com.app.bean;

import java.io.File;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.HashSet;

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.ServiceException;

import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.BindingOperationInfo;

import com.app.util.Util;



public class Test {
    public static void main(String[] args) throws Exception {  
//    	JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();  
//        org.apache.cxf.endpoint.Client client = dcf.createClient("http://192.168.1.248:8828/appCreditsManage/services/requestServerHandler?wsdl");  
//        QName name=new QName("http://inter.app.com/","requestHandlerEnternce");  
        String xmlStr = "";  
        //注册
//        xmlStr = "<tjtrequest>"
//       	 + "<bizcode>tjt001</bizcode>"
//    	 + "<transid>6F9619FF-8B86-D011-B42D-00C04FC964FF</transid>"
//    	 + "<timestamp>201207081223501234</timestamp>"
//    	 + "<imei>434343434334322</imei>"
//    	 + "<imsi>460030912121001</imsi>"
//    	 + "<version>1.0</version>"
//    	 + "<svccont>"
//    	 + "<pra><item><accout>123456789</accout><pwd>111111</pwd></item></pra>" 
//    	 + "</svccont>"
//    	 + "</tjtrequest>";
        xmlStr = "PHRqdHJlcXVlc3Q+PGJpemNvZGU+dGp0MDAxPC9iaXpjb2RlPjx0cmFuc2lkPjZGOTYxOUZGLThCODYtRDAxMS1CNDJELTAwQzA0RkM5NjRGRjwvdHJhbnNpZD48dGltZXN0YW1wPjIwMTIwNzA4MTIyMzUwMTIzNDwvdGltZXN0YW1wPjxpbWVpPjQzNDM0MzQzNDMzNDMyMjwvaW1laT48aW1zaT40NjAwMzA5MTIxMjEwMDE8L2ltc2k+PHZlcnNpb24+MS4wPC92ZXJzaW9uPjxzdmNjb250PjxwcmE+PGl0ZW0+PGFjY291dD4xMjM0NTY3ODk8L2FjY291dD48cHdkPjExMTExMTwvcHdkPjwvaXRlbT48L3ByYT48L3N2Y2NvbnQ+PC90anRyZXF1ZXN0Pg==";
        
        //登录
//        xmlStr = "<tjtrequest>"
//          	 + "<bizcode>tjt002</bizcode>"
//       	 + "<transid>6F9619FF-8B86-D011-B42D-00C04FC964FF</transid>"
//       	 + "<timestamp>201207081223501234</timestamp>"
//       	 + "<imei>434343434334322</imei>"
//       	 + "<imsi>460030912121001</imsi>"
//       	 + "<svccont>PHByYT48aXRlbT48YWNjb3V0PmJpZTwvYWNjb3V0Pjxwd2Q+MTExMTExPC9wd2Q+PC9pdGVtPjwvcHJhPg==</svccont>"
//       	 + "</tjtrequest>";
//        
        //积分同步
//        xmlStr = "<tjtrequest>"
//         	 + "<bizcode>tjt003</bizcode>"
//      	 + "<transid>6F9619FF-8B86-D011-B42D-00C04FC964FF</transid>"
//      	 + "<timestamp>201207081223501234</timestamp>"
//      	 + "<imei>434343434334322</imei>"
//      	 + "<imsi>460030912121001</imsi>"
//      	 + "<svccont>PHByYT48aXRlbT48YWNjb3V0PmJpZTwvYWNjb3V0Pjx0eXBlPjE8L3R5cGU+PGNoYW5uZWx0eXBlPjE8L2NoYW5uZWx0eXBlPjxjcmVkaXQ+MTAwPC9jcmVkaXQ+PC9pdGVtPjwvcHJhPg==</svccont>"
//      	 + "</tjtrequest>";
        //获取体现记录
//        xmlStr = "<tjtrequest>"
//        	 + "<bizcode>tjt004</bizcode>"
//     	 + "<transid>6F9619FF-8B86-D011-B42D-00C04FC964FF</transid>"
//     	 + "<timestamp>201207081223501234</timestamp>"
//     	 + "<imei>434343434334322</imei>"
//     	 + "<imsi>460030912121001</imsi>"
//     	 + "<svccont>PHByYT48aXRlbT48YWNjb3V0PmJpZTwvYWNjb3V0PjwvaXRlbT48L3ByYT4=</svccont>"
//     	 + "</tjtrequest>";
        
        //用户反馈
//        xmlStr = "<tjtrequest>"
//       	 + "<bizcode>tjt005</bizcode>"
//    	 + "<transid>6F9619FF-8B86-D011-B42D-00C04FC964FF</transid>"
//    	 + "<timestamp>201207081223501234</timestamp>"
//    	 + "<imei>434343434334322</imei>"
//    	 + "<imsi>460030912121001</imsi>"
//    	 + "<svccont>PHByYT48aXRlbT48YWNjb3V0PmJpZTwvYWNjb3V0Pjxtc2c+dGVzdOa1i+ivlTwvbXNnPjwvaXRlbT48L3ByYT4=</svccont>"
//    	 + "</tjtrequest>";
        
       // Object[] objects=client.invoke(name,xmlStr);   
        //System.out.println("返回结果: " + objects[0].toString());  
//        String str = "1.0.2";
//        String a = str.replace(".", "");
//        System.out.println("a: " + a);
//        
//        int i = 11111111;
//        
//        HashSet<Integer> set = new HashSet<Integer>();  
//	    Util.randomSet(10000000,99999999,1,set);
//	    
//	    int accountRandom = 0;
//	    
//	    for (int j : set) {  
//	    	System.out.println("a: " + j);
//	    }  
	    
	    
//	    File file1=new File("D:/apk/UserSmsServiceImpl.class");  
//	    File file2=new File("D:/apk/b.txt");  
//	    boolean flag = file1.renameTo(file2);
	    System.out.println("flag: " + 0/2);
	    String msg = "your password" + 1111 + ".";
		String xmlstr = "<smsrequest><mobile>15234057707</mobile><msgcontent>" + msg + "</msgcontent></smsrequest>";
	
	    JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        org.apache.cxf.endpoint.Client client = dcf.createClient("http://183.203.18.7:8082/HuaWeiSms_WebService/services/HandleSms?wsdl");
       
	     // 下面一段处理 WebService接口和实现类namespace不同的情况  
	     // CXF动态客户端在处理此问题时，会报No operation was found with the name的异常  
	     Endpoint endpoint = client.getEndpoint();  
	     QName opName = new QName(endpoint.getService().getName().getNamespaceURI(), "AddSmsInterface");  
	     BindingInfo bindingInfo = endpoint.getEndpointInfo().getBinding();  
	     if (bindingInfo.getOperation(opName) == null) {  
	         for (BindingOperationInfo operationInfo : bindingInfo.getOperations()) {  
	             if ("AddSmsInterface".equals(operationInfo.getName().getLocalPart())) {  
	                 opName = operationInfo.getName();  
	                 break;  
	             }  
	         }  
	     }  
        
        
        //sayHello 为接口中定义的方法名称   张三为传递的参数   返回一个Object数组
        Object[] objects=client.invoke("AddSmsInterface", xmlstr); 
		System.out.println(objects.toString());
    }  
}
