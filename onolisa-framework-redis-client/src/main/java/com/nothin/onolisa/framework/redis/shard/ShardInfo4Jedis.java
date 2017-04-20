/*
 * Copyright (C), 2014-2014, 佛祖保佑 , 永无BUG
 * FileName: ShardInfo4Jedis.java
 * Author:   李牧牧
 * Date:     2014年11月14日 下午8:09:08
 * Description:  
 * History: 
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间                     版本号                  描述
 * 李牧牧       2013.11.14  1.0.0
 */
package com.nothin.onolisa.framework.redis.shard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;

/**
 * 
 * 〈一个分片的Redis节点所有信息〉<br> 
 * 〈功能详细描述〉
 *
 * @author 李牧牧
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class ShardInfo4Jedis extends DefaultShardInfo<ShardHandler> {

    /**
     * shard的唯一标识
     */
    private String shardName;

    /**
     * 一个分片的Redis节点所有信息
     */
    private Set<NodeInfo4Jedis> nodes = new HashSet<NodeInfo4Jedis>();

    /**
     * 
     * ShardInfo4Jedis constructor
     */
    public ShardInfo4Jedis() {

    }

    /**
     * 构造方法
     * 
     * @param shardName 分片名
     * @param nodes 节点
     */
    public ShardInfo4Jedis(String shardName, Collection<NodeInfo4Jedis> nodes) {
        this.shardName = shardName;
        this.nodes.addAll(nodes);
    }

    /**
     * 
     * 返回分片名称
     * 
     * @return String 返回分片名称
     */
    @Override
    public String getName() {
        return shardName;
    }

    /**
     * 
     * toString
     * 
     * @return String 分片信息 shardName + nodes
     */
    @Override
    public String toString() {
        return "ShardInfo [shardName=" + shardName + ", nodes=" + nodes + "]";
    }

    /**
     * 
     * 返回hashCode
     * 
     * @return int hash编码
     */
    @Override
    public int hashCode() {
        return ObjectUtils.hashCodeMulti(shardName, nodes);
    }

    /**
     * 获取分片名称
     * 
     * @return String 分片名称
     */
    public String getShardName() {
        return shardName;
    }

    /**
     * 获取节点
     * 
     * @return Set<NodeInfo4Jedis> 节点
     */
    public Set<NodeInfo4Jedis> getNodes() {
        return nodes;
    }

    /**
     * 设置分片名称
     * 
     * @param shardName 分片名称
     */
    public void setShardName(String shardName) {
        this.shardName = shardName;
    }

    /**
     * 设置节点
     * 
     * @param nodes 节点
     */
    public void setNodes(Set<NodeInfo4Jedis> nodes) {
        this.nodes = nodes;
    }

    /**
     * 
     * equals
     * 
     * @param obj 比较对象
     * @return boolean 比较结果
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ShardInfo4Jedis) {
            ShardInfo4Jedis that = (ShardInfo4Jedis) obj;
            return ObjectUtils.equals(nodes, that.nodes) && ObjectUtils.equals(shardName, that.shardName);
        } else {
            return false;
        }
    }

    /**
     * 创建本shard的处理资源
     * 
     * @return ShardHandler 本shard的处理资源
     */
    protected ShardHandler createResource() {
        List<DefaultJedisPool> masterPools = new ArrayList<DefaultJedisPool>();
        for (NodeInfo4Jedis nodeInfo : nodes) {
            DefaultJedisPool jedisPool = new DefaultJedisPool(nodeInfo.getConfig(), nodeInfo.getIp(),
                    nodeInfo.getPort(), nodeInfo.getTimeOut(), nodeInfo.getPassword(), nodeInfo.getDbIndex(), true);
            masterPools.add(jedisPool);
        }
        return new ShardHandler(masterPools, this);
    }
}
