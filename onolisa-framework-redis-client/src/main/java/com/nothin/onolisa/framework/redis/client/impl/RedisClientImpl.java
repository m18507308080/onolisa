/*
 * Copyright (C), 2014-2014, 佛祖保佑 , 永无BUG
 * FileName: RedisClientImpl.java
 * Author:   李牧牧
 * Date:     2014年11月14日 下午8:09:08
 * Description:  
 * History: 
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间                     版本号                  描述
 * 李牧牧       2013.11.14  1.0.0
 */
package com.nothin.onolisa.framework.redis.client.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nothin.onolisa.framework.redis.client.CallBack;
import com.nothin.onolisa.framework.redis.client.IRedisClient;
import com.nothin.onolisa.framework.redis.exception.RedisClientException;
import com.nothin.onolisa.framework.redis.util.CacheUtils;

import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.ZParams;

/**
 * 
 * 〈默认的String操作接口实现〉<br> 
 * 〈功能详细描述〉
 *
 * @author 李牧牧
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class RedisClientImpl extends DefaultClientImpl implements IRedisClient {

    /**
     * 构造方法
     */
    public RedisClientImpl() {
        super();
    }

    /**
     * 构造方法
     * 
     * @param config 配置
     */
    public RedisClientImpl(String config) {
        super(config);
    }

    /**
     * 
     * @param key specified key
     * @param callBack callback
     * @return result
     */
    protected <R> R performFunction(String key, CallBack<R> callBack) {
        return sharded4Jedis.getShard(key).execute(callBack);
    }

    /**
     * @param key 主键
     * @param value 值
     * @return Bulk reply
     */
    @Override
    public String set(final String key, final String value) {
        return this.performFunction(key, new CallBack<String>() {
            // set
            public String invoke(Jedis jedis) {
                return jedis.set(key, value);
            }
        });
    }

    /**
     * Get the value of the specified key. If the key does not exist the special value 'nil' is returned. If the value
     * stored at key is not a string an error is returned because GET can only handle string values.
     * <p>
     * Time complexity: O(1)
     * 
     * @param key specified key
     * @return Bulk reply
     */
    @Override
    public String get(final String key) {
        return this.performFunction(key, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.get(key);
            }
        });
    }

    /**
     * Set the string value as value of the key. The string can't be longer than 1073741824 bytes (1 GB).
     * 
     * @param key
     * @param value
     * @param nxxx NX|XX, NX -- Only set the key if it does not already exist. XX -- Only set the key if it already
     *            exist.
     * @param expx EX|PX, expire time units: EX = seconds; PX = milliseconds
     * @param time expire time in the units of {@param #expx}
     * @return Status code reply
     */
    @Override
    public String set(final String key, final String value, final String nxxx, final String expx, final long time) {
        return this.performFunction(key, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.set(key, value, nxxx, expx, time);
            }
        });
    }

    /**
     * Test if the specified key exists. The command returns "1" if the key exists, otherwise "1" is returned. Note that
     * even keys set with an empty string as value will return "0".
     * 
     * Time complexity: O(1)
     * 
     * @param key specified key
     * @return Boolean reply, true if the key exists, otherwise false
     */
    @Override
    public Boolean exists(final String key) {
        return this.performFunction(key, new CallBack<Boolean>() {
            public Boolean invoke(Jedis jedis) {
                return jedis.exists(key);
            }
        });
    }

    /**
     * Return the type of the value stored at key in form of a string. The type can be one of "none", "string", "list",
     * "set". "none" is returned if the key does not exist.
     * 
     * Time complexity: O(1)
     * 
     * @param key specified key
     * @return Status code reply, specifically: "none" if the key does not exist "string" if the key contains a String
     *         value "list" if the key contains a List value "set" if the key contains a Set value "zset" if the key
     *         contains a Sorted Set value "hash" if the key contains a Hash value
     */
    @Override
    public String type(final String key) {
        return this.performFunction(key, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.type(key);
            }
        });
    }

    /**
     * Set a timeout on the specified key. After the timeout the key will be automatically deleted by the server. A key
     * with an associated timeout is said to be volatile in Redis terminology.
     * <p>
     * Voltile keys are stored on disk like the other keys, the timeout is persistent too like all the other aspects of
     * the dataset. Saving a dataset containing expires and stopping the server does not stop the flow of time as Redis
     * stores on disk the time when the key will no longer be available as Unix time, and not the remaining seconds.
     * <p>
     * Since Redis 2.1.3 you can update the value of the timeout of a key already having an expire set. It is also
     * possible to undo the expire at all turning the key into a normal key using the {@link #persist(String) PERSIST}
     * command.
     * <p>
     * Time complexity: O(1)
     * 
     * @see <ahref="http://code.google.com/p/redis/wiki/ExpireCommand">ExpireCommand</a>
     * 
     * @param key specified key
     * @param seconds associated timeout
     * @return Integer reply, specifically: 1: the timeout was set. 0: the timeout was not set since the key already has
     *         an associated timeout (this may happen only in Redis versions < 2.1.3, Redis >= 2.1.3 will happily update
     *         the timeout), or the key does not exist.
     */
    @Override
    public Long expire(final String key, final int seconds) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.expire(key, seconds);
            }
        });
    }

    /**
     * EXPIREAT works exctly like {@link #expire(String, int) EXPIRE} but instead to get the number of seconds
     * representing the Time To Live of the key as a second argument (that is a relative way of specifing the TTL), it
     * takes an absolute one in the form of a UNIX timestamp (Number of seconds elapsed since 1 Gen 1970).
     * <p>
     * EXPIREAT was introduced in order to implement the Append Only File persistence mode so that EXPIRE commands are
     * automatically translated into EXPIREAT commands for the append only file. Of course EXPIREAT can also used by
     * programmers that need a way to simply specify that a given key should expire at a given time in the future.
     * <p>
     * Since Redis 2.1.3 you can update the value of the timeout of a key already having an expire set. It is also
     * possible to undo the expire at all turning the key into a normal key using the {@link #persist(String) PERSIST}
     * command.
     * <p>
     * Time complexity: O(1)
     * 
     * @see <ahref="http://code.google.com/p/redis/wiki/ExpireCommand">ExpireCommand</a>
     * 
     * @param key specified key
     * @param unixTime UNIX timestamp
     * @return Integer reply, specifically: 1: the timeout was set. 0: the timeout was not set since the key already has
     *         an associated timeout (this may happen only in Redis versions < 2.1.3, Redis >= 2.1.3 will happily update
     *         the timeout), or the key does not exist.
     */
    @Override
    public Long expireAt(final String key, final long unixTime) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.expireAt(key, unixTime);
            }
        });
    }

    /**
     * The TTL command returns the remaining time to live in seconds of a key that has an {@link #expire(String, int)
     * EXPIRE} set. This introspection capability allows a Redis client to check how many seconds a given key will
     * continue to be part of the dataset.
     * 
     * @param key specified key
     * @return Integer reply, returns the remaining time to live in seconds of a key that has an EXPIRE. If the Key does
     *         not exists or does not have an associated expire, -1 is returned.
     */
    @Override
    public Long ttl(final String key) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.ttl(key);
            }
        });
    }

    /**
     * Sets or clears the bit at offset in the string value stored at key
     * 
     * @param key specified key
     * @param offset offset
     * @param value string value
     * @return result
     */
    @Override
    public Boolean setbit(final String key, final long offset, final boolean value) {
        return this.performFunction(key, new CallBack<Boolean>() {
            public Boolean invoke(Jedis jedis) {
                return jedis.setbit(key, offset, value);
            }
        });
    }

    /**
     * Sets or clears the bit at offset in the string value stored at key
     * 
     * @param key specified key
     * @param offset offset
     * @return result
     */
    @Override
    public Boolean getbit(final String key, final long offset) {
        return this.performFunction(key, new CallBack<Boolean>() {
            public Boolean invoke(Jedis jedis) {
                return jedis.getbit(key, offset);
            }
        });
    }

    /**
     * setrange
     * 
     * @param key specified key
     * @param offset offset
     * @param value string value
     * @return result
     * 
     */
    @Override
    public Long setrange(final String key, final long offset, final String value) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.setrange(key, offset, value);
            }
        });
    }

    /**
     * setrange
     * 
     * @param key specified key
     * @param startOffset start offset
     * @param endOffset end Offset
     * @return result
     * 
     */
    @Override
    public String getrange(final String key, final long startOffset, final long endOffset) {
        return this.performFunction(key, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.getrange(key, startOffset, endOffset);
            }
        });
    }

    /**
     * GETSET is an atomic set this value and return the old value command. Set key to the string value and return the
     * old value stored at key. The string can't be longer than 1073741824 bytes (1 GB).
     * <p>
     * Time complexity: O(1)
     * 
     * @param key specified key
     * @param value the old value
     * @return Bulk reply
     */
    @Override
    public String getSet(final String key, final String value) {
        return this.performFunction(key, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.getSet(key, value);
            }
        });
    }

    /**
     * SETNX works exactly like {@link #set(String, String) SET} with the only difference that if the key already exists
     * no operation is performed. SETNX actually means "SET if Not eXists".
     * <p>
     * Time complexity: O(1)
     * 
     * @param key specified key
     * @param value value
     * @return Integer reply, specifically: 1 if the key was set 0 if the key was not set
     */
    @Override
    public Long setnx(final String key, final String value) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.setnx(key, value);
            }
        });
    }

    /**
     * Get the value of the specified key. If the key does not exist the special value 'nil' is returned. If the value
     * stored at key is not a string an error is returned because GET can only handle string values.
     * <p>
     * Time complexity: O(1)
     * 
     * @param key specified key
     * @param seconds timeout seconds
     * @param value value
     * @return Bulk reply
     */
    @Override
    public String setex(final String key, final int seconds, final String value) {
        return this.performFunction(key, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.setex(key, seconds, value);
            }
        });
    }

    /**
     * IDECRBY work just like {@link #decr(String) INCR} but instead to decrement by 1 the decrement is integer.
     * <p>
     * INCR commands are limited to 64 bit signed integers.
     * <p>
     * Note: this is actually a string operation, that is, in Redis there are not "integer" types. Simply the string
     * stored at the key is parsed as a base 10 64 bit signed integer, incremented, and then converted back as a string.
     * <p>
     * Time complexity: O(1)
     * 
     * @see #incr(String)
     * @see #decr(String)
     * @see #incrBy(String, int)
     * 
     * @param key specified key
     * @param integer integer value
     * @return Integer reply, this commands will reply with the new value of key after the increment.
     */
    @Override
    public Long decrBy(final String key, final long integer) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.decrBy(key, integer);
            }
        });
    }

    /**
     * Decrement the number stored at key by one. If the key does not exist or contains a value of a wrong type, set the
     * key to the value of "0" before to perform the decrement operation.
     * <p>
     * INCR commands are limited to 64 bit signed integers.
     * <p>
     * Note: this is actually a string operation, that is, in Redis there are not "integer" types. Simply the string
     * stored at the key is parsed as a base 10 64 bit signed integer, incremented, and then converted back as a string.
     * <p>
     * Time complexity: O(1)
     * 
     * @see #incr(String)
     * @see #incrBy(String, int)
     * @see #decrBy(String, int)
     * 
     * @param key specified key
     * @return Integer reply, this commands will reply with the new value of key after the increment.
     */
    @Override
    public Long decr(final String key) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.decr(key);
            }
        });
    }

    /**
     * INCRBY work just like {@link #incr(String) INCR} but instead to increment by 1 the increment is integer.
     * <p>
     * INCR commands are limited to 64 bit signed integers.
     * <p>
     * Note: this is actually a string operation, that is, in Redis there are not "integer" types. Simply the string
     * stored at the key is parsed as a base 10 64 bit signed integer, incremented, and then converted back as a string.
     * <p>
     * Time complexity: O(1)
     * 
     * @see #incr(String)
     * @see #decr(String)
     * @see #decrBy(String, int)
     * 
     * @param key specified key
     * @param integer integer
     * @return Integer reply, this commands will reply with the new value of key after the increment.
     */
    @Override
    public Long incrBy(final String key, final long integer) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.incrBy(key, integer);
            }
        });
    }

    /**
     * Increment the number stored at key by one. If the key does not exist or contains a value of a wrong type, set the
     * key to the value of "0" before to perform the increment operation.
     * <p>
     * INCR commands are limited to 64 bit signed integers.
     * <p>
     * Note: this is actually a string operation, that is, in Redis there are not "integer" types. Simply the string
     * stored at the key is parsed as a base 10 64 bit signed integer, incremented, and then converted back as a string.
     * <p>
     * Time complexity: O(1)
     * 
     * @see #incrBy(String, int)
     * @see #decr(String)
     * @see #decrBy(String, int)
     * 
     * @param key specified key
     * @return Integer reply, this commands will reply with the new value of key after the increment.
     */
    @Override
    public Long incr(final String key) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.incr(key);
            }
        });
    }

    /**
     * If the key already exists and is a string, this command appends the provided value at the end of the string. If
     * the key does not exist it is created and set as an empty string, so APPEND will be very similar to SET in this
     * special case.
     * <p>
     * Time complexity: O(1). The amortized time complexity is O(1) assuming the appended value is small and the already
     * present value is of any size, since the dynamic string library used by Redis will double the free space available
     * on every reallocation.
     * 
     * @param key specified key
     * @param value value
     * @return Integer reply, specifically the total length of the string after the append operation.
     */
    @Override
    public Long append(final String key, final String value) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.append(key, value);
            }
        });
    }

    /**
     * Return a subset of the string from offset start to offset end (both offsets are inclusive). Negative offsets can
     * be used in order to provide an offset starting from the end of the string. So -1 means the last char, -2 the
     * penultimate and so forth.
     * <p>
     * The function handles out of range requests without raising an error, but just limiting the resulting range to the
     * actual length of the string.
     * <p>
     * Time complexity: O(start+n) (with start being the start index and n the total length of the requested range).
     * Note that the lookup part of this command is O(1) so for small strings this is actually an O(1) command.
     * 
     * @param key specified key
     * @param start start
     * @param end end
     * @return Bulk reply
     */
    @Override
    public String substr(final String key, final int start, final int end) {
        return this.performFunction(key, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.substr(key, start, end);
            }
        });
    }

    /**
     * 
     * Set the specified hash field to the specified value.
     * <p>
     * If key does not exist, a new key holding a hash is created.
     * <p>
     * <b>Time complexity:</b> O(1)
     * 
     * @param key specified key
     * @param field hash field
     * @param value specified value
     * @return If the field already exists, and the HSET just produced an update of the value, 0 is returned, otherwise
     *         if a new field is created 1 is returned.
     */
    @Override
    public Long hset(final String key, final String field, final String value) {

        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.hset(key, field, value);
            }
        });
    }

    /**
     * If key holds a hash, retrieve the value associated to the specified field.
     * <p>
     * If the field is not found or the key does not exist, a special 'nil' value is returned.
     * <p>
     * <b>Time complexity:</b> O(1)
     * 
     * @param key specified key
     * @param field hash field
     * @return Bulk reply
     */
    @Override
    public String hget(final String key, final String field) {

        return this.performFunction(key, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.hget(key, field);
            }
        });
    }

    /**
     * 
     * Set the specified hash field to the specified value if the field not exists. <b>Time complexity:</b> O(1)
     * 
     * @param key specified key
     * @param field hash field
     * @param value specified value
     * @return If the field already exists, 0 is returned, otherwise if a new field is created 1 is returned.
     */
    @Override
    public Long hsetnx(final String key, final String field, final String value) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.hsetnx(key, field, value);
            }
        });
    }

    /**
     * Set the respective fields to the respective values. HMSET replaces old values with new values.
     * <p>
     * If key does not exist, a new key holding a hash is created.
     * <p>
     * <b>Time complexity:</b> O(N) (with N being the number of fields)
     * 
     * @param key specified key
     * @param hash hash
     * @return Return OK or Exception if hash is empty
     */
    @Override
    public String hmset(final String key, final Map<String, String> hash) {
        return this.performFunction(key, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.hmset(key, hash);
            }
        });
    }

    /**
     * Retrieve the values associated to the specified fields.
     * <p>
     * If some of the specified fields do not exist, nil values are returned. Non existing keys are considered like
     * empty hashes.
     * <p>
     * <b>Time complexity:</b> O(N) (with N being the number of fields)
     * 
     * @param key specified key
     * @param fields specified fields
     * @return Multi Bulk Reply specifically a list of all the values associated with the specified fields, in the same
     *         order of the request.
     */
    @Override
    public List<String> hmget(final String key, final String... fields) {
        return this.performFunction(key, new CallBack<List<String>>() {
            public List<String> invoke(Jedis jedis) {
                return jedis.hmget(key, fields);
            }
        });
    }

    /**
     * Increment the number stored at field in the hash at key by value. If key does not exist, a new key holding a hash
     * is created. If field does not exist or holds a string, the value is set to 0 before applying the operation. Since
     * the value argument is signed you can use this command to perform both increments and decrements.
     * <p>
     * The range of values supported by HINCRBY is limited to 64 bit signed integers.
     * <p>
     * <b>Time complexity:</b> O(1)
     * 
     * @param key specified key
     * @param field specified key
     * @param value specified value
     * @return Integer reply The new value at field after the increment operation.
     */
    @Override
    public Long hincrBy(final String key, final String field, final long value) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.hincrBy(key, field, value);
            }
        });
    }

    /**
     * Test for existence of a specified field in a hash.
     * 
     * <b>Time complexity:</b> O(1)
     * 
     * @param key specified key
     * @param field specified key
     * @return Return 1 if the hash stored at key contains the specified field. Return 0 if the key is not found or the
     *         field is not present.
     */
    @Override
    public Boolean hexists(final String key, final String field) {
        return this.performFunction(key, new CallBack<Boolean>() {
            public Boolean invoke(Jedis jedis) {
                return jedis.hexists(key, field);
            }
        });
    }

    /**
     * Remove the specified field from an hash stored at key.
     * <p>
     * <b>Time complexity:</b> O(1)
     * 
     * @param key specified key
     * @param field specified field
     * @return If the field was present in the hash it is deleted and 1 is returned, otherwise 0 is returned and no
     *         operation is performed.
     */
    @Override
    public Long hdel(final String key, final String... field) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.hdel(key, field);
            }
        });
    }

    /**
     * Return the number of items in a hash.
     * <p>
     * <b>Time complexity:</b> O(1)
     * 
     * @param key specified key
     * @return The number of entries (fields) contained in the hash stored at key. If the specified key does not exist,
     *         0 is returned assuming an empty hash.
     */
    @Override
    public Long hlen(final String key) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.hlen(key);
            }
        });
    }

    /**
     * @param key specified key
     * @return result
     */
    @Override
    public Set<String> hkeys(final String key) {
        return this.performFunction(key, new CallBack<Set<String>>() {
            public Set<String> invoke(Jedis jedis) {
                return jedis.hkeys(key);
            }
        });
    }

    /**
     * @param key specified key
     * @return result
     */
    @Override
    public List<String> hvals(final String key) {
        return this.performFunction(key, new CallBack<List<String>>() {
            public List<String> invoke(Jedis jedis) {
                return jedis.hvals(key);
            }
        });
    }

    /**
     * @param key specified key
     * @return result
     */
    @Override
    public Map<String, String> hgetAll(final String key) {
        return this.performFunction(key, new CallBack<Map<String, String>>() {
            public Map<String, String> invoke(Jedis jedis) {
                return jedis.hgetAll(key);
            }
        });
    }

     /*
     * {@inheritDoc}
    
     @Override
     public Long rpush(final String key, final String value) {
     return this.performFunction(key, new CallBack<Long>() {
     public Long invoke(Jedis jedis) {
     return jedis.rpush(key, value);
     }
     });
     }

     * 存入值
     *
     * @param key 键
     * @param value 值
     * @return 存入值
     @Override
     public Long lpush(final String key, final String value) {
     return this.performFunction(key, new CallBack<Long>() {
     public Long invoke(Jedis jedis) {
     return jedis.lpush(key, value);
     }
     });
     } */

    /**
     * 获取长度
     * 
     * @param key 键
     * @return 长度
     */
    @Override
    public Long llen(final String key) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.llen(key);
            }
        });
    }

    /**
     * 根据键值，起始位置获取结果列表
     * 
     * @param key 键值
     * @param start 开始位置
     * @param end 结束为止
     * @return 结果列表
     */
    @Override
    public List<String> lrange(final String key, final long start, final long end) {
        return this.performFunction(key, new CallBack<List<String>>() {
            public List<String> invoke(Jedis jedis) {
                return jedis.lrange(key, start, end);
            }
        });
    }

    /**
     * 根据键值，起始位置trim字符串
     * 
     * @param key 键值
     * @param start 开始位置
     * @param end 结束位置
     * @return trim后的字符串
     */
    @Override
    public String ltrim(final String key, final long start, final long end) {
        return this.performFunction(key, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.ltrim(key, start, end);
            }
        });
    }

    /**
     * 获取指定索引的字符串
     * 
     * @param key 键值
     * @param index 索引
     * @return 指定索引的字符串
     */
    @Override
    public String lindex(final String key, final long index) {
        return this.performFunction(key, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.lindex(key, index);
            }
        });
    }

    /**
     * 设置指定索引值
     * 
     * @param key 键值
     * @param index 索引
     * @param value 值
     * @return result
     */
    @Override
    public String lset(final String key, final long index, final String value) {
        return this.performFunction(key, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.lset(key, index, value);
            }
        });
    }

    /**
     * Remove the first count occurrences of the value element from the list. If count is zero all the elements are
     * removed. If count is negative elements are removed from tail to head, instead to go from head to tail that is the
     * normal behaviour. So for example LREM with count -2 and hello as value to remove against the list
     * (a,b,c,hello,x,hello,hello) will lave the list (a,b,c,hello,x). The number of removed elements is returned as an
     * integer, see below for more information about the returned value. Note that non existing keys are considered like
     * empty lists by LREM, so LREM against non existing keys will always return 0.
     * <p>
     * Time complexity: O(N) (with N being the length of the list)
     * 
     * @param key specified key
     * @param count removed counts
     * @param value value
     * @return Integer Reply, specifically: The number of removed elements if the operation succeeded
     */
    @Override
    public Long lrem(final String key, final long count, final String value) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.lrem(key, count, value);
            }
        });
    }

    /**
     * Atomically return and remove the first (LPOP) or last (RPOP) element of the list. For example if the list
     * contains the elements "a","b","c" LPOP will return "a" and the list will become "b","c".
     * <p>
     * If the key does not exist or the list is already empty the special value 'nil' is returned.
     * 
     * @see #rpop(String)
     * 
     * @param key specified key
     * @return Bulk reply
     */
    @Override
    public String lpop(final String key) {
        return this.performFunction(key, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.lpop(key);
            }
        });
    }

    /**
     * Atomically return and remove the first (LPOP) or last (RPOP) element of the list. For example if the list
     * contains the elements "a","b","c" LPOP will return "a" and the list will become "b","c".
     * <p>
     * If the key does not exist or the list is already empty the special value 'nil' is returned.
     * 
     * @see #lpop(String)
     * 
     * @param key specified key
     * @return Bulk reply
     */
    @Override
    public String rpop(final String key) {
        return this.performFunction(key, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.rpop(key);
            }
        });
    }

    /**
     * Add the specified member to the set value stored at key. If member is already a member of the set no operation is
     * performed. If key does not exist a new set with the specified member as sole member is created. If the key exists
     * but does not hold a set value an error is returned.
     * <p>
     * Time complexity O(1)
     * 
     * @param key specified key
     * @param member specified member
     * @return Integer reply, specifically: 1 if the new element was added 0 if the element was already a member of the
     *         set
     */
    @Override
    public Long sadd(final String key, final String... member) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.sadd(key, member);
            }
        });
    }

    /**
     * Return all the members (elements) of the set value stored at key. This is just syntax glue for
     * {@link #sinter(String...) SINTER}.
     * <p>
     * Time complexity O(N)
     * 
     * @param key specified key
     * @return Multi bulk reply
     */
    @Override
    public Set<String> smembers(final String key) {
        return this.performFunction(key, new CallBack<Set<String>>() {
            public Set<String> invoke(Jedis jedis) {
                return jedis.smembers(key);
            }
        });
    }

    /**
     * Remove the specified member from the set value stored at key. If member was not a member of the set no operation
     * is performed. If key does not hold a set value an error is returned.
     * <p>
     * Time complexity O(1)
     * 
     * @param key specified key
     * @param member specified member
     * @return Integer reply, specifically: 1 if the new element was removed 0 if the new element was not a member of
     *         the set
     */
    @Override
    public Long srem(final String key, final String... member) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.srem(key, member);
            }
        });
    }

    /**
     * Remove a random element from a Set returning it as return value. If the Set is empty or the key does not exist, a
     * nil object is returned.
     * <p>
     * The {@link #srandmember(String)} command does a similar work but the returned element is not removed from the
     * Set.
     * <p>
     * Time complexity O(1)
     * 
     * @param key specified key
     * @return Bulk reply
     */
    @Override
    public String spop(final String key) {
        return this.performFunction(key, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.spop(key);
            }
        });
    }

    /**
     * Remove a random element from a Set returning it as return value. If the Set is empty or the key does not exist, a
     * nil object is returned.
     * <p>
     * The {@link #srandmember(String)} command does a similar work but the returned element is not removed from the
     * Set.
     * <p>
     * Time complexity O(1)
     * 
     * @param key specified key
     * @return Bulk reply
     */
    @Override
    public Long scard(final String key) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.scard(key);
            }
        });
    }

    /**
     * Return 1 if member is a member of the set stored at key, otherwise 0 is returned.
     * <p>
     * Time complexity O(1)
     * 
     * @param key specified key
     * @param member specified member
     * @return Integer reply, specifically: 1 if the element is a member of the set 0 if the element is not a member of
     *         the set OR if the key does not exist
     */
    @Override
    public Boolean sismember(final String key, final String member) {
        return this.performFunction(key, new CallBack<Boolean>() {
            public Boolean invoke(Jedis jedis) {
                return jedis.sismember(key, member);
            }
        });
    }

    /**
     * Return a random element from a Set, without removing the element. If the Set is empty or the key does not exist,
     * a nil object is returned.
     * <p>
     * The SPOP command does a similar work but the returned element is popped (removed) from the Set.
     * <p>
     * Time complexity O(1)
     * 
     * @param key specified key
     * @return Bulk reply
     */
    @Override
    public String srandmember(final String key) {
        return this.performFunction(key, new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.srandmember(key);
            }
        });
    }

    /**
     * Add the specified member having the specifeid score to the sorted set stored at key. If member is already a
     * member of the sorted set the score is updated, and the element reinserted in the right position to ensure
     * sorting. If key does not exist a new sorted set with the specified member as sole member is crated. If the key
     * exists but does not hold a sorted set value an error is returned.
     * <p>
     * The score value can be the string representation of a double precision floating point number.
     * <p>
     * Time complexity O(log(N)) with N being the number of elements in the sorted set
     * 
     * @param key specified key
     * @param score specifeid score
     * @param member specified member
     * @return Integer reply, specifically: 1 if the new element was added 0 if the element was already a member of the
     *         sorted set and the score was updated
     */
    @Override
    public Long zadd(final String key, final double score, final String member) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.zadd(key, score, member);
            }
        });
    }

    /**
     * @param key specefied key
     * @param start specified start member
     * @param end specified end member
     * @return result
     */
    @Override
    public Set<String> zrange(final String key, final long start, final long end) {
        return this.performFunction(key, new CallBack<Set<String>>() {
            public Set<String> invoke(Jedis jedis) {
                return jedis.zrange(key, start, end);
            }
        });
    }

    /**
     * Remove the specified member from the sorted set value stored at key. If member was not a member of the set no
     * operation is performed. If key does not not hold a set value an error is returned.
     * <p>
     * Time complexity O(log(N)) with N being the number of elements in the sorted set
     * 
     * 
     * 
     * @param key specified key
     * @param member specified member
     * @return Integer reply, specifically: 1 if the new element was removed 0 if the new element was not a member of
     *         the set
     */
    @Override
    public Long zrem(final String key, final String... member) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.zrem(key, member);
            }
        });
    }

    /**
     * If member already exists in the sorted set adds the increment to its score and updates the position of the
     * element in the sorted set accordingly. If member does not already exist in the sorted set it is added with
     * increment as score (that is, like if the previous score was virtually zero). If key does not exist a new sorted
     * set with the specified member as sole member is crated. If the key exists but does not hold a sorted set value an
     * error is returned.
     * <p>
     * The score value can be the string representation of a double precision floating point number. It's possible to
     * provide a negative value to perform a decrement.
     * <p>
     * For an introduction to sorted sets check the Introduction to Redis data types page.
     * <p>
     * Time complexity O(log(N)) with N being the number of elements in the sorted set
     * 
     * @param key specified key
     * @param score specified score
     * @param member specified member
     * @return The new score
     */
    @Override
    public Double zincrby(final String key, final double score, final String member) {
        return this.performFunction(key, new CallBack<Double>() {
            public Double invoke(Jedis jedis) {
                return jedis.zincrby(key, score, member);
            }
        });
    }

    /**
     * Return the rank (or index) or member in the sorted set at key, with scores being ordered from low to high.
     * <p>
     * When the given member does not exist in the sorted set, the special value 'nil' is returned. The returned rank
     * (or index) of the member is 0-based for both commands.
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))
     * 
     * @see #zrevrank(String, String)
     * 
     * @param key specified key
     * @param member specified member
     * @return Integer reply or a nil bulk reply, specifically: the rank of the element as an integer reply if the
     *         element exists. A nil bulk reply if there is no such element.
     */
    @Override
    public Long zrank(final String key, final String member) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.zrank(key, member);
            }
        });
    }

    /**
     * Return the rank (or index) or member in the sorted set at key, with scores being ordered from high to low.
     * <p>
     * When the given member does not exist in the sorted set, the special value 'nil' is returned. The returned rank
     * (or index) of the member is 0-based for both commands.
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))
     * 
     * @see #zrank(String, String)
     * 
     * @param key specified key
     * @param member specified member
     * @return Integer reply or a nil bulk reply, specifically: the rank of the element as an integer reply if the
     *         element exists. A nil bulk reply if there is no such element.
     */
    @Override
    public Long zrevrank(final String key, final String member) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.zrevrank(key, member);
            }
        });
    }

    /**
     * zrevrange
     * 
     * @param key specified key
     * @param start specified start member
     * @param end specified end member
     * @return Multi bulk reply specifically a list of elements in the specified range.
     */
    @Override
    public Set<String> zrevrange(final String key, final long start, final long end) {
        return this.performFunction(key, new CallBack<Set<String>>() {
            public Set<String> invoke(Jedis jedis) {
                return jedis.zrevrange(key, start, end);
            }
        });
    }

    /**
     * zrangeWithScores
     * 
     * @param key specified key
     * @param start specified member
     * @param end specified member
     * @return Multi bulk reply specifically a list of elements in the specified range.
     */
    @Override
    public Set<Tuple> zrangeWithScores(final String key, final long start, final long end) {
        return this.performFunction(key, new CallBack<Set<Tuple>>() {
            public Set<Tuple> invoke(Jedis jedis) {
                return jedis.zrangeWithScores(key, start, end);
            }
        });
    }

    /**
     * zrevrangeWithScores
     * 
     * @param key specified key
     * @param start specified member
     * @param end specified member
     * @return Multi bulk reply specifically a list of elements in the specified range.
     */
    @Override
    public Set<Tuple> zrevrangeWithScores(final String key, final long start, final long end) {
        return this.performFunction(key, new CallBack<Set<Tuple>>() {
            public Set<Tuple> invoke(Jedis jedis) {
                return jedis.zrevrangeWithScores(key, start, end);
            }
        });
    }

    /**
     * Return the sorted set cardinality (number of elements). If the key does not exist 0 is returned, like for empty
     * sorted sets.
     * <p>
     * Time complexity O(1)
     * 
     * @param key specified key
     * @return the cardinality (number of elements) of the set as an integer.
     */
    @Override
    public Long zcard(final String key) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.zcard(key);
            }
        });
    }

    /**
     * Return the score of the specified element of the sorted set at key. If the specified element does not exist in
     * the sorted set, or the key does not exist at all, a special 'nil' value is returned.
     * <p>
     * <b>Time complexity:</b> O(1)
     * 
     * @param key specified key
     * @param member specified member
     * @return the score
     */
    @Override
    public Double zscore(final String key, final String member) {
        return this.performFunction(key, new CallBack<Double>() {
            public Double invoke(Jedis jedis) {
                return jedis.zscore(key, member);
            }
        });
    }

    /**
     * Sort a Set or a List.
     * <p>
     * Sort the elements contained in the List, Set, or Sorted Set value at key. By default sorting is numeric with
     * elements being compared as double precision floating point numbers. This is the simplest form of SORT.
     * 
     * @see #sort(String, String)
     * @see #sort(String, SortingParams)
     * @see #sort(String, SortingParams, String)
     * 
     * 
     * @param key specified key
     * @return Assuming the Set/List at key contains a list of numbers, the return value will be the list of numbers
     *         ordered from the smallest to the biggest number.
     */
    @Override
    public List<String> sort(final String key) {
        return this.performFunction(key, new CallBack<List<String>>() {
            public List<String> invoke(Jedis jedis) {
                return jedis.sort(key);
            }
        });
    }

    /**
     * Sort a Set or a List accordingly to the specified parameters.
     * <p>
     * <b>examples:</b>
     * <p>
     * Given are the following sets and key/values:
     * 
     * <pre>
     * x = [1, 2, 3]
     * y = [a, b, c]
     * 
     * k1 = z
     * k2 = y
     * k3 = x
     * 
     * w1 = 9
     * w2 = 8
     * w3 = 7
     * </pre>
     * 
     * Sort Order:
     * 
     * <pre>
     * sort(x) or sort(x, sp.asc())
     * -> [1, 2, 3]
     * 
     * sort(x, sp.desc())
     * -> [3, 2, 1]
     * 
     * sort(y)
     * -> [c, a, b]
     * 
     * sort(y, sp.alpha())
     * -> [a, b, c]
     * 
     * sort(y, sp.alpha().desc())
     * -> [c, a, b]
     * </pre>
     * 
     * Limit (e.g. for Pagination):
     * 
     * <pre>
     * sort(x, sp.limit(0, 2))
     * -> [1, 2]
     * 
     * sort(y, sp.alpha().desc().limit(1, 2))
     * -> [b, a]
     * </pre>
     * 
     * Sorting by external keys:
     * 
     * <pre>
     * sort(x, sb.by(w*))
     * -> [3, 2, 1]
     * 
     * sort(x, sb.by(w*).desc())
     * -> [1, 2, 3]
     * </pre>
     * 
     * Getting external keys:
     * 
     * <pre>
     * sort(x, sp.by(w*).get(k*))
     * -> [x, y, z]
     * 
     * sort(x, sp.by(w*).get(#).get(k*))
     * -> [3, x, 2, y, 1, z]
     * </pre>
     * 
     * @see #sort(String)
     * @see #sort(String, SortingParams, String)
     * 
     * @param key specified key
     * @param sortingParameters sortingParameters
     * @return a list of sorted elements.
     */
    @Override
    public List<String> sort(final String key, final SortingParams sortingParameters) {
        return this.performFunction(key, new CallBack<List<String>>() {
            public List<String> invoke(Jedis jedis) {
                return jedis.sort(key, sortingParameters);
            }
        });
    }

    /**
     * zcount
     * 
     * @param key specified key
     * @param min min value
     * @param max max value
     * @return zount
     */
    @Override
    public Long zcount(final String key, final double min, final double max) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.zcount(key, min, max);
            }
        });
    }

    /**
     * Return the all the elements in the sorted set at key with a score between min and max (including elements with
     * score equal to min or max).
     * <p>
     * The elements having the same score are returned sorted lexicographically as ASCII strings (this follows from a
     * property of Redis sorted sets and does not involve further computation).
     * <p>
     * Using the optional {@link #zrangeByScore(String, double, double, int, int) LIMIT} it's possible to get only a
     * range of the matching elements in an SQL-alike way. Note that if offset is large the commands needs to traverse
     * the list for offset elements and this adds up to the O(M) figure.
     * <p>
     * The {@link #zcount(String, double, double) ZCOUNT} command is similar to
     * {@link #zrangeByScore(String, double, double) ZRANGEBYSCORE} but instead of returning the actual elements in the
     * specified interval, it just returns the number of matching elements.
     * <p>
     * <b>Exclusive intervals and infinity</b>
     * <p>
     * min and max can be -inf and +inf, so that you are not required to know what's the greatest or smallest element in
     * order to take, for instance, elements "up to a given value".
     * <p>
     * Also while the interval is for default closed (inclusive) it's possible to specify open intervals prefixing the
     * score with a "(" character, so for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (1.3 5}
     * <p>
     * Will return all the values with score > 1.3 and <= 5, while for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (5 (10}
     * <p>
     * Will return all the values with score > 5 and < 10 (5 and 10 excluded).
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))+O(M) with N being the number of elements in the sorted set and M the number of elements returned by the
     * command, so if M is constant (for instance you always ask for the first ten elements with LIMIT) you can consider
     * it O(log(N))
     * 
     * @see #zrangeByScore(String, double, double)
     * @see #zrangeByScore(String, double, double, int, int)
     * @see #zrangeByScoreWithScores(String, double, double)
     * @see #zrangeByScoreWithScores(String, String, String)
     * @see #zrangeByScoreWithScores(String, double, double, int, int)
     * @see #zcount(String, double, double)
     * 
     * @param key specified key
     * @param min min value
     * @param max max value
     * @return Multi bulk reply specifically a list of elements in the specified score range.
     */
    @Override
    public Set<String> zrangeByScore(final String key, final double min, final double max) {
        return this.performFunction(key, new CallBack<Set<String>>() {
            public Set<String> invoke(Jedis jedis) {
                return jedis.zrangeByScore(key, min, max);
            }
        });
    }

    /**
     * zrevrangeByScore
     * 
     * @param key specified key
     * @param max max value
     * @param min min value
     * @return Multi bulk reply specifically a list of elements in the specified range.
     */
    @Override
    public Set<String> zrevrangeByScore(final String key, final double max, final double min) {
        return this.performFunction(key, new CallBack<Set<String>>() {
            public Set<String> invoke(Jedis jedis) {
                return jedis.zrevrangeByScore(key, max, min);
            }
        });
    }

    /**
     * Return the all the elements in the sorted set at key with a score between min and max (including elements with
     * score equal to min or max).
     * <p>
     * The elements having the same score are returned sorted lexicographically as ASCII strings (this follows from a
     * property of Redis sorted sets and does not involve further computation).
     * <p>
     * Using the optional {@link #zrangeByScore(String, double, double, int, int) LIMIT} it's possible to get only a
     * range of the matching elements in an SQL-alike way. Note that if offset is large the commands needs to traverse
     * the list for offset elements and this adds up to the O(M) figure.
     * <p>
     * The {@link #zcount(String, double, double) ZCOUNT} command is similar to
     * {@link #zrangeByScore(String, double, double) ZRANGEBYSCORE} but instead of returning the actual elements in the
     * specified interval, it just returns the number of matching elements.
     * <p>
     * <b>Exclusive intervals and infinity</b>
     * <p>
     * min and max can be -inf and +inf, so that you are not required to know what's the greatest or smallest element in
     * order to take, for instance, elements "up to a given value".
     * <p>
     * Also while the interval is for default closed (inclusive) it's possible to specify open intervals prefixing the
     * score with a "(" character, so for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (1.3 5}
     * <p>
     * Will return all the values with score > 1.3 and <= 5, while for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (5 (10}
     * <p>
     * Will return all the values with score > 5 and < 10 (5 and 10 excluded).
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))+O(M) with N being the number of elements in the sorted set and M the number of elements returned by the
     * command, so if M is constant (for instance you always ask for the first ten elements with LIMIT) you can consider
     * it O(log(N))
     * 
     * @see #zrangeByScore(String, double, double)
     * @see #zrangeByScore(String, double, double, int, int)
     * @see #zrangeByScoreWithScores(String, double, double)
     * @see #zrangeByScoreWithScores(String, double, double, int, int)
     * @see #zcount(String, double, double)
     * 
     * @param key specified key
     * @param min min value
     * @param max max value
     * @param offset offset value
     * @param count count number
     * @return Multi bulk reply specifically a list of elements in the specified score range.
     */
    @Override
    public Set<String> zrangeByScore(final String key, final double min, final double max, final int offset,
            final int count) {
        return this.performFunction(key, new CallBack<Set<String>>() {
            public Set<String> invoke(Jedis jedis) {
                return jedis.zrangeByScore(key, max, min, offset, count);
            }
        });
    }

    /**
     * zrevrangeByScore
     * 
     * @param key specified key
     * @param max max value
     * @param min min value
     * @param offset offset value
     * @param count count value
     * @return Multi bulk reply specifically a list of elements in the specified range.
     */
    @Override
    public Set<String> zrevrangeByScore(final String key, final double max, final double min, final int offset,
            final int count) {
        return this.performFunction(key, new CallBack<Set<String>>() {
            public Set<String> invoke(Jedis jedis) {
                return jedis.zrevrangeByScore(key, max, min, offset, count);
            }
        });
    }

    /**
     * Remove all elements in the sorted set at key with rank between start and end. Start and end are 0-based with rank
     * 0 being the element with the lowest score. Both start and end can be negative numbers, where they indicate
     * offsets starting at the element with the highest rank. For example: -1 is the element with the highest score, -2
     * the element with the second highest score and so forth.
     * <p>
     * <b>Time complexity:</b> O(log(N))+O(M) with N being the number of elements in the sorted set and M the number of
     * elements removed by the operation
     * 
     * @param key specified key
     * @param start start rank
     * @param end end rank
     * @return range
     */
    @Override
    public Long zremrangeByRank(final String key, final long start, final long end) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.zremrangeByRank(key, start, end);
            }
        });
    }

    /**
     * Remove all the elements in the sorted set at key with a score between min and max (including elements with score
     * equal to min or max).
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))+O(M) with N being the number of elements in the sorted set and M the number of elements removed by the
     * operation
     * 
     * @param key specified key
     * @param start start positon
     * @param end end positon
     * @return Integer reply, specifically the number of elements removed.
     */
    @Override
    public Long zremrangeByScore(final String key, final double start, final double end) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.zremrangeByScore(key, start, end);
            }
        });
    }

    /**
     * Return the all the elements in the sorted set at key with a score between min and max (including elements with
     * score equal to min or max).
     * <p>
     * The elements having the same score are returned sorted lexicographically as ASCII strings (this follows from a
     * property of Redis sorted sets and does not involve further computation).
     * <p>
     * Using the optional {@link #zrangeByScore(String, double, double, int, int) LIMIT} it's possible to get only a
     * range of the matching elements in an SQL-alike way. Note that if offset is large the commands needs to traverse
     * the list for offset elements and this adds up to the O(M) figure.
     * <p>
     * The {@link #zcount(String, double, double) ZCOUNT} command is similar to
     * {@link #zrangeByScore(String, double, double) ZRANGEBYSCORE} but instead of returning the actual elements in the
     * specified interval, it just returns the number of matching elements.
     * <p>
     * <b>Exclusive intervals and infinity</b>
     * <p>
     * min and max can be -inf and +inf, so that you are not required to know what's the greatest or smallest element in
     * order to take, for instance, elements "up to a given value".
     * <p>
     * Also while the interval is for default closed (inclusive) it's possible to specify open intervals prefixing the
     * score with a "(" character, so for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (1.3 5}
     * <p>
     * Will return all the values with score > 1.3 and <= 5, while for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (5 (10}
     * <p>
     * Will return all the values with score > 5 and < 10 (5 and 10 excluded).
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))+O(M) with N being the number of elements in the sorted set and M the number of elements returned by the
     * command, so if M is constant (for instance you always ask for the first ten elements with LIMIT) you can consider
     * it O(log(N))
     * 
     * @see #zrangeByScore(String, double, double)
     * @see #zrangeByScore(String, double, double, int, int)
     * @see #zrangeByScoreWithScores(String, double, double)
     * @see #zrangeByScoreWithScores(String, double, double, int, int)
     * @see #zcount(String, double, double)
     * 
     * @param key specified key
     * @param min min value
     * @param max max value
     * @return Multi bulk reply specifically a list of elements in the specified score range.
     */
    @Override
    public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max) {
        return this.performFunction(key, new CallBack<Set<Tuple>>() {
            public Set<Tuple> invoke(Jedis jedis) {
                return jedis.zrangeByScoreWithScores(key, min, max);
            }
        });
    }

    /**
     * zrevrangeByScoreWithScores
     * 
     * @param key specified key
     * @param max max value
     * @param min min value
     * @return Multi bulk reply specifically a list of elements in the specified score range.
     */
    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min) {
        return this.performFunction(key, new CallBack<Set<Tuple>>() {
            public Set<Tuple> invoke(Jedis jedis) {
                return jedis.zrevrangeByScoreWithScores(key, max, min);
            }
        });
    }

    /**
     * Return the all the elements in the sorted set at key with a score between min and max (including elements with
     * score equal to min or max).
     * <p>
     * The elements having the same score are returned sorted lexicographically as ASCII strings (this follows from a
     * property of Redis sorted sets and does not involve further computation).
     * <p>
     * Using the optional {@link #zrangeByScore(String, double, double, int, int) LIMIT} it's possible to get only a
     * range of the matching elements in an SQL-alike way. Note that if offset is large the commands needs to traverse
     * the list for offset elements and this adds up to the O(M) figure.
     * <p>
     * The {@link #zcount(String, double, double) ZCOUNT} command is similar to
     * {@link #zrangeByScore(String, double, double) ZRANGEBYSCORE} but instead of returning the actual elements in the
     * specified interval, it just returns the number of matching elements.
     * <p>
     * <b>Exclusive intervals and infinity</b>
     * <p>
     * min and max can be -inf and +inf, so that you are not required to know what's the greatest or smallest element in
     * order to take, for instance, elements "up to a given value".
     * <p>
     * Also while the interval is for default closed (inclusive) it's possible to specify open intervals prefixing the
     * score with a "(" character, so for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (1.3 5}
     * <p>
     * Will return all the values with score > 1.3 and <= 5, while for instance:
     * <p>
     * {@code ZRANGEBYSCORE zset (5 (10}
     * <p>
     * Will return all the values with score > 5 and < 10 (5 and 10 excluded).
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))+O(M) with N being the number of elements in the sorted set and M the number of elements returned by the
     * command, so if M is constant (for instance you always ask for the first ten elements with LIMIT) you can consider
     * it O(log(N))
     * 
     * @see #zrangeByScore(String, double, double)
     * @see #zrangeByScore(String, double, double, int, int)
     * @see #zrangeByScoreWithScores(String, double, double)
     * @see #zrangeByScoreWithScores(String, double, double, int, int)
     * @see #zcount(String, double, double)
     * 
     * @param key specified key
     * @param min min value
     * @param max max value
     * @param offset offset
     * @param count count
     * @return Multi bulk reply specifically a list of elements in the specified score range.
     */
    @Override
    public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max, final int offset,
            final int count) {
        return this.performFunction(key, new CallBack<Set<Tuple>>() {
            public Set<Tuple> invoke(Jedis jedis) {
                return jedis.zrangeByScoreWithScores(key, max, min, offset, count);
            }
        });
    }

    /**
     * zrevrangeByScoreWithScores
     * 
     * @param key specified key
     * @param max max value
     * @param min min value
     * @param offset offset value
     * @param count count number
     * @return Multi bulk reply specifically a list of elements in the specified score range.
     */
    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min,
            final int offset, final int count) {
        return this.performFunction(key, new CallBack<Set<Tuple>>() {
            public Set<Tuple> invoke(Jedis jedis) {
                return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
            }
        });
    }

    /**
     * linsert
     * 
     * @param key specified key
     * @param where list position
     * @param pivot pivot
     * @param value value
     * @return result
     */
    @Override
    public Long linsert(final String key, final LIST_POSITION where, final String pivot, final String value) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.linsert(key, where, pivot, value);
            }
        });
    }

    /**
     * Remove the specified keys. If a given key does not exist no operation is performed for this key. The command
     * returns the number of keys removed.
     * 
     * Time complexity: O(1)
     * 
     * @param key
     * @return Integer reply, specifically: an integer greater than 0 if one or more keys were removed 0 if none of the
     *         specified key existed
     */
    @Override
    public Long del(final String key) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.del(key);
            }
        });
    }

    /**
     * Add the string value to the head (LPUSH) or tail (RPUSH) of the list stored at key. If the key does not exist an
     * empty list is created just before the append operation. If the key exists but is not a List an error is returned.
     * <p>
     * Time complexity: O(1)
     * 
     * @see Jedis#rpush(String, String)
     * 
     * @param key specified key
     * @param fields string fields
     * @return Integer reply, specifically, the number of elements inside the list after the push operation.
     */
    @Override
    public Long lpush(final String key, final String... fields) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                long r = 0;
                for (String field : fields) {
                    r = r + jedis.lpush(key, field);
                }
                return r;
            }
        });
    }

    /**
     * Add the string value to the head (LPUSH) or tail (RPUSH) of the list stored at key. If the key does not exist an
     * empty list is created just before the append operation. If the key exists but is not a List an error is returned.
     * <p>
     * Time complexity: O(1)
     * 
     * @see Jedis#lpush(String, String)
     * 
     * @param key specified key
     * @param fields string fields
     * @return Integer reply, specifically, the number of elements inside the list after the push operation.
     */
    @Override
    public Long rpush(final String key, final String... fields) {
        return this.performFunction(key, new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                long r = 0;
                for (String field : fields) {
                    r = r + jedis.rpush(key, field);
                }
                return r;
            }
        });
    }

    /**
     * Returns all the keys matching the glob-style pattern as space separated strings. For example if you have in the
     * database the keys "foo" and "foobar" the command "KEYS foo*" will return "foo foobar".
     * <p>
     * Note that while the time complexity for this operation is O(n) the constant times are pretty low. For example
     * Redis running on an entry level laptop can scan a 1 million keys database in 40 milliseconds. <b>Still it's
     * better to consider this one of the slow commands that may ruin the DB performance if not used with care.</b>
     * <p>
     * In other words this command is intended only for debugging and special operations like creating a script to
     * change the DB schema. Don't use it in your normal code. Use Redis Sets in order to group together a subset of
     * objects.
     * <p>
     * Glob style patterns examples:
     * <ul>
     * <li>h?llo will match hello hallo hhllo
     * <li>h*llo will match hllo heeeello
     * <li>h[ae]llo will match hello and hallo, but not hillo
     * </ul>
     * <p>
     * Use \ to escape special chars if you want to match them verbatim.
     * <p>
     * Time complexity: O(n) (with n being the number of keys in the DB, and assuming keys and pattern of limited
     * length)
     * 
     * @param pattern glob-style pattern
     * @return Multi bulk reply
     */
    @Override
    public Set<String> keys(final String pattern) {
        if (isSharding()) {
            throw new RedisClientException(UNSUPPORT);
        }
        return this.performFunction("", new CallBack<Set<String>>() {
            public Set<String> invoke(Jedis jedis) {
                return jedis.keys(pattern);
            }
        });
    }

    /**
     * Set the the respective keys to the respective values. MSET will replace old values with new values, while
     * {@link #msetnx(String...) MSETNX} will not perform any operation at all even if just a single key already exists.
     * <p>
     * Because of this semantic MSETNX can be used in order to set different keys representing different fields of an
     * unique logic object in a way that ensures that either all the fields or none at all are set.
     * <p>
     * Both MSET and MSETNX are atomic operations. This means that for instance if the keys A and B are modified,
     * another client talking to Redis can either see the changes to both A and B at once, or no modification at all.
     * 
     * @see #msetnx(String...)
     * 
     * @param keyValues respective keys to the respective values
     * @return Status code reply Basically +OK as MSET can't fail
     */
    @Override
    public String mset(final Map<String, String> keyValues) {
        if (isSharding()) {
            throw new RedisClientException(UNSUPPORT);
        }
        return this.performFunction("", new CallBack<String>() {
            public String invoke(Jedis jedis) {
                return jedis.mset(CacheUtils.smapToArray(keyValues));
            }
        });
    }

    /**
     * Get the values of all the specified keys. If one or more keys dont exist or is not of type String, a 'nil' value
     * is returned instead of the value of the specified key, but the operation never fails.
     * <p>
     * Time complexity: O(1) for every key
     * 
     * @param keys specified keys
     * @return Multi bulk reply
     */
    @Override
    public List<String> mget(final String... keys) {
        if (isSharding()) {
            throw new RedisClientException("The current configuration does not support this operation...");
        }
        return this.performFunction("", new CallBack<List<String>>() {
            public List<String> invoke(Jedis jedis) {
                return jedis.mget(keys);
            }
        });
    }

    /**
     * Return the members of a set resulting from the intersection of all the sets hold at the specified keys. Like in
     * {@link #lrange(String, int, int) LRANGE} the result is sent to the client as a multi-bulk reply (see the protocol
     * specification for more information). If just a single key is specified, then this command produces the same
     * result as {@link #smembers(String) SMEMBERS}. Actually SMEMBERS is just syntax sugar for SINTER.
     * <p>
     * Non existing keys are considered like empty sets, so if one of the keys is missing an empty set is returned
     * (since the intersection with an empty set always is an empty set).
     * <p>
     * Time complexity O(N*M) worst case where N is the cardinality of the smallest set and M the number of sets
     * 
     * @param keys specified keys
     * @return Multi bulk reply, specifically the list of common elements.
     */
    @Override
    public Set<String> sinter(final String... keys) {
        if (isSharding()) {
            throw new RedisClientException(UNSUPPORT);
        }
        return this.performFunction("", new CallBack<Set<String>>() {
            public Set<String> invoke(Jedis jedis) {
                return jedis.sinter(keys);
            }
        });
    }

    /**
     * This commnad works exactly like {@link #sinter(String...) SINTER} but instead of being returned the resulting set
     * is sotred as dstkey.
     * <p>
     * Time complexity O(N*M) worst case where N is the cardinality of the smallest set and M the number of sets
     * 
     * @param dstkey dstkey
     * @param keys specified keys
     * @return Status code reply
     */
    @Override
    public Long sinterstore(final String dstkey, final String... keys) {
        if (isSharding()) {
            throw new RedisClientException(UNSUPPORT);
        }
        return this.performFunction("", new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.sinterstore(dstkey, keys);
            }
        });
    }

    /**
     * Return the members of a set resulting from the union of all the sets hold at the specified keys. Like in
     * {@link #lrange(String, int, int) LRANGE} the result is sent to the client as a multi-bulk reply (see the protocol
     * specification for more information). If just a single key is specified, then this command produces the same
     * result as {@link #smembers(String) SMEMBERS}.
     * <p>
     * Non existing keys are considered like empty sets.
     * <p>
     * Time complexity O(N) where N is the total number of elements in all the provided sets
     * 
     * @param keys specified keys
     * @return Multi bulk reply, specifically the list of common elements.
     */
    @Override
    public Set<String> sunion(final String... keys) {
        if (isSharding()) {
            throw new RedisClientException(UNSUPPORT);
        }
        return this.performFunction("", new CallBack<Set<String>>() {
            public Set<String> invoke(Jedis jedis) {
                return jedis.sunion(keys);
            }
        });
    }

    /**
     * This command works exactly like {@link #sunion(String...) SUNION} but instead of being returned the resulting set
     * is stored as dstkey. Any existing value in dstkey will be over-written.
     * <p>
     * Time complexity O(N) where N is the total number of elements in all the provided sets
     * 
     * @param dstkey dstkey
     * @param keys specified keys
     * @return Status code reply
     */
    @Override
    public Long sunionstore(final String dstkey, final String... keys) {
        if (isSharding()) {
            throw new RedisClientException(UNSUPPORT);
        }
        return this.performFunction("", new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.sunionstore(dstkey, keys);
            }
        });
    }

    /**
     * Return the difference between the Set stored at key1 and all the Sets key2, ..., keyN
     * <p>
     * <b>Example:</b>
     * 
     * <pre>
     * key1 = [x, a, b, c]
     * key2 = [c]
     * key3 = [a, d]
     * SDIFF key1,key2,key3 => [x, b]
     * </pre>
     * 
     * Non existing keys are considered like empty sets.
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(N) with N being the total number of elements of all the sets
     * 
     * @param keys specified keys
     * @return Return the members of a set resulting from the difference between the first set provided and all the
     *         successive sets.
     */
    @Override
    public Set<String> sdiff(final String... keys) {
        if (isSharding()) {
            throw new RedisClientException("The current configuration does not support this operation...");
        }
        return this.performFunction("", new CallBack<Set<String>>() {
            public Set<String> invoke(Jedis jedis) {
                return jedis.sdiff(keys);
            }
        });
    }

    /**
     * This command works exactly like {@link #sdiff(String...) SDIFF} but instead of being returned the resulting set
     * is stored in dstkey.
     * 
     * @param dstkey dstkey
     * @param keys specified keys
     * @return Status code reply
     */
    @Override
    public Long sdiffstore(final String dstkey, final String... keys) {
        if (isSharding()) {
            throw new RedisClientException("The current configuration does not support this operation...");
        }
        return this.performFunction("", new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.sdiffstore(dstkey, keys);
            }
        });
    }

    /**
     * @param dstkey dstkey
     * @param sets sets
     * @return result
     */
    @Override
    public Long zinterstore(final String dstkey, final String... sets) {
        if (isSharding()) {
            throw new RedisClientException("The current configuration does not support this operation...");
        }
        return this.performFunction("", new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.zinterstore(dstkey, sets);
            }
        });
    }

    /**
     * @param dstkey dstkey
     * @param params ZParams
     * @param sets sets
     * @return result
     */
    @Override
    public Long zinterstore(final String dstkey, final ZParams params, final String... sets) {
        if (isSharding()) {
            throw new RedisClientException("The current configuration does not support this operation...");
        }
        return this.performFunction("", new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.zinterstore(dstkey, params, sets);
            }
        });
    }

    /**
     * @param dstkey dstkey
     * @param sets sets
     * @return result
     */
    @Override
    public Long zunionstore(final String dstkey, final String... sets) {
        if (isSharding()) {
            throw new RedisClientException("The current configuration does not support this operation...");
        }
        return this.performFunction("", new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.zunionstore(dstkey, sets);
            }
        });
    }

    /**
     * @param dstkey dstkey
     * @param params params
     * @param sets sets
     * @return result
     */
    @Override
    public Long zunionstore(final String dstkey, final ZParams params, final String... sets) {
        if (isSharding()) {
            throw new RedisClientException("The current configuration does not support this operation...");
        }
        return this.performFunction("", new CallBack<Long>() {
            public Long invoke(Jedis jedis) {
                return jedis.zunionstore(dstkey, params, sets);
            }
        });
    }

    /**
     * 移除给定 key 的生存时间，将这个 key 从『易失的』(带生存时间 key )转换成『持久的』(一个不带生存时间、永不过期的 key )。
     * 
     * @param key 给定 key
     * @return 结果
     */
    @Override
    public Long persist(String key) {
        return null;
    }

    /**
     * 对 key 所储存的字符串值，设置或清除指定偏移量上的位(bit)。位的设置或清除取决于 value 参数，可以是 0 也可以是 1 。当 key 不存在时，自动生成一个新的字符串值。
     * 字符串会进行伸展(grown)以确保它可以将 value 保存在指定的偏移量上。当字符串值进行伸展时，空白位置以 0 填充。 offset 参数必须大于或等于 0 ，小于 2^32 (bit 映射被限制在 512 MB
     * 之内)。
     * 
     * @param key 所储存的字符串值
     * @param offset 偏移量
     * @param value 参数
     * @return 结果
     */
    @Override
    public Boolean setbit(String key, long offset, String value) {
        return null;
    }

    /**
     * 返回 key 所储存的字符串值的长度。当 key 储存的不是字符串值时，返回一个错误。
     * 
     * @param key 所储存的字符串值
     * @return 长度
     */
    @Override
    public Long strlen(String key) {
        return null;
    }

    /**
     * 将一个或多个 member 元素及其 score 值加入到有序集 key 当中。如果某个 member 已经是有序集的成员，那么更新这个 member 的 score 值，并通过重新插入这个 member 元素，来保证该
     * member 在正确的位置上。 score 值可以是整数值或双精度浮点数。如果 key 不存在，则创建一个空的有序集并执行 ZADD 操作。当 key 存在但不是有序集类型时，返回一个错误。
     * 
     * @param key 有序集 key
     * @param scoreMembers 成员得分
     * @return 结果
     */
    @Override
    public Long zadd(String key, Map<Double, String> scoreMembers) {
        return null;
    }

    /**
     * 返回有序集 key 中， score 值在 min 和 max 之间(默认包括 score 值等于 min 或 max )的成员的数量。
     * 
     * @param key 有序集 key
     * @param min 最小得分
     * @param max 最大得分
     * @return 结果
     */
    @Override
    public Long zcount(String key, String min, String max) {
        return null;
    }

    /**
     * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。
     * 
     * @param key 有序集 key
     * @param min 最小得分
     * @param max 最大得分
     * @return 结果
     */
    @Override
    public Set<String> zrangeByScore(String key, String min, String max) {
        return null;
    }

    /**
     * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score 值递减(从大到小)的次序排列。 具有相同 score
     * 值的成员按字典序的逆序(reverse lexicographical order )排列。 除了成员按 score 值递减的次序排列这一点外， ZREVRANGEBYSCORE 命令的其他方面和 ZRANGEBYSCORE
     * 命令一样。
     * 
     * @param key 有序集 key
     * @param min 最小得分
     * @param max 最大得分
     * @return 结果
     */
    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min) {
        return null;
    }

    /**
     * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。
     * 
     * @param key 有序集 key
     * @param min 最小得分
     * @param max 最大得分
     * @param offset 偏移量
     * @param count 个数
     * @return 结果
     */
    @Override
    public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
        return null;
    }

    /**
     * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score 值递减(从大到小)的次序排列。 具有相同 score
     * 值的成员按字典序的逆序(reverse lexicographical order )排列。 除了成员按 score 值递减的次序排列这一点外， ZREVRANGEBYSCORE 命令的其他方面和 ZRANGEBYSCORE
     * 命令一样。
     * 
     * @param key 有序集 key
     * @param min 最小得分
     * @param max 最大得分
     * @param offset 偏移量
     * @param count 个数
     * @return 结果
     */
    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
        return null;
    }

    /**
     * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。
     * 
     * @param key 有序集 key
     * @param min 最小得分
     * @param max 最大得分
     * @return 结果
     */
    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
        return null;
    }

    /**
     * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score 值递减(从大到小)的次序排列。 具有相同 score
     * 值的成员按字典序的逆序(reverse lexicographical order )排列。 除了成员按 score 值递减的次序排列这一点外， ZREVRANGEBYSCORE 命令的其他方面和 ZRANGEBYSCORE
     * 命令一样。
     * 
     * @param key 有序集 key
     * @param max 最大得分
     * @param min 最小得分
     * @return 结果
     */
    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {
        return null;
    }

    /**
     * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。
     * 
     * @param key 有序集 key
     * @param min 最小得分
     * @param max 最大得分
     * @param offset 偏移量
     * @param count 个数
     * @return 所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员
     */
    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
        return null;
    }

    /**
     * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score 值递减(从大到小)的次序排列。 具有相同 score
     * 值的成员按字典序的逆序(reverse lexicographical order )排列。 除了成员按 score 值递减的次序排列这一点外， ZREVRANGEBYSCORE 命令的其他方面和 ZRANGEBYSCORE
     * 命令一样。
     * 
     * @param key 有序集 key
     * @param min 最小得分
     * @param max 最大得分
     * @param offset 偏移量
     * @param count 个数
     * @return score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员
     */
    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count) {
        return null;
    }

    /**
     * 移除有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。
     * 
     * @param key 有序集 key
     * @param start 开始位置
     * @param end 结束位置
     * @return 移出结果
     * @return 结果
     */
    @Override
    public Long zremrangeByScore(String key, String start, String end) {
        return null;
    }

    /**
     * 将值 value 插入到列表 key 的表头，当且仅当 key 存在并且是一个列表。
     * 
     * @param key 列表 key
     * @param string value
     * @return 插入结果
     */
    @Override
    public Long lpushx(String key, String... string) {
        return null;
    }

    /**
     * 将值 value 插入到列表 key 的表尾，当且仅当 key 存在并且是一个列表。
     * 
     * @param key 列表 key
     * @param string value
     * @return 插入结果
     */
    @Override
    public Long rpushx(String key, String... string) {
        return null;
    }

    /**
     * BLPOP 是列表的阻塞式(blocking)弹出原语。 它是 LPOP 命令的阻塞版本，当给定列表内没有任何元素可供弹出的时候，连接将被 BLPOP 命令阻塞，直到等待超时或发现可弹出元素为止。 当给定多个 key
     * 参数时，按参数 key 的先后顺序依次检查各个列表，弹出第一个非空列表的头元素。
     * 
     * @param arg 参数
     * @return 弹出第一个非空列表的头元素
     */
    @Override
    public List<String> blpop(String arg) {
        return null;
    }

    /**
     * BRPOP 是列表的阻塞式(blocking)弹出原语。 它是 RPOP 命令的阻塞版本，当给定列表内没有任何元素可供弹出的时候，连接将被 BRPOP 命令阻塞，直到等待超时或发现可弹出元素为止。 当给定多个 key
     * 参数时，按参数 key 的先后顺序依次检查各个列表，弹出第一个非空列表的尾部元素。
     * 
     * @param arg 参数
     * @return 弹出第一个非空列表的尾部元素
     */
    @Override
    public List<String> brpop(String arg) {
        return null;
    }

    /**
     * 打印一个特定的信息 message ，测试时使用。
     * 
     * @param string 特定的信息
     * @return message
     */
    @Override
    public String echo(String string) {
        return null;
    }

    /**
     * 将当前数据库的 key 移动到给定的数据库 db 当中。 如果当前数据库(源数据库)和给定数据库(目标数据库)有相同名字的给定 key ，或者 key 不存在于当前数据库，那么 MOVE 没有任何效果。
     * 因此，也可以利用这一特性，将 MOVE 当作锁(locking)原语(primitive)。
     * 
     * @param key 给定的字符串
     * @param dbIndex 给定的数据库
     * @return 移动结果
     */
    @Override
    public Long move(String key, int dbIndex) {
        return null;
    }

    /**
     * 计算给定字符串中，被设置为 1 的比特位的数量。一般情况下，给定的整个字符串都会被进行计数，
     * 
     * @param key 给定的字符串
     * @return 比特位的数量
     */
    @Override
    public Long bitcount(String key) {
        return null;
    }

    /**
     * 计算给定字符串中，被设置为 1 的比特位的数量。一般情况下，给定的整个字符串都会被进行计数，通过指定额外的 start 或 end 参数，可以让计数只在特定的位上进行。 start 和 end 参数的设置和 GETRANGE
     * 命令类似，都可以使用负数值：比如 -1 表示最后一个位，而 -2 表示倒数第二个位，以此类推。 不存在的 key 被当成是空字符串来处理，因此对一个不存在的 key 进行 BITCOUNT 操作，结果为 0 。
     * 
     * @param key 给定的字符串
     * @param start 开始位置
     * @param end 结束位置
     * @return 比特位的数量
     */
    @Override
    public Long bitcount(String key, long start, long end) {
        return null;
    }
}
