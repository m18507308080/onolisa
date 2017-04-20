/*
 * Copyright (C), 2014-2014, 佛祖保佑 , 永无BUG
 * FileName: DefaultSharded.java
 * Author:   李牧牧
 * Date:     2014年11月14日 下午8:09:08
 * Description:  
 * History: 
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间                     版本号                  描述
 * 李牧牧       2013.11.14  1.0.0
 */
package com.nothin.onolisa.framework.redis.shard;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import redis.clients.util.Hashing;
import redis.clients.util.SafeEncoder;

/**
 * 
 * 〈分片工具类, 来自Jedis，为便于区分，重新取名〉<br> 
 * 〈功能详细描述〉
 *
 * @author 李牧牧
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class DefaultSharded<R, S extends DefaultShardInfo<R>> {
    /**
     * 默认权重
     */
    public static final int DEFAULT_WEIGHT = 1;

    /**
     * the tag is anything between {}
     */
    public static final Pattern DEFAULT_KEY_TAG_PATTERN = Pattern.compile("\\{(.+?)\\}");

    /**
     * 节点
     */
    private TreeMap<Long, S> nodes;

    /**
     * hash算法逻辑
     */
    private final Hashing algo;

    /**
     * 分片信息
     */
    private final Map<DefaultShardInfo<R>, R> resources = new LinkedHashMap<DefaultShardInfo<R>, R>();

    /**
     * The default pattern used for extracting a key tag. The pattern must have a group (between parenthesis), which
     * delimits the tag to be hashed. A null pattern avoids applying the regular expression for each lookup, improving
     * performance a little bit is key tags aren't being used.
     */
    private Pattern tagPattern = null;

    /**
     * 构造方法
     * @param shards 分片列表
     */
    public DefaultSharded(List<S> shards) {
        /** MD5 is really not good as we works */
        this(shards, Hashing.MURMUR_HASH);
    }

    /**
     * 构造方法
     * 
     * @param shards 分片
     * @param algo hash算法
     */
    public DefaultSharded(List<S> shards, Hashing algo) {
        this.algo = algo;
        initialize(shards);
    }

    /**
     * 构造方法
     * 
     * @param shards 分片
     * @param tagPattern 标签格式
     */
    public DefaultSharded(List<S> shards, Pattern tagPattern) {
        /** MD5 is really not good */
        this(shards, Hashing.MURMUR_HASH, tagPattern);
    }

    /**
     * 构造方法
     * 
     * @param shards 分片
     * @param algo hash算法
     * @param tagPattern 标签格式
     */
    public DefaultSharded(List<S> shards, Hashing algo, Pattern tagPattern) {
        this.algo = algo;
        this.tagPattern = tagPattern;
        initialize(shards);
    }

    /**
     * 初始化
     * @param shards 分片列表
     */
    private void initialize(List<S> shards) {
        final int factor = 160;
        nodes = new TreeMap<Long, S>();
        int shardsSize = shards.size();
        for (int i = 0; i != shardsSize; ++i) {
            final S shardInfo = shards.get(i);
            final int weight = shardInfo.getWeight();
            final int fweight = factor * weight;
            if (shardInfo.getName() == null) {
                for (int n = 0; n < fweight; n++) {
                    nodes.put(this.algo.hash("SHARD-" + i + "-NODE-" + n), shardInfo);
                }
            } else {
                for (int n = 0; n < fweight; n++) {
                    nodes.put(this.algo.hash(shardInfo.getName() + "*" + shardInfo.getWeight() + n), shardInfo);
                }
            }
            resources.put(shardInfo, shardInfo.createResource());
        }
    }

    /**
     * 功能描述： getShard
     * @param key  键
     * @return 分片
     */
    public R getShard(byte[] key) {
        return resources.get(getShardInfo(key));
    }

    /**
     * 功能描述： 获取分片
     * @param key 键
     * @return 分片
     */
    public R getShard(String key) {
        return resources.get(getShardInfo(key));
    }

    /**
     * 功能描述： 获取分片信息
     * @param key 键
     * @return 分片信息
     */
    public S getShardInfo(byte[] key) {
        SortedMap<Long, S> tail = nodes.tailMap(algo.hash(key));
        if (tail.size() == 0) {
            return nodes.get(nodes.firstKey());
        }
        return tail.get(tail.firstKey());
    }

    /**
     * 
     * @param key 键
     * @return 分片信息
     */
    public S getShardInfo(String key) {
        return getShardInfo(SafeEncoder.encode(getKeyTag(key)));
    }

    /**
     * A key tag is a special pattern inside a key that, if preset, is the only part of the key hashed in order to
     * select the server for this key.
     * 
     * @see http://code.google.com/p/redis/wiki/FAQ#I 'm_using_some_form_of_key_hashing_for_partitioning,_but_wh
     * @param key special key
     * @return The tag if it exists, or the original key
     */
    public String getKeyTag(String key) {
        if (tagPattern != null) {
            Matcher m = tagPattern.matcher(key);
            if (m.find()) {
                return m.group(1);
            }
        }
        return key;
    }

    /**
     * 返回所有分片信息
     * 功能描述： getAllShardInfo
     * @return 所有分片信息
     */
    public Collection<S> getAllShardInfo() {
        return Collections.unmodifiableCollection(nodes.values());
    }

    /**
     * 返回所有分片
     * 功能描述： getAllShards
     * @return 所有分片
     */
    public Collection<R> getAllShards() {
        return Collections.unmodifiableCollection(resources.values());
    }
}
