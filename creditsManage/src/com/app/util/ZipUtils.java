package com.app.util;

import java.util.Enumeration;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.List;
 
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;



public class ZipUtils {
	public static void main(String[] args) throws Exception {
		unzipPSD("/media/share/material/file/temp/1eeb28ecb8e0d6f2.zip","/media/share/material/file/temp/");
 
	}
 
	/**
	 * 解压zip,
	 * 
	 * @param zipFile　里面可以有多个psd文件，支持多级目录
	 * @param targetPath　保存目录
	 * @throws Exception 
	 */
	public static void unzipPSD(String zipPath, String targetPath) throws Exception {
 
			ZipFile zipFile = new ZipFile(zipPath);
			Enumeration emu = zipFile.getEntries();
			int i = 0;
			while (emu.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) emu.nextElement();
 
				String fileName=entry.getName().toLowerCase();
				if(!fileName.startsWith("__macosx/")&&fileName.endsWith("psd"))
				{
					//如果文件名没有以__macosx/开头，且以psd结尾，就是psd文件，解压,在mac下压缩的文件，会自动加上__macosx目录，但其实是没用的
					BufferedInputStream bis = new BufferedInputStream(
							zipFile.getInputStream(entry));
					File file = new File(targetPath + System.currentTimeMillis()+".psd");
					//一次读40K
					int BUFFER=40960;
					FileOutputStream fos = new FileOutputStream(file);
					BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER);
 
					int count;
					byte data[] = new byte[BUFFER];
					while ((count = bis.read(data, 0, BUFFER)) != -1) {
						bos.write(data, 0, count);
					}
					bos.flush();
					bos.close();
					bis.close();
				}
			}
			zipFile.close();
 
	}
 
}
