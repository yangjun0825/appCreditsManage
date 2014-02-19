package com.app.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.app.util.Util;

public class Test {
	public static void unZipFiles(File zipFile,String descDir)throws IOException{  
        File pathFile = new File(descDir);  
        if(!pathFile.exists()){  
            pathFile.mkdirs();  
        }  
        ZipFile zip = new ZipFile(zipFile);  
        for(Enumeration entries = zip.entries();entries.hasMoreElements();){  
            ZipEntry entry = (ZipEntry)entries.nextElement();  
            String zipEntryName = entry.getName();  
            InputStream in = zip.getInputStream(entry);  
            String outPath = (descDir+zipEntryName).replaceAll("\\*", "/");;  
            //判断路径是否存在,不存在则创建文件路径  
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));  
            if(!file.exists()){  
                file.mkdirs();  
            }  
            //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压  
            if(new File(outPath).isDirectory()){  
                continue;  
            }  
            //输出文件路径信息  
            System.out.println(outPath);  
              
            OutputStream out = new FileOutputStream(outPath);  
            byte[] buf1 = new byte[1024];  
            int len;  
            while((len=in.read(buf1))>0){  
                out.write(buf1,0,len);  
            }  
            in.close();  
            out.close();  
            }  
        System.out.println("******************解压完毕********************");  
    }  
	
	
	 public static void compressedFile(String resourcesPath,String targetPath) throws Exception{  
	        File resourcesFile = new File(resourcesPath);     //源文件  
	        File targetFile = new File(targetPath);           //目的  
	        //如果目的路径不存在，则新建  
	        if(!targetFile.exists()){       
	            targetFile.mkdirs();    
	        }  
	          
	        String targetName = resourcesFile.getName()+".apk";   //目的压缩文件名  
	        FileOutputStream outputStream = new FileOutputStream(targetPath+"\\"+targetName);  
	        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(outputStream));  
	          
	        createCompressedFile(out, resourcesFile, "");  
	          
	        out.close();    
	    }  
	
	
	 public static void createCompressedFile(ZipOutputStream out,File file,String dir) throws Exception{  
	        //如果当前的是文件夹，则进行进一步处理  
	        if(file.isDirectory()){  
	            //得到文件列表信息  
	            File[] files = file.listFiles();  
	            //将文件夹添加到下一级打包目录  
	            out.putNextEntry(new ZipEntry(dir+"/"));  
	              
	            dir = dir.length() == 0 ? "" : dir +"/";  
	              
	            //循环将文件夹中的文件打包  
	            for(int i = 0 ; i < files.length ; i++){  
	                createCompressedFile(out, files[i], dir + files[i].getName());         //递归处理  
	            }  
	        }  
	        else{   //当前的是文件，打包处理  
	            //文件输入流  
	            FileInputStream fis = new FileInputStream(file);  
	              
	            out.putNextEntry(new ZipEntry(dir));  
	            //进行写操作  
	            int j =  0;  
	            byte[] buffer = new byte[1024];  
	            while((j = fis.read(buffer)) > 0){  
	                out.write(buffer,0,j);  
	            }  
	            //关闭输入流  
	            fis.close();  
	        }  
	    }  
	
	public static void main(String []args) throws Exception {
//		File file = new File("F:/app测试包/HOH_CS.zip");
//		//解压
//		unZipFiles(file,"F:/app测试包/HOH_CS/");
//		
//		//写入文件
//		String s = "hello world";
//		File f = new File("F:/app测试包/HOH_CS/assets/code.txt");
//		if(!f.exists()){
//			f.createNewFile();
//		}
//		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f)),true);  
//		pw.println(s);  
//		System.out.println(new Date());
//		compressedFile("F:/app测试包/HOH_CS/", "F:/app测试包/");
//		System.out.println(new Date());
//		
//		String uri = "/creditsManage/version/aaa.apk";
//		
//		String account = uri.substring(uri.lastIndexOf("/") + 1, uri.lastIndexOf("."));
//		
//		System.out.println("account: " + account);
		
//		Util.upZipFile("F:/个人/0215/aizanqian.zip","F:/个人/0215/version/aizanqian/");
		
//		FileOutputstream fos = new FileOutputstream("c:/myzip.zip");
//	    zipoutputstream zos = new zipoutputstream(fos);
//	    zipentry ze = new zipentry("c:/file1.txt");
//	    zos.putnextentry(ze);
//	    zos.closeentry();
//	    ze = new zipentry("c:/file2.txt");
//	    zos.putnextentry(ze);
//	    zos.closeentry();
//	    zos.close();
		
		String a = "success";
		String b = "success";
		
		System.out.println(a == b);
	}
}
