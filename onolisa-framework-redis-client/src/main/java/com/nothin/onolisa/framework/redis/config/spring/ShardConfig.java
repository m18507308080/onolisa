/*
 * Copyright (C), 2014-2014, 佛祖保佑 , 永无BUG
 * FileName: ShardConfig.java
 * Author:   李牧牧
 * Date:     2014年11月14日 下午8:09:08
 * Description:  
 * History: 
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间                     版本号                  描述
 * 李牧牧       2013.11.14  1.0.0
 */
package com.nothin.onolisa.framework.redis.config.spring;

import java.util.List;

import org.apache.commons.pool.impl.GenericObjectPool.Config;

import com.nothin.onolisa.framework.redis.shard.ShardInfo4Jedis;

/**
 * 
 * 〈分片配置〉<br> 
 * 〈功能详细描述〉
 *
 * @author 李牧牧
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class ShardConfig {

    /**
     * shardConfigName
     */
    private String shardConfigName;

    /**
     * shards
     */
    private List<ShardInfo4Jedis> shards;
    /**
     * config
     */
    private Config config;

    /**
     * 获取分片配置
     * 
     * @return Config 分片配置
     */
    public Config getConfig() {
        return config;
    }

    /**
     * 设置分片配置
     * 
     * @param config 分片配置
     */
    public void setConfig(Config config) {
        this.config = config;
    }

    /**
     * 获取分片列表
     * 
     * @return List<ShardInfo4Jedis> 分片列表
     */
    public List<ShardInfo4Jedis> getShards() {
        return shards;
    }

    /**
     * 设置分片列表
     * 
     * @param shards 设置分片列表
     */
    public void setShards(List<ShardInfo4Jedis> shards) {
        this.shards = shards;
    }

    /**
     * 获取分片配置名
     * 
     * @return String 分片配置名
     */
    public String getShardConfigName() {
        return shardConfigName;
    }

    /**
     * 设置分片配置名
     * 
     * @param shardConfigName 分片配置名
     */
    public void setShardConfigName(String shardConfigName) {
        this.shardConfigName = shardConfigName;
    }

}
