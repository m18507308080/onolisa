/*
 * Copyright (C), 2014-2014, 佛祖保佑 , 永无BUG
 * FileName: BinaryRedisClientImpl.java
 * Author:   李牧牧
 * Date:     2014年11月14日 下午8:09:08
 * Description:  
 * History: 
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间                     版本号                  描述
 * 李牧牧       2013.11.14  1.0.0
 */
package com.nothin.onolisa.framework.redis.client.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import redis.clients.jedis.Jedis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.nothin.onolisa.framework.redis.client.CallBack;
import com.nothin.onolisa.framework.redis.client.IBinaryRedisClient;
import com.nothin.onolisa.framework.redis.util.SessionStreamUtils;

/**
 * 
 * 〈默认的Binary操作接口实现〉<br> 
 * 〈功能详细描述〉
 *
 * @author 李牧牧
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class BinaryRedisClientImpl<T> extends RedisClientImpl implements IBinaryRedisClient {

    /**
     * 构造方法
     */
    public BinaryRedisClientImpl() {
        super();
    }

    /**
     * 构造方法
     * 
     * @param config 配置信息
     */
    public BinaryRedisClientImpl(String config) {
        super(config);
    }

    /*
     * performFunction private <R> R performFunction(byte[] key, CallBack<R> callBack) { return
     * sharded4Jedis.getShard(key).execute(callBack); } private byte[] encode(Serializable object) { return
     * CacheUtils.encode(object); } private Serializable convertType(byte[] bytes) { try { return
     * Integer.valueOf(SafeEncoder.encode(bytes)); } catch (NumberFormatException e) { return CacheUtils.decode(bytes);
     * } }
     */

    /**
     * 根据key和集合类型获取缓存对象
     * 
     * @param key 键值
     * @param type TypeReference类型
     * @param <T> 泛型对象
     * @return json字符串
     */
    @Override
    public <T> T get(String key, TypeReference<T> type) {
        return JSON.parseObject(get(key), type);
    }

    /**
     * 根据key和集合类型设置缓存对象
     * 
     * @param key 键
     * @param value 值
     * @param <T> 泛型对象
     */
    @Override
    public <T> void set(final String key, final T value) {
        set(key, JSON.toJSONString(value));
    }

    /**
     * 根据key和类的类别获取缓存对象
     * 
     * @param key 键
     * @param value 值
     * @param <T> 泛型对象
     * @return json字符串
     */
    @Override
    public <T> T get(String key, Class<T> value) {
        return JSON.parseObject(get(key), value);
    }

    /**
     * 根据key移出缓存对象
     * 
     * @param key 键值
     * 
     */
    @Override
    public void remove(String key) {
        del(key);
    }

    /**
     * 设置过期时间
     * 
     * @param key 键
     * @param value 值
     * @param time 时间复杂度
     * @param <T> 泛型对象
     */
    @Override
    public <T> void setex(final String key, final T value, final int time) {
        setex(key, time, JSON.toJSONString(value));
    }

    @Override
    public String setObject(final String key, final Object value) {
        return this.performFunction(key, new CallBack<String>() {
            @Override
            public String invoke(Jedis jedis) {
                try {
                    byte[] bkey = SessionStreamUtils.objectToByteArray(key);
                    byte[] bvalue = SessionStreamUtils.objectToByteArray(value);
                    return jedis.set(bkey, bvalue);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    @Override
    public String setexObject(final String key, final Object value, final int time) {
        return this.performFunction(key, new CallBack<String>() {
            @Override
            public String invoke(Jedis jedis) {
                try {
                    byte[] bkey = SessionStreamUtils.objectToByteArray(key);
                    byte[] bvalue = SessionStreamUtils.objectToByteArray(value);                    
                    return jedis.setex(bkey, time, bvalue);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    @Override
    public Object getObject(final String key) {
        return this.performFunction(key, new CallBack<Object>() {

            @Override
            public Object invoke(Jedis jedis) {
                try {
                    byte[] bkey = SessionStreamUtils.objectToByteArray(key);
                    return SessionStreamUtils.byteArrayToObject(jedis.get(bkey));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    @Override
    public void removeObject(final String key) {
        this.performFunction(key, new CallBack<Long>() {
            @Override
            public Long invoke(Jedis jedis) {
                return jedis.del(key);
            }
        });
    }

    @Override
    public Long hsetObject(final String key, final String field, final Object value) {
        return this.performFunction(key, new CallBack<Long>() {
            @Override
            public Long invoke(Jedis jedis) {
                try {
                    byte[] bkey = SessionStreamUtils.objectToByteArray(key);
                    byte[] bfield = SessionStreamUtils.objectToByteArray(field);
                    byte[] bvalue = SessionStreamUtils.objectToByteArray(value);
                    return jedis.hset(bkey, bfield, bvalue);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }         
                return null;
            }
        });
    }

    @Override
    public Object hgetObject(final String key, final String field) {
        return this.performFunction(key, new CallBack<Object>() {

            @Override
            public Object invoke(Jedis jedis) {
                try {
                    byte[] bkey = SessionStreamUtils.objectToByteArray(key);
                    byte[] bfield = SessionStreamUtils.objectToByteArray(field);
                    return SessionStreamUtils.byteArrayToObject(jedis.hget(bkey, bfield));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }  
                return null;
            }
        });
    }
    
    @Override
    public void hdelObject(final String key, final String field) {
        this.performFunction(key, new CallBack<Long>() {

            @Override
            public Long invoke(Jedis jedis) {
                try{
                    byte[] bkey = SessionStreamUtils.objectToByteArray(key);
                    byte[] bfield = SessionStreamUtils.objectToByteArray(field);
                    return jedis.hdel(bkey, bfield);
                } catch(IOException e){   
                    e.printStackTrace();
                }
                return 0L;
            }
        });
    }
    
    @Override
    public Map<String, Object> hgetAllObjects(final String key) {
        return this.performFunction(key, new CallBack<Map<String, Object>>() {

            @Override
            public Map<String, Object> invoke(Jedis jedis) {
                try{
                    byte[] bkey = SessionStreamUtils.objectToByteArray(key);
                    Map<byte[], byte[]> all = jedis.hgetAll(bkey);
                    Map<String, Object> allObjs = new HashMap<String, Object>();
                    for(Entry<byte[], byte[]> item : all.entrySet()){
                        String _key = (String)SessionStreamUtils.byteArrayToObject(item.getKey());
                        Object _value = SessionStreamUtils.byteArrayToObject(item.getValue());
                        allObjs.put(_key, _value);
                    }
                    return allObjs;
                } catch(IOException e){
                    e.printStackTrace();
                }
                return null;
            }
        });
    }
}
