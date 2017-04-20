/*
 * Copyright (C), 2014-2014, 佛祖保佑 , 永无BUG
 * FileName: SpringBinaryRedisClientImpl.java
 * Author:   李牧牧
 * Date:     2014年11月14日 下午8:09:08
 * Description:  
 * History: 
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间                     版本号                  描述
 * 李牧牧       2013.11.14  1.0.0
 */
package com.nothin.onolisa.framework.redis.client.impl.spring;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.nothin.onolisa.framework.redis.client.CallBack;
import com.nothin.onolisa.framework.redis.client.IBinaryRedisClient;
import com.nothin.onolisa.framework.redis.util.SessionStreamUtils;

/**
 * 
 * 
 * 〈默认的Binary操作接口实现〉<br> 
 * 〈功能详细描述〉
 *
 * @author 李牧牧
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class SpringBinaryRedisClientImpl<T> extends SpringRedisClientImpl implements IBinaryRedisClient {

    private final Logger logger = LoggerFactory.getLogger(SpringBinaryRedisClientImpl.class);

    /**
     * DefSNBinaryRedisClient
     */
    public SpringBinaryRedisClientImpl() {
        super();
    }

    /**
     * 
     * 功能描述: 此API用于存取集合类型的数据，比如：Map<String,Object>, List<Object> 客户端的使用方式（示例）为：redisClient.get("user",new
     * TypeReference<Map<String, long[]>>(){}); 对应的set方式为：public <T> void set(final String key, final T value)
     * 
     * @param key 指定的key
     * @param type 集合类型
     * @param <T> 泛型对象
     * @return 集合类型
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public <T> T get(String key, TypeReference<T> type) {
        return JSON.parseObject(get(key), type);
    }

    /**
     * 功能描述: 用于存放JAVA简单的类对象的数据，如:put(userId,user) 〈功能详细描述〉
     * 
     * @param key 键值
     * @param value 对象
     * @param <T> 泛型对象
     */
    @Override
    public <T> void set(final String key, final T value) {
        set(key, JSON.toJSONString(value));
    }

    /**
     * 功能描述: 用于访问JAVA简单的类对象的数据，如:get(userId,User.class) 〈功能详细描述〉
     * 
     * @param key 键值
     * @param value java类型
     * @param <T> 泛型对象
     * @return JAVA简单的类对象的数据
     */
    @Override
    public <T> T get(String key, Class<T> value) {
        return JSON.parseObject(get(key), value);
    }

    /**
     * 功能描述: 移除对象
     * 
     * @param key 指定的键值
     */
    @Override
    public void remove(String key) {
        del(key);
    }

    /**
     * 设置集合对象
     * 
     * @param key 指定的key
     * @param value 集合对象
     * @param time 过期时间
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
                    logger.debug(key + " put in the redis");
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
                    logger.debug(key + " put in the redis and expired after " + time + " seconds");
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
                    logger.debug("get the object from redis with key " + key);
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
                logger.debug("remove the object from the redis with key " + key);
                try {
                    byte[] bkey = SessionStreamUtils.objectToByteArray(key);
                    return jedis.del(bkey);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return -1L;
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
                    logger.debug(key + ":" + field + " put in the redis");
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
                    logger.info("get the object from the redis with key " + key + ":" + field);
                    return SessionStreamUtils.byteArrayToObject(jedis.hget(bkey, bfield));
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
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
                try {
                    byte[] bkey = SessionStreamUtils.objectToByteArray(key);
                    byte[] bfield = SessionStreamUtils.objectToByteArray(field);
                    logger.debug("remove the object from redis with key " + key + ":" + field);
                    return jedis.hdel(bkey, bfield);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return 0L;
            }
        });
    }

    @Override
    public Long expire(final String key, final int seconds) {
        return this.performFunction(key, new CallBack<Long>() {

            @Override
            public Long invoke(Jedis jedis) {
                try {
                    byte[] bkey = SessionStreamUtils.objectToByteArray(key);
                    logger.debug("object with key " + key + " expired after " + seconds + " seconds");
                    return jedis.expire(bkey, seconds);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
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
                try {
                    byte[] bkey = SessionStreamUtils.objectToByteArray(key);
                    Map<byte[], byte[]> all = jedis.hgetAll(bkey);
                    Map<String, Object> allObjs = new HashMap<String, Object>();
                    logger.debug("get all the objects with key : " + key);
                    for (Entry<byte[], byte[]> item : all.entrySet()) {
                        if (item.getKey() != null && item.getValue() != null && item != null) {
                            String _key = (String) SessionStreamUtils.byteArrayToObject(item.getKey());
                            Object _value = SessionStreamUtils.byteArrayToObject(item.getValue());
                            allObjs.put(_key, _value);
                        } else {
                            logger.debug(item.getKey() + ":" + item.getValue());
                        }
                    }
                    return allObjs;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

}
