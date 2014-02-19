package com.app.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.util.Enumeration;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipInputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;



public class Util {

	public static void unZipFiles(File zipFile,String descDir)throws IOException{ 
		
        File pathFile = new File(descDir);  
        if(!pathFile.exists()){  
            pathFile.mkdirs();  
        }  
        ZipFile zip = new ZipFile(zipFile); 
        for(Enumeration entries = zip.getEntries();entries.hasMoreElements();){  
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
    } 
	
	public static void compressedFile(String resourcesPath,String targetPath) throws Exception{  
	        File resourcesFile = new File(resourcesPath);     //源文件  
	        File targetFile = new File(targetPath);           //目的  
	        //如果目的路径不存在，则新建  
	        if(!targetFile.exists()){       
	            targetFile.mkdirs();    
	        }  
	          
	        String targetName = resourcesFile.getName()+"test.zip";   //目的压缩文件名  
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
	 
//	 public static void upZipFile(String zipFilename,String dir) throws Exception{ 
//	        ZipInputStream zin=new ZipInputStream(new FileInputStream(zipFilename));   
//	        ZipEntry ze=null;   
//	        byte[] buf=new byte[1024];   
//	        while((ze=zin.getNextEntry())!=null){   
//	            System.out.println("unziping:"+ze.getName()); 
//	            OutputStream os=new BufferedOutputStream(new FileOutputStream(getRealFileName(dir,ze.getName())));   
//	            int readLen=0;   
//	            while ((readLen=zin.read(buf, 0, 1024))!=-1) {   
//	                os.write(buf, 0, readLen);   
//	            }   
//	            os.close();    
//	            
//	        }   
//	        zin.close();   
//
//	} 
//		
//	    public static File getRealFileName(String baseDir, String absFileName){   
//	        //判断是否有下级目录,如果没有则将该文件直接new出来 
//	    String[] dirs=absFileName.split("/");   
//	        File ret=new File(baseDir);   
//	        //有下级目录则先创建目录，再创建文件 
//	        if(dirs.length>1){   
//	            for (int i = 0; i < dirs.length-1;i++) {   
//	                ret=new File(ret, dirs[i]);   
//	            }   
//	            if(!ret.exists())   
//	                ret.mkdirs();   
//	            ret=new File(ret, dirs[dirs.length-1]);   
//	            return ret;   
//	        } 
//	        return new File(ret,absFileName);   
//	    }   
	 
	 public static String exec(String cmd) {
         try {
             String[] cmdA = { "/bin/sh", "-c", cmd };
             Process process = Runtime.getRuntime().exec(cmdA);
             LineNumberReader br = new LineNumberReader(new InputStreamReader(
                     process.getInputStream()));
             StringBuffer sb = new StringBuffer();
             String line;
             while ((line = br.readLine()) != null) {
                 System.out.println(line);
                 sb.append(line).append("\n");
             }
             return sb.toString();
         } catch (Exception e) {
             e.printStackTrace();
         }
         return null;
     }
}
