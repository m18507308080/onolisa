/*
 * Copyright (C), 2014-2014, 佛祖保佑 , 永无BUG
 * FileName: DefaultShardInfo.java
 * Author:   李牧牧
 * Date:     2014年11月14日 下午8:09:08
 * Description:  
 * History: 
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间                     版本号                  描述
 * 李牧牧       2013.11.14  1.0.0
 */
package com.nothin.onolisa.framework.redis.shard;

/**
 * 
 * 〈分片工具类, 来自Jedis，为便于区分，重新取名〉<br> 
 * 〈功能详细描述〉
 *
 * @author 李牧牧
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public abstract class DefaultShardInfo<T> {
    /**
     * 该shard的权重
     */
    private int weight = 1;

    /**
     * 构造方法
     */
    public DefaultShardInfo() {
    }

    /**
     * 构造方法
     * 
     * @param weight 参数说明
     */
    public DefaultShardInfo(int weight) {
        this.weight = weight;
    }

    /**
     * 
     * 功能描述： 获取权重
     * 
     * @return int 返回值
     */
    public int getWeight() {
        return this.weight;
    }

    /**
     * 
     * 功能描述: 创建Resource<br>
     * 初始化一个Resource
     * 
     * @return T 返回值
     */
    protected abstract T createResource();

    /**
     * 
     * 功能描述：获取分片名字
     * 
     * @return String 返回值
     */
    public abstract String getName();
}
