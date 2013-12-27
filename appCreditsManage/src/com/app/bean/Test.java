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
        //url为调用webService的wsdl地址  
        QName name=new QName("http://inter.app.com/","requestHandlerEnternce");  
        //namespace是命名空间，methodName是方法名  
        String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"  
                 + "     <facelook>"  
                 + "        <condition>"  
                 + "            <name>家</name>"  
                 + "            <description></description>"  
                 + "            <pageno></pageno>"  
                 + "            <pagesize></pagesize>"  
                 + "        </condition>"  
                 + "     </facelook>";  
        //paramvalue为参数值  
        Object[] objects=client.invoke(name,xmlStr);   
        //调用web Service//输出调用结果  
        System.out.println(objects[0].toString());  
    }  
}
