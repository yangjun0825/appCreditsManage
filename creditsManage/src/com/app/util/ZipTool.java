package com.app.util;

import java.io.BufferedInputStream;

import java.io.BufferedOutputStream;

import java.io.File;

import java.io.FileInputStream;

import java.io.FileNotFoundException;

import java.io.FileOutputStream;

import java.io.IOException;

import java.io.InputStream;

import java.io.OutputStream;

import java.util.ArrayList;

import java.util.Enumeration;

import java.util.List;

import org.apache.tools.zip.ZipEntry;

import org.apache.tools.zip.ZipFile;

import org.apache.tools.zip.ZipOutputStream;

/**
 * @author WYY
 * @description 压缩工具类
 */
public class ZipTool
{

    public static final int BUFFER = 1024;// 缓存大小

 

    /**
     * @description 压缩文件或目录（包含子目录压缩）
     * @updated by WYY 2013年11月14日 下午10:11:34
     * @param baseDir
     *            待压缩目录或文件
     * @param dest
     *            要所后的文件名
     */

    public static void zip(String baseDir, String dest)
    {

        File sourceFile = new File(baseDir);

        File destFile = new File(dest);

        ZipOutputStream zos = null;

        try

        {

            if (sourceFile.isFile())

            {// 单个文件压缩

                zos = new ZipOutputStream(new FileOutputStream(destFile));

                zipFile(baseDir, sourceFile, zos);

            }
            else
            {// 文件夹压缩

                List<File> fileList = getSubFiles(sourceFile);

                zos = new ZipOutputStream(new FileOutputStream(destFile));

                for (int i = 0; i < fileList.size(); i++)

                {

                    File subFile = (File) fileList.get(i);

                    zipFile(baseDir, subFile, zos);

                }

            }

        }

        catch (IOException e)

        {

            e.getMessage();

        }

        finally

        {

            IOUtil.closeQuietly(zos);

        }

    }

 

    /**

     * @description 压缩文件

     * @updated by WYY 2013年11月14日 下午10:13:11

     * @param baseDir

     *            基本目录

     * @param file

     *            本次压缩的文件

     * @param zos

     * @throws IOException

     * @throws FileNotFoundException

     */

    private static void zipFile(String baseDir, File file, ZipOutputStream zos) throws IOException,

            FileNotFoundException

    {

        byte[] buf = new byte[BUFFER];

        int readLen = 0;

        ZipEntry ze = new ZipEntry(getAbsFileName(baseDir, file));

        ze.setSize(file.length());

        ze.setTime(file.lastModified());

        zos.putNextEntry(ze);

        InputStream is = new BufferedInputStream(new FileInputStream(file));

        while ((readLen = is.read(buf, 0, BUFFER)) != -1)

        {

            zos.write(buf, 0, readLen);

        }

        IOUtil.closeQuietly(is);

    }

 

    /**

     * 给定根目录，返回另一个文件名的相对路径，用于zip文件中的路径.

     *

     * @param baseDir

     *            java.lang.String 根目录

     * @param realFileName

     *            java.io.File 实际的文件名

     * @return 相对文件名

     */

    private static String getAbsFileName(String baseDir, File realFileName)

    {

        File real = realFileName;

        File base = new File(baseDir);

        String ret = real.getName();

        if (real.equals(base))// baseDir 为文件时，直接返回

        {

            return ret;

        }

        else

        {

            while (true)

            {

                real = real.getParentFile();

                if (real == null)

                    break;

                if (real.equals(base))

                    break;

                else

                    ret = real.getName() + "/" + ret;

            }

        }

        return ret;

    }

 

    /**

     * 取得指定目录下的所有文件列表，包括子目录下的文件.

     *

     * @param baseDir

     *            File 指定的目录

     * @return 包含java.io.File的List

     */

    private static List<File> getSubFiles(File baseDir)

    {

        List<File> ret = new ArrayList<File>();

        File[] tmp = baseDir.listFiles();

        for (int i = 0; i < tmp.length; i++)

        {

            if (tmp[i].isFile())

                ret.add(tmp[i]);

            if (tmp[i].isDirectory())

                ret.addAll(getSubFiles(tmp[i]));

        }

        return ret;

    }

 

    /**
     * 解压缩功能. 将zipFile文件解压到zipDir目录下.
     *

     * @throws Exception

     */

    public static void unzip(String zipDir, String zipFile)

    {

        ZipFile zfile = null;

        InputStream is = null;

        OutputStream os = null;

        try

        {

            zfile = new ZipFile(zipFile);

            Enumeration<?> zList = zfile.getEntries();

            ZipEntry ze = null;

            byte[] buf = new byte[1024];

            while (zList.hasMoreElements())

            {

                ze = (ZipEntry) zList.nextElement();

                if (ze.isDirectory())

                {

                    File f = new File(zipDir + ze.getName());

                    f.mkdir();

                    continue;

                }

                os = new BufferedOutputStream(new FileOutputStream(getRealFileName(zipDir, ze.getName())));

                is = new BufferedInputStream(zfile.getInputStream(ze));

                int readLen = 0;

                while ((readLen = is.read(buf, 0, 1024)) != -1)

                {

                    os.write(buf, 0, readLen);

                }

                IOUtil.closeQuietly(is);

                IOUtil.closeQuietly(os);

            }

            zfile.close();

        }

        catch (IOException e)

        {

            e.printStackTrace();

        }

        finally

        {

            IOUtil.closeQuietly(is);

            IOUtil.closeQuietly(os);

            try

            {

                if (null != zfile)

                {

                    zfile.close();

                }

            }

            catch (IOException ex)

            {

                // ignore

            }

        }

    }

 

    /**

     * 给定根目录，返回一个相对路径所对应的实际文件名.

     *

     * @param baseDir

     *            指定根目录

     * @param absFileName

     *            相对路径名，来自于ZipEntry中的name

     * @return java.io.File 实际的文件

     */

    public static File getRealFileName(String baseDir, String absFileName)

    {

        String[] dirs = absFileName.split("/");

        File ret = new File(baseDir);

        if (dirs.length > 1)

        {

            for (int i = 0; i < dirs.length - 1; i++)

            {

                ret = new File(ret, dirs[i]);

            }

            if (!ret.exists())
                ret.mkdirs();
            ret = new File(ret, dirs[dirs.length - 1]);
            return ret;
        }

        ret = new File(ret, dirs[dirs.length - 1]);

        return ret;
    }
 
    public static void main(String[] args) throws Exception {
        // 测试
        ZipTool.zip("F:/个人/0215/aizanqian", "F:/个人/0215/aizanqian.zip");
        //ZipTool.unzip("F:/个人/0215/aizanqian", "F:/个人/0215/aizanqian.zip");
    }
}

