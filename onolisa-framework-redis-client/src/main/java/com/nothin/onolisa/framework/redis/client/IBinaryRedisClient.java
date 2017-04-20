/*
 * Copyright (C), 2014-2014, 佛祖保佑 , 永无BUG
 * FileName: IBinaryRedisClient.java
 * Author:   李牧牧
 * Date:     2014年11月14日 下午8:09:08
 * Description:  
 * History: 
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间                     版本号                  描述
 * 李牧牧       2013.11.14  1.0.0
 */
package com.nothin.onolisa.framework.redis.client;

import java.util.Map;

import com.alibaba.fastjson.TypeReference;

/**
 * 
 * 〈Binary类型缓存操作接口定义〉<br> 
 * 〈功能详细描述〉
 *
 * @author 李牧牧
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface IBinaryRedisClient {

    /*
     * 功能描述: <br> 刷新配置，重建连接池
     * @param config void refresh(String config);
     */

    /*
     * 功能描述: <br> 将字符串值 value 关联到 key 。 如果 key 已经持有其他值， SET 就覆写旧值，无视类型。 对于某个原本带有生存时间的键来说， 当 SET 命令成功在这个键上执行时， 这个键原有的 TTL
     * 将被清除。
     * @param key
     * @param value
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) String set(final Serializable key, final Serializable value);
     */

    
    
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
    String set(final String key, final String value, final String nxxx, final String expx, final long time);
    
    
    
    /**
     * 
     * 功能描述: 此API用于存取集合类型的数据，比如：Map<String,Object>, List<Object> 客户端的使用方式（示例）为：redisClient.get("user",new
     * TypeReference<Map<String, long[]>>(){}); 对应的set方式为：public <T> void set(final String key, final T value)
     * 
     * @param key 键值
     * @param type 集合类型
     * @param <T> 泛型对象
     * @return 集合类型
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    <T> T get(String key, TypeReference<T> type);

    /**
     * 
     * 功能描述: 用于存放JAVA简单的类对象的数据，如:put(userId,user) 〈功能详细描述〉
     * 
     * @param key 键值
     * @param value 简单的类对象
     * @param <T> 泛型对象
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    <T> void set(String key, T value);
    
    String setObject(final String key, final Object value);

    /**
     * 设置集合对象
     * 
     * @param key 键值
     * @param value 集合对象
     * @param time 过期时间
     * @param <T> 泛型对象
     */
    <T> void setex(String key, T value, int time);
    
    String setexObject(final String key, final Object value, final int time);

    /**
     * 
     * 功能描述: 用于访问JAVA简单的类对象的数据，如:get(userId,User.class) 〈功能详细描述〉
     * 
     * @param key 键值
     * @param value 简单的类对象
     * @param <T> 泛型对象
     * @return JAVA简单的类对象
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    <T> T get(String key, Class<T> value);
    
    Object getObject(final String key);

    /**
     * 移除对象
     * 
     * @param key 移出指定的对象
     */
    void remove(String key);
    
    void removeObject(final String key);
    
    Long hsetObject(final String key, final String field, final Object value);
    
    Object hgetObject(final String key, final String field);
    
    void hdelObject(final String key, final String field);
    
    Map<String, Object> hgetAllObjects(final String key);
    
    Long expire(final String id, final int seconds);
    /*
     * 功能描述: <br> 将值 value 关联到 key ，并将 key 的生存时间设为 seconds (以秒为单位)。 如果 key 已经存在，覆写旧值。
     * @param key
     * @param time
     * @param value
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) String setex(final Serializable key, final int time, final Serializable value); 功能描述: <br>
     * 返回 key 所关联的字符串值。 如果 key 不存在那么返回特殊值 nil
     * @param key
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) <T extends Serializable> T get(final Serializable key); 功能描述: <br> 将 key 中储存的数字值减一。 如果 key
     * 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 decr操作
     * @param key
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Long decr(final Serializable key); 功能描述: <br> 将 key 所储存的值减去减量 integer。 如果 key 不存在，那么 key
     * 的值会先被初始化为 0 ，然后再执行 decrBy操作。
     * @param key
     * @param integer
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Long decrBy(final Serializable key, final long integer); 功能描述: <br> 将 key 中储存的数字值加一。 如果 key
     * 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 incr操作
     * @param key
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Long incr(final Serializable key); 功能描述: <br> 将 key 所储存的值加上增量 integer。 如果 key 不存在，那么 key
     * 的值会先被初始化为 0 ，然后再执行 incrBy操作。
     * @param key
     * @param integer
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Long incrBy(final Serializable key, final long integer); 功能描述: <br> 检测key所对应的值是否存在
     * @param key
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) boolean exists(final Serializable key); 功能描述: <br> 将key 中的域 field 的值设为 value 。 如果 key
     * 不存在，一个新的哈希表被创建 如果域 field 已经存在中，覆盖旧值。
     * @param key
     * @param field
     * @param value
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Long hset(final Serializable key, final Serializable field, final Serializable value); 功能描述:
     * <br> field 不存在时，将哈希表 key 中的域 field 的值设置为 value
     * @param key
     * @param field
     * @param value
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Long hsetnx(final Serializable key, final Serializable field, final Serializable value);
     * 功能描述: <br> 获取key对应的Field/Value对
     * @param key
     * @param field
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) <T extends Serializable> T hget(final Serializable key, final Serializable field); 功能描述:
     * <br> 同时将多个 field-value (域-值)对设置到哈希表 key 中
     * @param key
     * @param hash
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) String hmset(final Serializable key, Map<Serializable, Serializable> hash); 功能描述: <br> 返回哈希表
     * key 中，一个或多个给定域的值。 如果给定的域不存在于哈希表，那么返回一个 nil 值
     * @param key
     * @param fields
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) <T extends Serializable> List<T> hmget(final Serializable key, Serializable... fields);
     * 功能描述: <br> 为哈希表 key 中的域 field 的值加上增量 increment 。 增量为负数，相当于对进行减法操作
     * @param key
     * @param field
     * @param value
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Long hincrBy(final Serializable key, final Serializable field, final long value); 功能描述: <br>
     * 查看哈希表 key 中，给定域 field 是否存在。
     * @param key
     * @param field
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Boolean hexists(final Serializable key, final Serializable field); 功能描述: <br> 返回哈希表 key
     * 中域的数量。
     * @param key
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Long hlen(final Serializable key); 功能描述: <br> 返回哈希表 key 中所有域的值。
     * @param key
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) <T extends Serializable> List<T> hvals(final Serializable key); 功能描述: <br> 返回哈希表 key 中的所有域。
     * @param key
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) <T extends Serializable> Set<T> hkeys(final Serializable key); 功能描述: <br> 返回哈希表 key
     * 中，所有的域和值。
     * @param key
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) <T extends Serializable> Map<T, T> hgetAll(final Serializable key); 功能描述: <br> 〈功能详细描述〉
     * @param key
     * @param field
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Long hdel(final Serializable key, final Serializable field); 功能描述: <br> 〈功能详细描述〉
     * @param key
     * @param seconds
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Long expire(final Serializable key, final int seconds); 功能描述: <br> 〈功能详细描述〉
     * @param key
     * @param unixTime
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Long expireAt(final Serializable key, final long unixTime); 功能描述: <br> 返回列表 key
     * 中指定区间内的元素，区间以偏移量 start 和 stop 指定
     * @param key
     * @param startIndex
     * @param endIndex
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) <T extends Serializable> List<T> lrange(final Serializable key, final int startIndex, final
     * int endIndex); 功能描述: <br> 将一个或多个值 value 插入到列表 key 的表头 如果 key 不存在，一个空列表会被创建并执行 lpush操作。
     * @param key
     * @param fields
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Long lpush(final Serializable key, final Serializable... fields); 功能描述: <br> 将一个或多个值 value
     * 插入到列表 key 的表尾 如果 key 不存在，一个空列表会被创建并执行 rpush操作。
     * @param key
     * @param fields
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Long rpush(final Serializable key, final Serializable... fields); 功能描述: <br> 返回列表 key 的长度。
     * 如果 key 不存在，则 key 被解释为一个空列表，返回 0
     * @param key
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Long llen(final Serializable key); 功能描述: <br> 截取列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除
     * @param key
     * @param count
     * @param value
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Long lrem(final Serializable key, final int count, final Serializable value); 功能描述: <br>
     * 〈功能详细描述〉
     * @param key
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Long ttl(final Serializable key); 功能描述: <br> 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member
     * 元素将被忽略。 假如 key 不存在，则创建一个只包含 member 元素作成员的集合。
     * @param key
     * @param member
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Long sadd(final Serializable key, final Serializable member); 功能描述: <br> 移除集合 key 中的一个或多个
     * member 元素，不存在的 member 元素会被忽略。
     * @param key
     * @param member
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Long srem(final Serializable key, final Serializable member); 功能描述: <br> 返回集合 key 中的所有成员。
     * @param key
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) <T extends Serializable> Set<T> smembers(final Serializable key); 功能描述: <br> 〈功能详细描述〉
     * @param keys
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) <T extends Serializable> Set<T> sinter(Serializable... keys); 功能描述: <br> sinterstore
     * @param dstkey
     * @param keys
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Long sinterstore(Serializable dstkey, Serializable... keys); 功能描述: <br> sunion
     * @param keys
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) <T extends Serializable> Set<T> sunion(Serializable... keys); 功能描述: <br> sunionstore
     * @param dstkey
     * @param keys
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Long sunionstore(Serializable dstkey, Serializable... keys); 功能描述: <br> sdiff
     * @param keys
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) <T extends Serializable> Set<T> sdiff(Serializable... keys); 功能描述: <br> sdiffstore
     * @param dstkey
     * @param keys
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Long sdiffstore(Serializable dstkey, Serializable... keys); 功能描述: <br> 将一个或多个 member 元素及其
     * score 值加入到有序集 key 当中。 如果某个 member 已经是有序集的成员，那么更新这个 member 的 score 值，并通过重新插入这个 member 元素，来保证该 member 在正确的位置上。 如果
     * key 不存在，则创建一个空的有序集并执行 ZADD 操作。
     * @param key
     * @param score
     * @param member
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Long zadd(final Serializable key, final double score, final Serializable member); 功能描述: <br>
     * 返回有序集 key 中，指定区间内的成员。 其中zrange成员的位置按 score 值递增来排序 zrevrange成员的位置按 score 值递减来排序
     * @param key
     * @param start
     * @param end
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) <T extends Serializable> Set<T> zrange(final Serializable key, final int start, final int
     * end); 功能描述: <br> 移除有序集 key 中的成员
     * @param key
     * @param member
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Long zrem(final Serializable key, final Serializable member); 功能描述: <br> 返回有序集 key 的基数。
     * @param key
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Long zcard(final Serializable key); 功能描述: <br> 求多个有序集合的交集，并把结果存在dstkey中，dstkey已存在则覆盖
     * score值默认按照权重为1，累加。
     * @param sets
     * @return 结果集中的成员数量。 Long zinterstore(Serializable dstkey, Serializable... sets); 功能描述: <br>
     * 求多个有序集合的交集，并把结果存在dstkey中，dstkey已存在则覆盖 score值按照params参数中设置的权重和算法计算
     * @param sets
     * @return 结果集中的成员数量。 Long zinterstore(Serializable dstkey, ZParams params, Serializable... sets); 功能描述: <br>
     * 求多个有序集合的并集，并把结果存在dstkey中，dstkey已存在则覆盖 score值默认按照权重为1，累加。
     * @param sets
     * @return 结果集中的成员数量。 Long zunionstore(Serializable dstkey, Serializable... sets); 功能描述: <br>
     * 求多个有序集合的并集，并把结果存在dstkey中，dstkey已存在则覆盖 score值按照params参数中设置的权重和算法计算
     * @param sets
     * @return 结果集中的成员数量。 Long zunionstore(Serializable dstkey, ZParams params, Serializable... sets); 功能描述： zscore
     * @param 参数说明 返回值: 类型 <说明>
     * @return 返回值
     * @throw 异常描述
     * @see 需要参见的其它内容 Double zscore(Serializable key, Serializable member); 功能描述: <br> 删除这个key
     * @param key
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) Long del(final Serializable key); 功能描述: <br> 批量获取keys对应的values
     * @param keys
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) <T extends Serializable> List<T> mget(final Serializable... keys); 功能描述: <br> 批量设置key/value对
     * @param keyValues
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) String mset(Map<Serializable, Serializable> keyValues); 功能描述: <br> 返回pattern匹配的keys
     * @param pattern
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) <T extends Serializable> Set<T> keys(final Serializable pattern); 功能描述: <br> Delete all the
     * keys of the currently selected DB. This command never fails
     * @return Status code reply
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选) String flushDB(); 功能描述: <br> 清除双写状态下节点不可用的标记 void clearDisableFlags();
     */
}
