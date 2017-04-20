/*
 * Copyright (C), 2014-2014, 佛祖保佑 , 永无BUG
 * FileName: CacheUtils.java
 * Author:   李牧牧
 * Date:     2014年11月14日 下午8:09:08
 * Description:  
 * History: 
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间                     版本号                  描述
 * 李牧牧       2013.11.14  1.0.0
 */
package com.nothin.onolisa.framework.redis.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nothin.onolisa.framework.redis.exception.RedisClientException;

import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.SafeEncoder;

/**
 * 
 * 〈缓存工具类〉<br> 
 * 〈功能详细描述〉
 *
 * @author 李牧牧
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class CacheUtils {
    /**
     * log
     */
    private static Logger logger = LoggerFactory.getLogger(CacheUtils.class);

    /**
     * 
     * 功能描述： list转换成数组
     * 
     * @param serializables 参数说明 返回值: 类型 <说明>
     * @return byte[][] 返回值
     */
    public static byte[][] blistToArray(String... serializables) {
        byte[][] paramByte = null;
        if (serializables != null && serializables.length > 0) {
            paramByte = new byte[serializables.length][0];
            for (int i = 0; i < serializables.length; i++) {
                paramByte[i] = encode(serializables[i]);
            }
        }
        return paramByte;
    }

    /**
     * 
     * 功能描述：listToArray
     * 
     * @param serializables 参数说明 返回值: 类型 <说明>
     * @return byte[][] 返回值
     */
    public static byte[][] listToArray(Serializable... serializables) {
        byte[][] paramByte = null;
        if (serializables != null && serializables.length > 0) {
            paramByte = new byte[serializables.length][0];
            for (int i = 0; i < serializables.length; i++) {
                paramByte[i] = encode(serializables[i]);
            }
        }
        return paramByte;
    }

    /**
     * 将序列化数组转成字符串数组
     * 
     * @param fields serializables数组
     * @return String[] 或null
     */
    public static String[] slistToArray(Serializable... fields) {
        if (fields != null) {
            String[] returnAry = new String[fields.length];
            for (int i = 0; i < fields.length; i++) {
                returnAry[i] = cast(fields[i]);
            }
            return returnAry;
        }
        return null;
    }

    /**
     * 
     * 功能描述：cast
     * 
     * @param value 参数说明 返回值: 类型 <说明>
     * @return String 返回值
     */
    public static String cast(Serializable value) {
        try {
            return (String) value;
        } catch (ClassCastException e) {
            throw new RedisClientException("Cast to String error.", e);
        }
    }

    /**
     * 将包含byte数组键值对的Map集合转成字符串数组
     * 
     * @param map 参数说明
     * @return String[] 或null
     */
    public static String[] smapToArray(Map<String, String> map) {
        String[] paramByte = null;
        if (map != null && map.size() > 0) {
            paramByte = new String[map.size() * 2];
            Iterator<Entry<String, String>> it = map.entrySet().iterator();
            int index = 0;
            while (it.hasNext()) {
                Entry<String, String> entry = it.next();
                paramByte[index++] = entry.getKey();
                paramByte[index++] = entry.getValue();
            }
        }
        return paramByte;
    }

    /**
     * 将包含byte数组键值对的Map集合转成二维byte数组
     * 
     * @param map 参数说明
     * @return byte[][] 或null
     */
    public static byte[][] mapToArray(Map<byte[], byte[]> map) {
        byte[][] paramByte = null;
        if (map != null && map.size() > 0) {
            paramByte = new byte[map.size() * 2][0];
            Iterator<Entry<byte[], byte[]>> it = map.entrySet().iterator();
            int index = 0;
            while (it.hasNext()) {
                Entry<byte[], byte[]> entry = it.next();
                paramByte[index++] = entry.getKey();
                paramByte[index++] = entry.getValue();
            }
        }
        return paramByte;
    }

    /**
     * 将byte[]数据反序列化成对象
     * 
     * @param bytes 参数说明
     * @return Serializable 返回值
     */
    public static Serializable decode(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        Serializable object = null;
        ObjectInputStream objectIS = null;
        ByteArrayInputStream byteIS = null;
        try {
            byteIS = new ByteArrayInputStream(bytes);
            objectIS = new ObjectInputStream(byteIS);
            object = (Serializable) objectIS.readObject();
        } catch (IOException e) {
            try {
                object = SafeEncoder.encode(bytes);
            } catch (JedisException e1) {
                throw new RedisClientException(e1);
            }
        } catch (ClassNotFoundException e) {
            try {
                object = SafeEncoder.encode(bytes);
            } catch (JedisException e1) {
                throw new RedisClientException(e1);
            }
        } finally {
            if (byteIS != null) {
                try {
                    byteIS.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
            if (objectIS != null) {
                try {
                    objectIS.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return object;
    }

    /**
     * 将对象序列化成byte[]数组
     * 
     * @param object 参数说明
     * @return byte[] 返回值
     */
    public static byte[] encode(Serializable object) {
        ByteArrayOutputStream byteOS = null;
        ObjectOutputStream objectOS = null;

        byte[] bytes = null;
        if (object instanceof Integer) {
            bytes = SafeEncoder.encode(((Integer) object).toString());
        } else if (object instanceof Long) {
            bytes = SafeEncoder.encode(((Long) object).toString());
        }

        if (bytes != null) {
            return bytes;
        }
        try {
            byteOS = new ByteArrayOutputStream();
            objectOS = new ObjectOutputStream(byteOS);
            objectOS.writeObject(object);
            bytes = byteOS.toByteArray();
        } catch (IOException e) {
            throw new RedisClientException(e);
        } finally {
            if (objectOS != null) {
                try {
                    objectOS.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
            if (byteOS != null) {
                try {
                    byteOS.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return bytes;
    }

    /**
     * 
     * 功能描述：toSMap
     * 
     * @param map 参数说明 返回值: 类型 <说明>
     * @return Map<String, String> 返回值
     */
    public static Map<String, String> toSMap(Map<String, String> map) {
        if (map != null) {
            Map<String, String> returnMap = new HashMap<String, String>();
            Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, String> entry = iterator.next();
                returnMap.put(entry.getKey(), cast(entry.getValue()));
            }
            return returnMap;
        }
        return null;
    }
}
