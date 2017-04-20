/*
 * Copyright (C), 2014-2014, 佛祖保佑 , 永无BUG
 * FileName: DefaultJedisPool.java
 * Author:   李牧牧
 * Date:     2014年11月14日 下午8:09:08
 * Description:  
 * History: 
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间                     版本号                  描述
 * 李牧牧       2013.11.14  1.0.0
 */
package com.nothin.onolisa.framework.redis.shard;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.Pool;

/**
 * 
 * 〈支持选择dbIndex的Jedis连接池〉<br> 
 * 〈功能详细描述〉
 *
 * @author 李牧牧
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class DefaultJedisPool extends Pool<Jedis> {
    /**
     * log
     */
    private static Logger logger = LoggerFactory.getLogger(DefaultJedisPool.class);
    /**
     * host
     */
    private String host;
    /**
     * port
     */
    private int port;
    /**
     * dbIndex
     */
    private int dbIndex;
    /**
     * errorCount
     */
    private AtomicInteger errorCount = new AtomicInteger(0);

    /**
     * 是否是master
     */
    private boolean isMaster = true;

    /**
     * 构造方法
     * 
     * @param poolConfig 池配置
     * @param host 主机地址
     * @param port 端口
     * @param timeout 超时时间
     * @param password 密码
     * @param dbIndex 索引
     * @param isMaster 是否是master
     */
    public DefaultJedisPool(final Config poolConfig, final String host, int port, int timeout, final String password,
            int dbIndex, boolean isMaster) {
        super(poolConfig, new JedisFactory(host, port, timeout, password, dbIndex));
        this.host = host;
        this.port = port;
        this.dbIndex = dbIndex;
        this.isMaster = isMaster;
    }

    /**
     * 
     * 功能描述：是否是master
     * 
     * @return boolean 返回值
     */
    public boolean isMaster() {
        return isMaster;
    }

    /**
     * 返回错误数量
     * 
     * @return AtomicInteger 错误数量
     */
    public AtomicInteger getErrorCount() {
        return errorCount;
    }

    /**
     * 
     * 功能描述：判断是否已经销毁
     * 
     * @return boolean 返回值
     * @throw 异常描述
     */
    public boolean isDestroyed() throws Exception {
        GenericObjectPool internalPool = getInternalPool();
        return internalPool.getNumActive() + internalPool.getNumIdle() == 0 ? true : false;
    }

    /**
     * 获取父类的成员变量
     * 
     * @return GenericObjectPool 父类的成员变量
     * @throws Exception 异常
     */
    public GenericObjectPool getInternalPool() throws Exception {
        Class<?> father = this.getClass().getSuperclass();
        Field f = father.getDeclaredField("internalPool");
        f.setAccessible(true);
        return (GenericObjectPool) f.get(this);
    }

    /**
     * toString 方法[host=" + host + ", port=" + port + ", dbIndex=" + dbIndex + "]
     * @return string
     */
    @Override
    public String toString() {
        return "[host=" + host + ", port=" + port + ", dbIndex=" + dbIndex + "]";
    }

    /**
     * 返回对象的hashcode
     * @return hash code
     */
    @Override
    public int hashCode() {
        return ObjectUtils.hashCodeMulti(host, port, dbIndex);
    }

    /**
     * 对象比较
     * 
     * @param obj 被比较的对象
     * @return 比较结果
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DefaultJedisPool) {
            DefaultJedisPool that = (DefaultJedisPool) obj;
            return ObjectUtils.equals(host, that.host) && ObjectUtils.equals(port, that.port)
                    && ObjectUtils.equals(dbIndex, that.dbIndex);
        } else {
            return false;
        }
    }

    /**
     * 
     * PoolableObjectFactory custom impl.
     * 
     * @author pinzhao
     */
    private static class JedisFactory extends BasePoolableObjectFactory {
        /**
         * host
         */
        private final String host;
        /**
         * port
         */
        private final int port;
        /**
         * timeout
         */
        private final int timeout;
        /**
         * password
         */
        private final String password;
        /**
         * dbIndex
         */
        private final int dbIndex;

        /**
         * JedisFactory constructor
         * 
         * @param host redis服务器ip
         * @param port redis服务器端口
         * @param timeout  超时时间
         * @param password 密码
         * @param dbIndex dbindex
         */
        public JedisFactory(final String host, final int port, final int timeout, final String password, int dbIndex) {
            super();
            this.host = host;
            this.port = port;
            this.timeout = (timeout > 0) ? timeout : -1;
            this.password = password;
            this.dbIndex = dbIndex;
        }

        /**
         * 
         * 创建新连接对象
         * 
         * @return Object 返回值
         * @exception Exception 异常
         */
        public Object makeObject() throws Exception {
            final Jedis jedis;
            if (timeout > 0) {
                jedis = new Jedis(this.host, this.port, this.timeout);
            } else {
                jedis = new Jedis(this.host, this.port);
            }
            jedis.connect();
            if (!StringUtils.isBlank(this.password)) {
                jedis.auth(this.password);
            }

            if (dbIndex != 0) {
                jedis.select(dbIndex);
            }
            return jedis;
        }

        /**
         * 
         * 销毁连接对象
         * 
         * @param obj 对象
         */
        public void destroyObject(final Object obj) {
            if (obj instanceof Jedis) {
                final Jedis jedis = (Jedis) obj;
                if (jedis.isConnected()) {
                    try {
                        try {
                            jedis.quit();
                        } catch (JedisException e) {
                            logger.error(e.getMessage());
                        }
                        jedis.disconnect();
                    } catch (JedisException e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        }

        /**
         * 
         * 验证连接是否可用
         * 
         * @param obj 对象
         * @return boolean 返回值
         */
        public boolean validateObject(final Object obj) {
            if (obj instanceof Jedis) {
                final Jedis jedis = (Jedis) obj;
                try {
                    return jedis.isConnected() && jedis.ping().equals("PONG");
                } catch (JedisException e) {
                    logger.error(e.getMessage());
                    return false;
                }
            } else {
                return false;
            }
        }
    }

}
