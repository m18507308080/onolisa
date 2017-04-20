/*
 * Copyright (C), 2014-2014, 佛祖保佑 , 永无BUG
 * FileName: ReadMethodTags.java
 * Author:   李牧牧
 * Date:     2014年11月14日 下午8:09:08
 * Description:  
 * History: 
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间                     版本号                  描述
 * 李牧牧       2013.11.14  1.0.0
 */
package com.nothin.onolisa.framework.redis.shard;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * 〈根据方法名判断是否是读操作〉<br> 
 * 〈功能详细描述〉
 *
 * @author 李牧牧
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class ReadMethodTags {
    /**
     * setReadTags
     */
    private static Set<String> setReadTags = new HashSet<String>();

    static {
        setReadTags.add("sort");
        setReadTags.add("get");
        setReadTags.add("exists");
        setReadTags.add("type");
        setReadTags.add("ttl");
        setReadTags.add("mget");
        setReadTags.add("keys");
        setReadTags.add("getbit");
        setReadTags.add("getrange");
        setReadTags.add("substr");
        setReadTags.add("hget");
        setReadTags.add("hmget");
        setReadTags.add("hexists");
        setReadTags.add("hlen");
        setReadTags.add("hkeys");
        setReadTags.add("hvals");
        setReadTags.add("hgetAll");
        setReadTags.add("llen");
        setReadTags.add("lrange");
        setReadTags.add("lindex");
        setReadTags.add("smembers");
        setReadTags.add("sismember");
        setReadTags.add("scard");
        setReadTags.add("srandmember");
        setReadTags.add("zrank");
        setReadTags.add("zrevrank");
        setReadTags.add("zrange");
        setReadTags.add("zrevrange");
        setReadTags.add("zcard");
        setReadTags.add("zscore");
        setReadTags.add("zcount");
        setReadTags.add("zrangeByScore");
        setReadTags.add("zrevrangeByScore");
        setReadTags.add("zrangeWithScores");
        setReadTags.add("zrevrangeWithScores");
        setReadTags.add("zrangeByScoreWithScores");
        setReadTags.add("zrevrangeByScoreWithScores");
    }

    /**
     * 
     * 功能描述：判断是否是读方法
     * 
     * @param methodName 参数说明 返回值: 类型 <说明>
     * @return boolean 返回值
     */
    public static boolean isReadMethod(String methodName) {
        return setReadTags.contains(methodName);
    }
}
