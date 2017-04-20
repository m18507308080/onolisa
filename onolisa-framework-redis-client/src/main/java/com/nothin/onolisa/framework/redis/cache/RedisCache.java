/*
 * Copyright (C), 2014-2014, 佛祖保佑 , 永无BUG
 * FileName: RedisCache.java
 * Author:   李牧牧
 * Date:     2014年11月14日 下午8:09:08
 * Description: Redis缓存
 * History: 
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间                     版本号                  描述
 * 李牧牧       2013.11.14  1.0.0
 */
package com.nothin.onolisa.framework.redis.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.TypeReference;
import com.nothin.onolisa.framework.redis.client.IRedisClient;

/**
 * Redis缓存<br>
 * 
 * @author 李牧牧
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 * 
 *        缓存策略：<br>
 * 
 *        <p>
 *        读取缓存：<br>
 *        如果缓存中Key不存在，进行设置缓存操作。由于只有一个线程能成功设置Lock
 *        Key以执行数据加载及设置缓存操作，其他线程将以睡眠100ms并重试的方式等待，直到 缓存数据被设置或Lock Key被删除或失效。<br>
 *        一般缓存读取：如果缓存中的Key已存在，直接返回缓存中的数据。<br>
 *        依赖缓存读取：如果读取缓存以设置另一个缓存（通过RedisCacheExecutionContext实现），
 *        需要检查该Key是否在DirtyKeySet中存在，若存在（缓存脏）， 需要进行设置缓存操作。<br>
 *        每次对缓存的读取，将Key和读取数据的Callback记录在History的HashMap中.
 *        <p>
 *        缓存失效：将指定失效的Key增加到Redis的DirtyKeySet中.后台线程扫描DirtyKeySet和Histrory的HashMap
 *        ，进行设置缓存操作。
 *        <p>
 *        设置缓存：<br>
 *        生成一个Lock Key（在原来Key的基础上加上“_@@LOCK”后缀，该Lock
 *        Key被设置失效时间（3s)以防止程序意外终止时该Lock Key永远存在）增加到Redis中，
 *        在并发情况下，只有一个线程能成功设置Lock Key以执行数据加载及设置缓存操作。<br>
 *        设置缓存操作结束后该Lock Key将被删除；<br>
 *        如果设置缓存失败（加载数据时发生Exception）并且 缓存中该Key已存在，要将该Key删除，已防止读取错误数据。
 * 
 */
public class RedisCache {

	/**
	 * 
	 * 〈记录缓存读取的历史操作，存储在HashMap中〉<br>
	 * 〈功能详细描述〉
	 *
	 * @author 李牧牧
	 * @see [相关类/方法]（可选）
	 * @since [产品/模块版本] （可选）
	 */
	private class History {
		private Map<String, Runnable> map = new ConcurrentHashMap<String, Runnable>();

		public void record(String key, Runnable setCacheCallback) {
			map.put(key, setCacheCallback);
		}

		public Map<String, Runnable> clear() {
			if (map.isEmpty()) {
				return map;
			}

			Map<String, Runnable> result = map;
			map = new ConcurrentHashMap<String, Runnable>();
			return result;
		}
	}

	/**
	 * 
	 * 〈后台缓存更新〉<br>
	 * 〈功能详细描述〉
	 *
	 * @author 李牧牧
	 * @see [相关类/方法]（可选）
	 * @since [产品/模块版本] （可选）
	 */
	private class Updater {

		Map<String, Runnable> callbacks = new HashMap<String, Runnable>();
		Map<String, Date> timestamps = new HashMap<String, Date>();
		Date lastExpirationCheck = new Date();
		boolean flagStop;
		boolean isRunning = false;
		Object runninglock = new Object();
		Semaphore sleepLock = new Semaphore(1);
		int countUpdated, countPersisted;

		private void doWork() {
			while (true) {
				try {
					updateCache(getUpdaterBatchLimit());

					if (countUpdated == 0) {
						removeExpiredKeys();
						sleep();
					}

					if (flagStop) {
						return;
					}

					log.info(String
							.format("Redis cache update completed successfully, %s key(s) updated, %s key(s) persisted.",
									countUpdated, countPersisted));
				} catch (Throwable ex) {
					log.error("Redis Cache Updater error:", ex);
				}
			}
		}

		private void removeExpiredKeys() {
			Date now = new Date();
			long checkInterval = 3600 * 1000; // 1 hour
			if (now.getTime() - lastExpirationCheck.getTime() < checkInterval) {
				return;
			}

			int count = 0;
			for (Entry<String, Date> entry : timestamps.entrySet()) {
				Date timestamp = entry.getValue();
				if (now.getTime() - timestamp.getTime() > KEY_EXPIRE_SECOND * 1000) {
					String key = entry.getKey();
					timestamps.remove(key);
					callbacks.remove(key);
					count++;
				}
			}
			this.lastExpirationCheck = new Date();
			log.info(String
					.format("removeExpiredKeys completed successfully. %s key(s) removed.",
							count));
		}

		private void sleep() {
			try {
				sleepLock.tryAcquire(1, getUpdaterIdleTime(),
						TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// do nothing
			}
		}

		private void initialize() {
			flagStop = false;
			try {
				sleepLock.acquire();
			} catch (InterruptedException e) {
				// do nothing
			}
		}

		public void start() {
			assert (!isRunning);

			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					synchronized (runninglock) {
						initialize();
						isRunning = true;
						doWork();
						isRunning = false;
						runninglock.notify();
					}
				}
			});
			t.start();
		}

		public void Stop() {
			flagStop = true;
			sleepLock.release();
			synchronized (runninglock) {
				while (isRunning) {
					try {
						runninglock.wait();
					} catch (InterruptedException e) {
						// do nothing
					}
				}
			}
		}

		private void updateCache(int limit) {
			countUpdated = 0;
			countPersisted = 0;
			Set<String> dirtyKeys = processHistory();
			processDirtyKeys(dirtyKeys, limit);
		}

		/**
		 * 优先处理已读取的Key. 若Key是脏的，进行设置缓存操作（两天后失效）;否则将该Key设置成永不失效（persist）。<br>
		 * 注：Persist操作可以优化成在读取缓存时进行，需要RedisClient支持Lua以减少Redis服务器访问.
		 * 
		 * @return 还未处理的脏数据Key集合
		 */
		private Set<String> processHistory() {
			Map<String, Runnable> history = RedisCache.this.history.clear();
			Set<String> dirtyKeys = redisClient.smembers(getDirtySetKey());
			Date now = new Date();

			for (Entry<String, Runnable> entry : history.entrySet()) {
				String key = entry.getKey();
				Runnable callback = entry.getValue();
				callbacks.put(key, callback);
				timestamps.put(key, now);

				if (dirtyKeys.contains(key)) {
					setCache(key, callback);
					dirtyKeys.remove(key);
				} else {
					redisClient.persist(key);
					countPersisted++;
				}
			}
			return dirtyKeys;
		}

		private void setCache(final String key, final Runnable callback) {
			if (!lock(key)) {
				return;
			}
			RedisCacheExecutionContext.execute(new RedisCacheExecutionAction() {
				@Override
				public void invoke(boolean forceCleanData) {
					try {
						callback.run();
					} catch (Exception ex) {
						log.error(String.format(
								"Error updating redis cache, key=%s", key), ex);
					}
				}
			});
			countUpdated++;
		}

		private void processDirtyKeys(Set<String> dirtyKeys, int limit) {
			for (String key : dirtyKeys) {
				if (countUpdated >= limit) {
					return;
				}

				Runnable callback = callbacks.get(key);
				if (callback == null) {
					continue;
				}

				setCache(key, callback);
			}
		}
	}

	private static Logger log = LoggerFactory.getLogger(RedisCache.class);

	private static final String LOCK_KEY_SUFFIX = "_@@LOCK";
	private static final Long RETRY_MILLS = 100L;
	private static final int LOCK_TIMEOUT_SECOND = 3;
	private static final int MAX_RETRY_COUNT = 30;

	/*
	 * key的失效时间，如果在这段时间内该key没有访问，该key将被删除
	 */
	private static final int KEY_EXPIRE_SECOND = 172800; // 2 days

	private History history = new History();
	private Updater updater = new Updater();

	/**
	 * spring注入的redisClient
	 */
	private IRedisClient redisClient;

	public IRedisClient getRedisClient() {
		return redisClient;
	}

	public void setRedisClient(IRedisClient redisClient) {
		this.redisClient = redisClient;
	}

	/**
	 * 脏数据集合的Key
	 */
	private String dirtySetKey;

	/**
	 * 后台缓存更新每次更新的Key的数目
	 */
	private int updaterBatchLimit = 100;

	/**
	 * 后台缓存更新空闲时间（ms）
	 */
	private int updaterIdleTime = 5000;

	/**
	 * 获取后台缓存更新每次更新的Key的数目.缺省为100
	 * 
	 * @return 后台缓存更新每次更新的Key的数目
	 */
	public int getUpdaterBatchLimit() {
		return updaterBatchLimit;
	}

	/**
	 * 设置后台缓存更新每次更新的Key的数目.缺省为100
	 */
	public void setUpdaterBatchLimit(int updaterBatchLimit) {
		this.updaterBatchLimit = updaterBatchLimit;
	}

	/**
	 * 获取后台缓存更新空闲时间（ms）。缺省为5000（5秒）
	 * 
	 * @return 获取后台缓存更新空闲时间（ms）。缺省为5000（5秒）
	 */
	public int getUpdaterIdleTime() {
		return updaterIdleTime;
	}

	/**
	 * 设置后台缓存更新空闲时间（ms）。缺省为5000（5秒）
	 * 
	 * @param updaterIdleTime
	 *            后台缓存更新空闲时间（ms）
	 */
	public void setUpdaterIdleTime(int updaterIdleTime) {
		this.updaterIdleTime = updaterIdleTime;
	}

	/**
	 * 获取脏数据集合的Key
	 * 
	 * @return 获取脏数据集合的Key
	 */
	public String getDirtySetKey() {
		return dirtySetKey;
	}

	/**
	 * 设置脏数据集合的Key
	 * 
	 * @param value
	 */
	public void setDirtySetKey(String value) {
		this.dirtySetKey = value;
	}

	/**
	 * 从缓存中读取数据
	 * 
	 * @param type
	 *            数据类型，用于泛型的反序列化
	 * @param key
	 *            缓存键的名称
	 * @param queryData
	 *            数据查询回调，用于缓存不存在或失效时
	 * @return 查询到的数据结果
	 */
	public <T> T getData(final TypeReference<T> type, final String key,
			final QueryDataCallback<T> queryData) {
		return RedisCacheExecutionContext
				.execute(new RedisCacheExecutionFunc<T>() {
					@Override
					public T invoke(boolean forceCleanData) {
						return getData(type, key, queryData, forceCleanData);
					}
				});
	}

	/**
	 * 从缓存中读取数据
	 * 
	 * @param type
	 *            数据类型，用于泛型的反序列化
	 * @param key
	 *            缓存键的名称
	 * @param queryData
	 *            数据查询回调，用于缓存不存在或失效时
	 * @param forceCleanData
	 *            是否必须读取更新后的数据，依赖的数据集应该用true参数来调用被依赖的数据集，以保证数据加载的正确性
	 * @return 查询到的数据结果
	 */
	private <T> T getData(TypeReference<T> type, final String key,
			final QueryDataCallback<T> queryData, boolean forceCleanData) {
		String value = readCache(key, forceCleanData);

		T data = null;
		if (value == null) {
			data = setCache(key, queryData);
		} else {
			try {
				data = JSON.parseObject(value, type);
			} catch (JSONException e) {
				redisClient.del(key);
				throw e;
			}
		}

		history.record(key, new Runnable() {
			@Override
			public void run() {
				setCache(key, queryData);
			}
		});
		return data;
	}

	private String getLockKey(String key) {
		return key + LOCK_KEY_SUFFIX;
	}

	private Boolean lock(String key) {
		String lockKey = getLockKey(key);
		Long success = redisClient.setnx(lockKey, LOCK_KEY_SUFFIX);

		if (success > 0) {
			redisClient.expire(lockKey, LOCK_TIMEOUT_SECOND);
			return true;
		}
		return false;
	}

	private boolean isDirty(String key) {
		return redisClient.sismember(getDirtySetKey(), key);
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 读取Redis缓存
	 *
	 * @param key
	 *            缓存键
	 * @param forceCleanData
	 *            是否必须读取更新后的数据
	 * @return 缓存字符串值，如果为NULL表示缓存不存在指定的键值
	 */
	private String readCache(String key, final boolean forceCleanData) {
		String value;
		int retryCount = 0;

		while (true) {
			value = redisClient.get(key);

			if (value != null) {
				if (!forceCleanData) {
					return value; // Cache hit. Shortest path for most cache
									// read
				}

				if (!isDirty(key)) {
					return value; // Inner cache (Cache A contains Cache B) hit.
				}
			}

			if (lock(key)) {
				// 成功锁定
				return null;
			}

			sleep();
			retryCount++;

			// 如果重试RETRYTIMES次仍然是正在设置则返回null 重新查询并设置
			if (retryCount >= MAX_RETRY_COUNT) {
				log.warn(String
						.format("key '%s' max retry count reached.", key));
				return null;
			}
		}
	}

	private void sleep() {
		sleep(RETRY_MILLS);
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// do nothing
		}
	}

	private <T> T setCache(String key, QueryDataCallback<T> queryData) {
		T data;
		try {
			data = queryData.invoke();
			redisClient.set(key, JSON.toJSONString(data));
			// 设置key的失效时间，如果在这段时间内该key没有访问，该key将被删除
			redisClient.expire(key, KEY_EXPIRE_SECOND);
			redisClient.srem(getDirtySetKey(), key);
		} catch (Exception ex) {
			redisClient.del(key); // if there is any exception, make sure the
									// key is deleted
			throw new RuntimeException(ex);
		} finally {
			redisClient.del(getLockKey(key)); // the lock key should be deleted
												// any way
		}
		return data;
	}

	/**
	 * 失效缓存数据
	 * 
	 * @param keyPattern
	 *            Glob style pattern
	 *            <p>
	 *            Glob style patterns examples:
	 *            <ul>
	 *            <li>h?llo will match hello hallo hhllo
	 *            <li>h*llo will match hllo heeeello
	 *            <li>h[ae]llo will match hello and hallo, but not hillo
	 *            </ul>
	 *            <p>
	 *            Use \ to escape special chars if you want to match them
	 *            verbatim.
	 *            <p>
	 */
	public void invalidate(String keyPattern) {
		Set<String> keySet = redisClient.keys(keyPattern);

		List<String> keys = new ArrayList<String>();
		for (String key : keySet) {
			if (!isLock(key)) {
				keys.add(key);
			}
		}

		if (!keys.isEmpty()) {
			// dirty keys should be added to redis set as a transaction
			// to avoid wrong data loading when dependent key has not been added
			// yet
			redisClient.sadd(getDirtySetKey(),
					keys.toArray(new String[keys.size()]));
		}
	}

	private static boolean isLock(String key) {
		return key.endsWith(LOCK_KEY_SUFFIX);
	}

	@PostConstruct
	public void Initialize() {
		if (org.apache.commons.lang.StringUtils.isEmpty(getDirtySetKey())) {
			throw new RuntimeException(
					"The DirtySetKey property is null or empty.");
		}

		updater.start();
		log.info("RedisCache updater started.");
	}

	@PreDestroy
	public void Destroy() {
		updater.Stop();
		log.info("RedisCache updater stopped.");
	}
}
