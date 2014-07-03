package com.app.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/** 
* @ClassName: FileOperateUtil 
* @Description: 文件上传下载工具栏
* @author yangjun 
* @date Aug 26, 2013 11:41:52 AM  
*/
public class FileOperateUtil {  
	
    private static final String UPLOADDIR = "version/";  
  
    /** 
    * @Title: download 
    * @Description: 下载文件
    * @param  request
    * @param  response
    * @param  storeName
    * @param  contentType
    * @param  realName
    * @return void    返回类型 
    * @throws 
    */
    public static void download(HttpServletRequest request,  
            HttpServletResponse response, String storeName, String contentType,  
            String realName) {  
        response.setContentType("text/html;charset=UTF-8");  
        try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
  
        String ctxPath = request.getSession().getServletContext()  
                .getRealPath("/")  
                + FileOperateUtil.UPLOADDIR;  
        String downLoadPath = ctxPath + storeName;  
  
        long fileLength = new File(downLoadPath).length();  
  
        response.setContentType(contentType);  
        try {
			response.setHeader("Content-disposition", "attachment; filename="  
			        + new String(realName.getBytes("utf-8"), "ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        response.addHeader("Content-Length", String.valueOf(fileLength));  
       //response.setHeader("Content-Length", String.valueOf(fileLength));  
        
        BufferedInputStream bis = null;  
        BufferedOutputStream bos = null;  
        
        FileInputStream fis = null;
        OutputStream os = null;
        
        try {
        	 fis = new FileInputStream(downLoadPath);
        	 bis = new BufferedInputStream(fis);  
        	 
        	 os = response.getOutputStream();
             bos = new BufferedOutputStream(os);  
             byte[] buff = new byte[2048];  
             int bytesRead;  
             while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {  
                 bos.write(buff, 0, bytesRead);  
             }  
             bos.close(); 
             os.close();
             fis.close();
             bis.close(); 
        } catch(Exception e) {
        	e.printStackTrace();
        } finally {
        	try {
        		if(bos != null) {
        			bos.close();
        		}
				if(bis != null) {
					bis.close();
				}
				if(os != null) {
					os.close();
				}
				if(fis != null) {
					fis.close();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }
       
    }  
}  
