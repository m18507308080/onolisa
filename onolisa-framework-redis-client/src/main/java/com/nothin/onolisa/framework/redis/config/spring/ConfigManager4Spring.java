/*
 * Copyright (C), 2014-2014, 佛祖保佑 , 永无BUG
 * FileName: ConfigManager4Spring.java
 * Author:   李牧牧
 * Date:     2014年11月14日 下午8:09:08
 * Description:  
 * History: 
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间                     版本号                  描述
 * 李牧牧       2013.11.14  1.0.0
 */
package com.nothin.onolisa.framework.redis.config.spring;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

import com.nothin.onolisa.framework.redis.config.ConfigManager;
import com.nothin.onolisa.framework.redis.shard.NodeInfo4Jedis;
import com.nothin.onolisa.framework.redis.shard.ShardInfo4Jedis;

/**
 * 
 * 〈配置信息管理类〉<br> 
 * 〈功能详细描述〉
 *
 * @author 李牧牧
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class ConfigManager4Spring extends ConfigManager {
    /**
     * 重试次数
     */
    public static final int RETRYTIMES = 3;
    /**
     * 默认ip
     */
    public static final String DEFAULT_HOST = "127.0.0.1";
    /**
     * 默认端口
     */
    public static final String DEFAULT_PORT = "6379";

    /**
     * 默认过期时间
     */
    public static final String DEFAULT_TIMEOUT_IN_MILL_SECONDS = "2000";

    /**
     * 默认索引初始值
     */
    public static final String DEFAULT_DB_INDEX = "0";

    /**
     * ShardInfo4Jedis列表
     */
    protected List<ShardInfo4Jedis> lstInfo4Jedis;

    /**
     * pool配置
     */
    private PoolConfig poolConfig;
    /**
     * shard配置
     */
    private ShardConfig shardConfig;

    /**
     * 
     * 功能描述: <br>
     * getShardConfig
     * 
     * @return ShardConfig 参数说明
     */
    public ShardConfig getShardConfig() {
        return shardConfig;
    }

    /**
     * 构造方法
     * 
     * @param shardConfig 配置
     */
    public void setShardConfig(ShardConfig shardConfig) {
        this.shardConfig = shardConfig;
    }

    /**
     * shard flag
     */
    private boolean isSharding;

    /**
     * ConfigManager4Spring constructor
     */
    public ConfigManager4Spring() {

    }

    /**
     * 
     * 功能描述: <br>
     * getPoolConfig
     * 
     * @return PoolConfig 参数说明
     */
    public PoolConfig getPoolConfig() {
        return poolConfig;
    }

    /**
     * 设置池配置
     * 
     * @param poolConfig 配置
     */
    public void setPoolConfig(PoolConfig poolConfig) {
        this.poolConfig = poolConfig;
    }

    /**
     * 获取分片的Redis节点列表信息
     * 
     * @return List<ShardInfo4Jedis> 分片的Redis节点列表信息
     */
    public List<ShardInfo4Jedis> getLstInfo4Jedis() {
        return lstInfo4Jedis;
    }

    /**
     * 
     * 功能描述：是否配置分片
     * 
     * @return boolean 返回值
     */
    public boolean isSharding() {
        return isSharding;
    }

    /**
     * 
     * 功能描述： 加载配置文件
     * 
     */
    public synchronized void loadConfig() {
        parserWithDoc();
    }

    /**
     * 
     * 功能描述：解析配置文件
     * 
     * @param shardConfig 参数说明 返回值: 类型 <说明>
     */
    @SuppressWarnings("unchecked")
    private void parseShardingConfig(ShardConfig shardConfig) {
        lstInfo4Jedis = new ArrayList<ShardInfo4Jedis>();
        Config config = new Config();
        setPoolParameters(poolConfig, config);
        shardConfig.setConfig(config);
        for (ShardInfo4Jedis shardInfo : shardConfig.getShards()) {
            lstInfo4Jedis.add(shardInfo);
            for (NodeInfo4Jedis node : shardInfo.getNodes()) {
                node.setConfig(shardConfig.getConfig());
            }
        }

        isSharding = lstInfo4Jedis.size() > 1 ? true : false;
    }

    /**
     * 
     * 功能描述： parserWithDoc
     * 
     */
    private void parserWithDoc() {
        parseShardingConfig(shardConfig);

    }

    /**
     * 处理连接池信息
     * 
     * @param poolConfig 参数说明
     * @param config 参数说明
     */
    private void setPoolParameters(PoolConfig poolConfig, Config config) {

        if (!StringUtils.isBlank(poolConfig.getTestOnBorrow())) {
            // 获取连接池是否检测可用性
            config.testOnBorrow = Boolean.valueOf(poolConfig.getTestOnBorrow());
        }

        if (!StringUtils.isBlank(poolConfig.getTestOnReturn())) {
            // 归还时是否检测可用性
            config.testOnReturn = Boolean.valueOf(poolConfig.getTestOnReturn());
        }
        if (!StringUtils.isBlank(poolConfig.getTestWhileIdle())) {
            // 空闲时是否检测可用性
            config.testWhileIdle = Boolean.valueOf(poolConfig.getTestWhileIdle());
        } else {
            config.testWhileIdle = true;
        }
        if (!StringUtils.isBlank(poolConfig.getWhenExhaustedAction())) {
            config.whenExhaustedAction = Byte.valueOf(poolConfig.getWhenExhaustedAction());
        }
        if (!StringUtils.isBlank(poolConfig.getTimeBetweenEvictionRunsMillis())) {
            config.timeBetweenEvictionRunsMillis = Long.valueOf(poolConfig.getTimeBetweenEvictionRunsMillis());
        } else {
            config.timeBetweenEvictionRunsMillis = 30000L;
        }
        if (!StringUtils.isBlank(poolConfig.getNumTestsPerEvictionRun())) {
            config.numTestsPerEvictionRun = Integer.valueOf(poolConfig.getNumTestsPerEvictionRun());
        } else {
            config.numTestsPerEvictionRun = -1;
        }
        if (!StringUtils.isBlank(poolConfig.getMinEvictableIdleTimeMillis())) {
            config.minEvictableIdleTimeMillis = Integer.valueOf(poolConfig.getMinEvictableIdleTimeMillis());
        } else {
            config.minEvictableIdleTimeMillis = 60000L;
        }
        if (!StringUtils.isBlank(poolConfig.getSoftMinEvictableIdleTimeMillis())) {
            config.softMinEvictableIdleTimeMillis = Integer.valueOf(poolConfig.getSoftMinEvictableIdleTimeMillis());
        }
    }

}
