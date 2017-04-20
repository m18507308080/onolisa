/*
 * Copyright (C), 2014-2014, 佛祖保佑 , 永无BUG
 * FileName: RedisClientException.java
 * Author:   李牧牧
 * Date:     2014年11月14日 下午8:09:08
 * Description:  
 * History: 
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间                     版本号                  描述
 * 李牧牧       2013.11.14  1.0.0
 */
package com.nothin.onolisa.framework.redis.exception;

/**
 * 
 * 〈Redis客户端异常〉<br> 
 * 〈功能详细描述〉
 *
 * @author 李牧牧
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class RedisClientException extends RuntimeException {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 7460934076911268418L;

    /**
     * 构造方法
     * 
     * @param msg 异常信息
     */
    public RedisClientException(String msg) {
        super(msg);
    }

    /**
     * 构造方法
     * 
     * @param exception 异常原因
     */
    public RedisClientException(Throwable exception) {
        super(exception);
    }

    /**
     * 构造方法
     * 
     * @param mag 异常信息
     * @param exception 异常原因
     */
    public RedisClientException(String mag, Exception exception) {
        super(mag, exception);
    }
}
