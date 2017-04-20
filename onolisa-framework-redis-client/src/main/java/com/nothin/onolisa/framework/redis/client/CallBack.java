/*
 * Copyright (C), 2014-2014, 佛祖保佑 , 永无BUG
 * FileName: CallBack.java
 * Author:   李牧牧
 * Date:     2014年11月14日 下午8:09:08
 * Description: 接口回调类   
 * History: 
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间                     版本号                  描述
 * 李牧牧       2013.11.14  1.0.0
 */
package com.nothin.onolisa.framework.redis.client;

import redis.clients.jedis.Jedis;

/**
 * 
 * 〈泛型对象〉<br> 
 * 〈功能详细描述〉
 *
 * @author 李牧牧
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface CallBack<R> {

    /**
     * 
     * 功能描述: <br>
     * 〈执行回调方法，调用jedis的实例，返回相应操作的结果。 R为返回值，可能为String, Object等〉
     *
     * @param jedis
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    R invoke(Jedis jedis);
}
