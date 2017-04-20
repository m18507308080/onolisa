/*
 * Copyright (C), 2014-2014, 佛祖保佑 , 永无BUG
 * FileName: DefaultSpringClientImpl.java
 * Author:   李牧牧
 * Date:     2014年11月14日 下午8:09:08
 * Description:  
 * History: 
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间                     版本号                  描述
 * 李牧牧       2013.11.14  1.0.0
 */
package com.nothin.onolisa.framework.redis.client.impl.spring;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.nothin.onolisa.framework.redis.config.spring.ConfigManager4Spring;
import com.nothin.onolisa.framework.redis.shard.ShardHandler;
import com.nothin.onolisa.framework.redis.shard.Sharded4Jedis;

/**
 * 
 * 〈默认的springclient 实现〉<br> 
 * 〈功能详细描述〉
 *
 * @author 李牧牧
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class DefaultSpringClientImpl {
    /**
     * 字符串常量
     */
    public static final String UNSUPPORT = "Current configuration does not support this operation";
    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(DefaultSpringClientImpl.class);
    /**
     * 分片
     */
    protected Sharded4Jedis sharded4Jedis;

    /**
     * 配置管理器
     */
    @Autowired
    protected ConfigManager4Spring configManager4Spring;

    /**
     * 返回配置管理器
     * @return 配置管理器
     */
    public ConfigManager4Spring getConfigManager4Spring() {
        return configManager4Spring;
    }

    /**
     * 构造方法
     * 
     * @param configManager4Spring spring配置
     */
    public void setConfigManager4Spring(ConfigManager4Spring configManager4Spring) {
        this.configManager4Spring = configManager4Spring;
    }

    /**
     * 构造方法
     */
    public DefaultSpringClientImpl() {

    }

    /**
     * 判断是否是分片
     * @return 判断结果
     */
    protected boolean isSharding() {
        return configManager4Spring.isSharding();
    }

    /**
     * 功能描述：初始化
     */
    protected synchronized void init() {
        /**加载配置*/
        configManager4Spring.loadConfig();
        sharded4Jedis = new Sharded4Jedis(configManager4Spring.getLstInfo4Jedis());
    }

    /**
     * 刷新配置
     * 
     * @param config 配置信息
     */
    public synchronized void refresh(String config) {
        Sharded4Jedis old = sharded4Jedis;
        if (!StringUtils.isBlank(config)) {
            configManager4Spring.setConfig(config);
        }
        this.init();
        // 销毁旧池
        Collection<ShardHandler> allShards = old.getAllShards();
        for (ShardHandler shardHandler : allShards) {
            shardHandler.destroy();
        }
    }

    /**
     * 
     * 功能描述: <br>
     * 刷新所有数据
     * 
     * @return 返回刷新结果
     */
    public String flushDB() {
        Collection<ShardHandler> allShards = sharded4Jedis.getAllShards();
        final CountDownLatch endSignal = new CountDownLatch(allShards.size());
        for (final ShardHandler shard : allShards) {
            // 多线程同时flushDB 提高效率
            new Thread(new Runnable() {
                @Override
                public void run() {
                    shard.flushShard();
                    endSignal.countDown();
                }
            }).start();
        }
        try {
            endSignal.await();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
        return "OK";
    }

    /**
     * 
     * 功能描述: <br>
     * 清除节点不可用的标记
     * 
     */
    public void clearDisableFlags() {
        // 所有shard中所有连接池
        Collection<ShardHandler> allShards = sharded4Jedis.getAllShards();
        for (ShardHandler shard : allShards) {
            shard.clearDisableFlags();
        }
    }
}
