package com.app.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.zip.ZipFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.util.FileOperateUtil;
import com.app.util.SpringUtils;
import com.app.util.Util;
import com.app.util.ZipToFile;
import com.app.util.ZipTool;

/** 
* @ClassName: AppDownLoadController 
* @Description: 软件下载
* @author yangjun 
* @date Feb 13, 2014 2:14:24 PM  
*/
@Controller
@RequestMapping("version")
public class AppDownLoadController {
	
	private Log logger = LogFactory.getLog(AppDownLoadController.class);
	
	@RequestMapping("*.apk")
	public void appDownLoadProcess(HttpServletRequest request, HttpServletResponse response) {
		
		logger.debug("enter AppDownLoadController.appDownLoadProcess(HttpServletRequest request, HttpServletResponse response)");
		
		String ctxPath = request.getSession().getServletContext().getRealPath("/"); 
		
		String uri = request.getRequestURI();
		
		String account = uri.substring(uri.lastIndexOf("/") + 1, uri.lastIndexOf("."));
		
		if(logger.isDebugEnabled()) {
			logger.debug("[ctxPath] = " + ctxPath + " [account] = " + account);
		}
		
		//1.解压压缩包
//		try {
//			//Util.upZipFile(ctxPath + "version/aizanqian.zip",ctxPath + "version/aizanqian/");
//			ZipTool.unzip(ctxPath + "version/aizanqian/", ctxPath + "version/aizanqian.zip");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		//如果文件已经存在，则直接下载
		File fileLoad = new File(ctxPath + "version/" + account + ".apk");
		if(fileLoad.exists()) {
			String contentType = "application/octet-stream";  
			  
	        try {
	        	FileOperateUtil.download(request, response, account +".apk", contentType, account + ".apk");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		} else {
			//1.将账号写入文件
			File f = new File(ctxPath + "version/aizanqian/assets/code.txt");
			if(!f.exists()){
				try {
					f.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			PrintWriter pw = null;
			try {
				pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f)),true);
				pw.println(account);  
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}  finally {
				pw.close();
			}
			
			
			//2.重新生成apk
			try {
				//Util.compressedFile(ctxPath + "version/aizanqian/", ctxPath + "version/");
				ZipTool.zip(ctxPath + "version/aizanqian/",ctxPath + "version/aizanqian.zip");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			File file1 = new File(ctxPath + "version/aizanqian.zip");  
			File file2 = new File(ctxPath + "version/" + account + "HH.apk");  
			
			boolean flag = file1.renameTo(file2);
			
			if(logger.isDebugEnabled()) {
				logger.debug("[renameResult] = " + flag);
			}
			
			//3.加密apk
//			Util.exec("rm -rf aizanqian.apk");
			
			String cmd = "sh /datac/myapp/admin-tomcat/webapps/creditsManage/version/apkPack.sh " + account;
			
			String apkResult = Util.exec(cmd);
			
			if(logger.isDebugEnabled()) {
				logger.debug("[apkResult] = " + apkResult + " [cmd] = " + cmd);
			}
			
			String contentType = "application/octet-stream";  
			  
	        try {
	        	FileOperateUtil.download(request, response, account +".apk", contentType, account + ".apk");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		}
		
		
		
		logger.debug("exit AppDownLoadController.appDownLoadProcess(HttpServletRequest request, HttpServletResponse response)");
	}
	
}
