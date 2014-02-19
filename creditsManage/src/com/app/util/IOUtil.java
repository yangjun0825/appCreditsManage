package com.app.util;

import java.io.Closeable;
import java.io.IOException;
 
/**
 * @author WYY
 * @description
 */
public class IOUtil
{
 
    /**
     * @description 关闭流
     * @updated by WYY 2013年11月14日 下午10:10:25
     * @param closeable
     */
    public static void closeQuietly(Closeable closeable)
    {
        try
        {
            if (null != closeable)
            {
                closeable.close();
            }
        }
        catch (IOException e)
        {
            // ignore
        }
    }
}

