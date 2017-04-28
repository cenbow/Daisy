package com.yourong.common.cache;

import java.io.Serializable;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.yourong.common.util.PropertiesUtil;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.ibatis.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class MybatisRedisCache implements Cache {
    /**
     * redis数据库index
     */
    public static final int INDEX_REDIS_DB = PropertiesUtil.getMybatisCacheDB();
    /**
     * 日志对象
     */
    protected Logger log = LoggerFactory.getLogger(MybatisRedisCache.class);

    /**
     * The ReadWriteLock.
     */
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private String id;

    public MybatisRedisCache(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("必须传入ID");
        }
        log.debug("MybatisRedisCache:id=" + id);
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public int getSize() {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        int result = 0;
        boolean borrowOrOprSuccess = true;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            jedis.select(MybatisRedisCache.INDEX_REDIS_DB);
            result = Integer.valueOf(jedis.dbSize().toString());
        } catch (JedisConnectionException e) {
            log.error("mybatisRedis获取redis,conneciton异常",e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
        return result;

    }

    @Override
    public void putObject(Object key, Object value) {
//		 
//		if (log.isInfoEnabled())
//			log.info("put to redis sql :" + key.toString());
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean borrowOrOprSuccess = true;
        Serializable serializeble = null;
        if (value instanceof java.io.Serializable) {
            serializeble = (Serializable) value;
        } else {
            log.error("对象未序列化");
            return;
        }
        try {
            jedis = RedisManager.getJedis();
            jedis.select(MybatisRedisCache.INDEX_REDIS_DB);
            jedisPool = RedisManager.getJedisPool();
            jedis.set(SerializationUtils.serialize(key.hashCode()), SerializationUtils.serialize(serializeble));
        } catch (JedisConnectionException e) {
            log.error("putObject", e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }

    }

    @Override
    public Object getObject(Object key) {
//		if (log.isInfoEnabled())
//			log.info("getObject :" + key.toString());
        Jedis jedis = null;
        JedisPool jedisPool = null;
        Object value = null;
        boolean borrowOrOprSuccess = true;
        try {
            jedis = RedisManager.getJedis();
            jedis.select(MybatisRedisCache.INDEX_REDIS_DB);
            jedisPool = RedisManager.getJedisPool();
            byte[] bs = jedis.get(SerializationUtils.serialize(key.hashCode()));
            if (bs == null) {
                return value;
            }
            value = SerializationUtils.deserialize(bs);
        } catch (JedisConnectionException e) {
            log.error("getObject", e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
//		if (log.isDebugEnabled())
//			log.debug("getObject:" + key.hashCode() + "=" + value);
        return value;
    }

    @Override
    public Object removeObject(Object key) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        Object value = null;
        boolean borrowOrOprSuccess = true;
        try {
            jedis = RedisManager.getJedis();
            jedis.select(MybatisRedisCache.INDEX_REDIS_DB);
            jedisPool = RedisManager.getJedisPool();
            value = jedis.expire(SerializationUtils.serialize(key.hashCode()), 0);
        } catch (JedisConnectionException e) {
            log.error("removeObject", e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
//		if (log.isDebugEnabled())
//			log.debug("removeObject:" + key.hashCode() + "=" + value);
        return value;
    }

    @Override
    public void clear() {
        log.info("MybatisRedisCache 清除缓存");
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean borrowOrOprSuccess = true;
        try {
            jedis = RedisManager.getJedis();
            jedis.select(MybatisRedisCache.INDEX_REDIS_DB);
            jedisPool = RedisManager.getJedisPool();
            jedis.flushDB();
        } catch (JedisConnectionException e) {
            log.error("clear", e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
    }
    @Override
    public ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }

}
