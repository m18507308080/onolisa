/*
 * Copyright (C), 2014-2014, 佛祖保佑 , 永无BUG
 * FileName: IRedisClient.java
 * Author:   李牧牧
 * Date:     2014年11月14日 下午8:09:08
 * Description:  
 * History: 
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间                     版本号                  描述
 * 李牧牧       2013.11.14  1.0.0
 */
package com.nothin.onolisa.framework.redis.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.ZParams;

/**
 * 
 * 〈操作接口定义〉<br> 
 * 〈功能详细描述〉
 *
 * @author 李牧牧
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface IRedisClient extends JedisCommands {
    /**
     * 功能描述： 删除该key
     * 
     * @param key 指定的key
     * @return Long 被成功删除的个数
     */
    Long del(String key);

    /**
     * 功能描述： lpush元素
     * 
     * @param key 指定的key
     * @param fields 被插入的元素
     * @return Long 被成功插入的个数
     */
    Long lpush(String key, String... fields);

    /**
     * 功能描述： rpush元素
     * 
     * @param key 指定的key
     * @param fields 被插入的元素
     * @return Long 被成功插入的个数
     */
    Long rpush(String key, String... fields);

    /**
     * 功能描述： 批量set数据
     * 
     * @param keyValues 键值对
     * @return set成功与否的状态码
     */
    String mset(Map<String, String> keyValues);

    /**
     * 功能描述：返回key集合
     * 
     * @param pattern 模式
     * @return key集合
     */
    Set<String> keys(String pattern);

    /**
     * 功能描述： 批量获取数据
     * 
     * @param keys 指定的key
     * @return 数据集合
     */
    List<String> mget(String... keys);

    /**
     * 功能描述：刷新配置 重建连接
     * 
     * @param config 参数配置
     */
    void refresh(String config);

    /**
     * 功能描述： 清空数据
     * 
     * @return 清空结果
     */
    String flushDB();

    /**
     * 功能描述: <br>
     * 求多个集合的交集
     * 
     * @param keys 指定的集合
     * @return 交集的集合，不存在时为空
     */
    Set<String> sinter(String... keys);
    
    
    
    /**
     * Set the string value as value of the key. The string can't be longer than
     * 1073741824 bytes (1 GB).
     * @param key
     * @param value
     * @param nxxx NX|XX, NX -- Only set the key if it does not already exist.
     *                    XX -- Only set the key if it already exist.
     * @param expx EX|PX, expire time units: EX = seconds; PX = milliseconds
     * @param time expire time in the units of {@param #expx}
     * @return Status code reply
     */
    public String set(final String key, final String value, final String nxxx, final String expx, final long time);
    
    

    /**
     * 功能描述: 求多个集合的交集，并把结果存在dstkey中，dstkey已存在则覆盖
     * 
     * @param dstkey 结果集
     * @param keys 目标集
     * @return 结果集中的成员数量。
     */
    Long sinterstore(String dstkey, String... keys);

    /**
     * 功能描述: <br>
     * 求多个集合的并集
     * 
     * @param keys 目标集
     * @return 并集的集合，不存在时为空
     */
    Set<String> sunion(String... keys);

    /**
     * 功能描述: <br>
     * 求多个集合的并集，并把结果存在dstkey中，dstkey已存在则覆盖
     * 
     * @param dstkey 结果及
     * @param keys 目标集
     * @return 结果集中的成员数量。
     */
    Long sunionstore(String dstkey, String... keys);

    /**
     * 功能描述: <br>
     * 求参数里第一个set与其他set的差集
     * 
     * @param keys 目标集
     * @return 差集的集合，不存在时为空
     */
    Set<String> sdiff(String... keys);

    /**
     * 功能描述: <br>
     * 求参数里第一个set与其他set的差集，并把结果存在dstkey中，dstkey已存在则覆盖
     * 
     * @param dstkey 结果集
     * @param keys 目标集
     * @return 结果集中的成员数量。
     */
    Long sdiffstore(String dstkey, String... keys);

    /**
     * 功能描述: <br>
     * 求多个有序集合的交集，并把结果存在dstkey中，dstkey已存在则覆盖 score值默认按照权重为1，累加。
     * 
     * @param dstkey 结果集
     * @param sets 目标集
     * @return 结果集中的成员数量。
     */
    Long zinterstore(String dstkey, String... sets);

    /**
     * 功能描述: <br>
     * 求多个有序集合的交集，并把结果存在dstkey中，dstkey已存在则覆盖 score值按照params参数中设置的权重和算法计算
     * 
     * @param dstkey 结果集
     * @param params 权重和算法参数
     * @param sets 目标集
     * @return 结果集中的成员数量
     */
    Long zinterstore(String dstkey, ZParams params, String... sets);

    /**
     * 功能描述: <br>
     * 求多个有序集合的并集，并把结果存在dstkey中，dstkey已存在则覆盖 score值默认按照权重为1，累加。
     * 
     * @param dstkey 结果集
     * @param sets 目标集
     * @return 结果集中的成员数量。
     */
    Long zunionstore(String dstkey, String... sets);

    /**
     * 
     * 功能描述: <br>
     * 求多个有序集合的并集，并把结果存在dstkey中，dstkey已存在则覆盖 score值按照params参数中设置的权重和算法计算
     * 
     * @param dstkey 结果集
     * @param params 权重和算法参数
     * @param sets 目标集
     * @return 结果集中的成员数量。
     */
    Long zunionstore(String dstkey, ZParams params, String... sets);

    /**
     * 
     * 功能描述: <br>
     * 清除双写状态下节点不可用的标记
     * 
     */
    void clearDisableFlags();
}
