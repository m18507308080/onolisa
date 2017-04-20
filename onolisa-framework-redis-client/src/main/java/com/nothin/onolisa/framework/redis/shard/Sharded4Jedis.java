/*
 * Copyright (C), 2014-2014, 佛祖保佑 , 永无BUG
 * FileName: Sharded4Jedis.java
 * Author:   李牧牧
 * Date:     2014年11月14日 下午8:09:08
 * Description:  
 * History: 
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间                     版本号                  描述
 * 李牧牧       2013.11.14  1.0.0
 */
package com.nothin.onolisa.framework.redis.shard;

import java.util.List;

/**
 * 
 * 〈包括所有分片的连接池〉<br> 
 * 〈功能详细描述〉
 *
 * @author 李牧牧
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class Sharded4Jedis extends DefaultSharded<ShardHandler, ShardInfo4Jedis> {

    /**
     * 构造方法
     * 
     * @param shards 分片列表
     */
    public Sharded4Jedis(List<ShardInfo4Jedis> shards) {
        super(shards);
    }

}
