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
        org.apache.cxf.endpoint.Client client = dcf.createClient("http://115.29.46.58:8080/appCreditsManage/services/requestServerHandler?wsdl");  
        //urlΪ����webService��wsdl��ַ  
        QName name=new QName("http://inter.app.com/","requestHandlerEnternce");  
        //namespace������ռ䣬methodName�Ƿ�����  
        String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"  
                 + "     <facelook>"  
                 + "        <condition>"  
                 + "            <name>��</name>"  
                 + "            <description></description>"  
                 + "            <pageno></pageno>"  
                 + "            <pagesize></pagesize>"  
                 + "        </condition>"  
                 + "     </facelook>";  
        //paramvalueΪ����ֵ  
        Object[] objects=client.invoke(name,xmlStr);   
        //����web Service//�����ý��  
        System.out.println(objects[0].toString());  
    }  
}
