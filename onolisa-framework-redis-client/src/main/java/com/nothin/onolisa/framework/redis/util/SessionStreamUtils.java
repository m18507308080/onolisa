/*
 * Copyright (C), 2014-2014, 佛祖保佑 , 永无BUG
 * FileName: SessionStreamUtils.java
 * Author:   李牧牧
 * Date:     2014年11月14日 下午8:09:08
 * Description:  
 * History: 
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间                     版本号                  描述
 * 李牧牧       2013.11.14  1.0.0
 */
package com.nothin.onolisa.framework.redis.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 〈SessionStreamUtils〉<br> 
 * 〈功能详细描述〉
 *
 * @author 李牧牧
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class SessionStreamUtils {
    
    private final static Logger logger = LoggerFactory.getLogger(SessionStreamUtils.class);

    /**
     * 5000 constant
     */
    private static final int FIVE =5000;
    
    /**
     * constructor
     */
    private SessionStreamUtils() {
    }
    
    /**
     * 
     * 功能描述:对象转换成自己数组 <br>
     * 〈功能详细描述〉
     * 
     * @param obj obj
     * @return byte
     * @throws IOException io exception
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public static byte[] objectToByteArray(Object obj) throws IOException {
        if (obj == null) {
            return null;
        }
        ObjectOutputStream os = null;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(FIVE);
        os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
        os.flush();
        os.writeObject(obj);
        os.flush();
        byte[] sendBuf = byteStream.toByteArray();
        os.close();
        return sendBuf;
    }

    /**
     * 
     * 
     * @param bytes
     * @return
     * @throws IOException
     */
    /**
     * 
     * 功能描述:自己数组转换成对象 <br>
     * 〈功能详细描述〉
     *
     * @param bytes bytes
     * @return object
     * @throws IOException ioexception
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public static Object byteArrayToObject(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(bis));
            obj = ois.readObject();
            bis.close();
            ois.close();
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
        }
        return obj;
    }
}
