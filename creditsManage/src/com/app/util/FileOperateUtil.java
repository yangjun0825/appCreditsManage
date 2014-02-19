package com.app.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
            String realName) throws Exception {  
        response.setContentType("text/html;charset=UTF-8");  
        request.setCharacterEncoding("UTF-8");  
        BufferedInputStream bis = null;  
        BufferedOutputStream bos = null;  
  
        String ctxPath = request.getSession().getServletContext()  
                .getRealPath("/")  
                + FileOperateUtil.UPLOADDIR;  
        String downLoadPath = ctxPath + storeName;  
  
        long fileLength = new File(downLoadPath).length();  
  
        response.setContentType(contentType);  
        response.setHeader("Content-disposition", "attachment; filename="  
                + new String(realName.getBytes("utf-8"), "ISO-8859-1"));  
        response.addHeader("Content-Length", String.valueOf(fileLength));  
       //response.setHeader("Content-Length", String.valueOf(fileLength));  
        
        bis = new BufferedInputStream(new FileInputStream(downLoadPath));  
        bos = new BufferedOutputStream(response.getOutputStream());  
        byte[] buff = new byte[2048];  
        int bytesRead;  
        while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {  
            bos.write(buff, 0, bytesRead);  
        }  
        bis.close();  
        bos.close();  
    }  
}  
