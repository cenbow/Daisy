package com.yourong.common.cache;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;
import rop.thirdparty.com.google.common.collect.Lists;

import com.yourong.common.util.PropertiesUtil;

public final class RedisManager {
    private static Logger logger = LoggerFactory.getLogger(RedisManager.class);

    private RedisManager() {

    }

    static class CachePool {
        JedisPool pool;
        private static final CachePool cachePool = new CachePool();

        public static CachePool getInstance() {
            return cachePool;
        }

        private CachePool() {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxIdle(Integer.parseInt(PropertiesUtil.getProperties("redis.maxIdle")));
            config.setMaxWaitMillis(Integer.parseInt(PropertiesUtil.getProperties("redis.maxWait")));
            
            pool = new JedisPool(
                    config,
                    PropertiesUtil.getProperties("redis.host"),
                    Integer.parseInt(PropertiesUtil.getProperties("redis.port")),
                    Integer.parseInt(PropertiesUtil
                            .getProperties("redis.maxWait")), PropertiesUtil
                    .getProperties("redis.pass"));
//        	 JedisPoolConfig config = new JedisPoolConfig();
//             config.setMaxIdle(100);
//             config.setMaxWaitMillis(300);
//             
//             pool = new JedisPool(
//                     config,
//                     "192.168.0.199",
//                     6379,
//                     10000, "xrw0197");
        }

        public Jedis getJedis() {
            Jedis jedis = null;
          
            try {
                jedis = pool.getResource();
            }catch ( Exception e){
                logger.error("redis连接异常",e);
            }
            return jedis;
        }

        public JedisPool getJedisPool() {
            return this.pool;
        }

    }

    public static Jedis getJedis() {
        return CachePool.getInstance().getJedis();
    }

    public static JedisPool getJedisPool() {
        return CachePool.getInstance().getJedisPool();
    }

    public static void putObject(Object key, Object value) {
        if (logger.isDebugEnabled())
            logger.debug("putObject:" + key.hashCode() + "=" + value);
        if (logger.isInfoEnabled())
            logger.info("put to redis sql :" + key.toString());
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean borrowOrOprSuccess = true;
        Serializable serializeble = null;
        if (value instanceof java.io.Serializable) {
            serializeble = (Serializable) value;
        } else {
            logger.error("对象未序列化");
            return;
        }
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            jedis.select(PropertiesUtil.getObjectCacheDBIndex());
            jedis.set(SerializationUtils.serialize(key.hashCode()), SerializationUtils.serialize(serializeble));
        } catch (JedisConnectionException e) {
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }

    }


    public static Object getObject(Object key) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        Object value = null;
        boolean borrowOrOprSuccess = true;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            jedis.select(PropertiesUtil.getObjectCacheDBIndex());
            value = SerializationUtils.deserialize(jedis.get(SerializationUtils
                    .serialize(key.hashCode())));
        } catch (JedisConnectionException e) {
        	logger.error("redis,获取 key={}的数据" , key, e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
        if (logger.isDebugEnabled())
            logger.debug("getObject:" + key.hashCode() + "=" + value);
        return value;
    }

    public static Object removeObject(Object key) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        Object value = null;
        boolean borrowOrOprSuccess = true;
        try {
            jedis = RedisManager.getJedis();
            jedis.select(PropertiesUtil.getObjectCacheDBIndex());
            jedisPool = RedisManager.getJedisPool();
            value = jedis.expire(SerializationUtils.serialize(key.hashCode()), 0);
        } catch (JedisConnectionException e) {
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
        if (logger.isDebugEnabled())
            logger.debug("getObject:" + key.hashCode() + "=" + value);
        return value;
    }

    /**
     * 判断键是否存在
     *
     * @param key
     * @return
     */
    public static boolean isExitByObjectKey(Object key) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean value = false;
        boolean borrowOrOprSuccess = true;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            jedis.select(PropertiesUtil.getObjectCacheDBIndex());
            value = jedis.exists(SerializationUtils.serialize(key.hashCode()));
        } catch (JedisConnectionException e) {
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
        if (logger.isDebugEnabled())
            logger.debug("getObject:" + key.hashCode() + "=" + value);
        return value;
    }

    /**
     * 判断键是否存在
     *
     * @param key
     * @return
     */
    public static boolean isExit(String key) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean value = false;
        boolean borrowOrOprSuccess = true;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            value = jedis.exists(key);
        } catch (JedisConnectionException e) {
            logger.error("查询redis,失败key=" + key, e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
        return value;
    }

    public static void putString(String key, String value) {
        Jedis jedis = null;
        JedisPool jedisPool = null;

        boolean borrowOrOprSuccess = true;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            jedis.set(key, value);
        } catch (JedisConnectionException e) {
            logger.error("写入redis,失败key=" + key + " value=" + value, e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }

    }

    public static void removeString(String key) {
        Jedis jedis = null;
        JedisPool jedisPool = null;

        boolean borrowOrOprSuccess = true;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            jedis.del(key);
        } catch (JedisConnectionException e) {
            logger.error("redis,删除 key=" + key, e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }

    }

    public static void hdel(String key, String... fields) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean borrowOrOprSuccess = true;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            jedis.hdel(key, fields);
        } catch (JedisConnectionException e) {
            logger.error("redis,删除 key=" + key, e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }

    }

    public static String getValueByString(String key) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        String value = "";
        boolean borrowOrOprSuccess = true;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            value = jedis.get(key);
        } catch (JedisConnectionException e) {
            logger.error("获取redis缓存,失败key=" + key, e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
        return value;
    }

    /**
     * 设置 key 的有效时间，
     *
     * @param key
     * @param seconds 秒单位
     * @return
     */
    public static Long expire(String key, int seconds) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean borrowOrOprSuccess = true;
        Long result = 0L;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            result = jedis.expire(key, seconds);
        } catch (JedisConnectionException e) {
            logger.error("hset redis,失败key=" + key, e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
        return result;

    }

    public static Long hincrBy(String key, String field, Long xvalue) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean borrowOrOprSuccess = true;
        Long hincrBy = 0L;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            hincrBy = jedis.hincrBy(key, field, xvalue);          
        } catch (JedisConnectionException e) {
            logger.error("hset redis,失败key=" + key, e);
            borrowOrOprSuccess = false;       
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
        return hincrBy;
    }

    /**
     * 将 key 中储存的数字值增一
     * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCR 操作。
     * @param key
     * @return
     */
    public static Long incr(String key) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean borrowOrOprSuccess = true;
        Long hincrBy = 0L;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            hincrBy = jedis.incr(key);
        } catch (JedisConnectionException e) {
            logger.error("incr 失败key=" + key, e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
        return hincrBy;
    }
    
    /**
     * 将 key 中储存的数字值增加指定额
     * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY 操作。
     * @param key
     * @return
     */
    public static Long incrby(String key, Long xvalue) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean borrowOrOprSuccess = true;
        Long incrBy = 0L;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            incrBy = jedis.incrBy(key, xvalue);
        } catch (JedisConnectionException e) {
            logger.error("incr 失败key=" + key, e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
        return incrBy;
    }
    
    /**
     * 将 key 中储存的数字值减一
     * @param key
     * @return
     */
    public static Long decr(String key) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean borrowOrOprSuccess = true;
        Long decrBy = 0L;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            decrBy = jedis.decr(key);
        } catch (JedisConnectionException e) {
            logger.error("decr 失败key=" + key, e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
        return decrBy;
    }

    /**
     * 用于减小存储在由指定的值的key的数量。如果该键不存在时，它被设置为0在执行操作之前。
     * 如果键包含了错误类型的值或包含不能被表示为整数，字符串，则返回错误。
     * @param key
     * @return
     */
    public static Long decrBy(String key, Long decrNum) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean borrowOrOprSuccess = true;
        Long hincrBy = 0L;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            hincrBy = jedis.decrBy(key, decrNum);
        } catch (JedisConnectionException e) {
            logger.error("decrBy 失败key={} decrNum={}", key, decrNum, e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
        return hincrBy;
    }
    
    public static boolean hset(String key, String field, String xvalue) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean borrowOrOprSuccess = true;
        boolean result = false;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            jedis.hset(key, field, xvalue);
            result = true;
        } catch (JedisConnectionException e) {
            logger.error("hset redis,失败key=" + key, e);
            borrowOrOprSuccess = false;
            result = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
        return result;

    }

    public static String hget(String key, String field) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean borrowOrOprSuccess = true;
        String value = null;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            value = jedis.hget(key, field);           
        } catch (JedisConnectionException e) {
            logger.error("hget redis,失败key=" + key, e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
        return value;
    }

    public static Map  hgetAll(String key) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean borrowOrOprSuccess = true;
        Map<String, String> value = null;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            value = jedis.hgetAll(key);
        } catch (JedisConnectionException e) {
            logger.error("hgetAll redis,失败key=" + key, e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
        return value;
    }

    /**
     * 添加值到集合
     *
     * @param key
     * @param score
     * @param member
     */
    public static void zset(String key, Double score, String member) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean borrowOrOprSuccess = true;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            Long zadd = jedis.zadd(key, score, member);
        } catch (JedisConnectionException e) {
            logger.error("zset redis,失败key=" + key, e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
    }

    /**
     * 获取在集合排名
     *
     * @param key
     * @param member
     * @return
     */
    public static long zrevrank(String key, String member) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean borrowOrOprSuccess = true;
        long result = 0L;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            result = jedis.zrevrank(key, member) + 1;
        } catch (JedisConnectionException e) {
            logger.error("zrevrank redis,失败key=" + key, e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
        return result;
    }

    /**
     * 删除集合里的 key 的值
     *
     * @param key
     * @param member
     * @return
     */
    public static long zrem(String key, String member) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean borrowOrOprSuccess = true;
        long result = 0L;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            result = jedis.zrem(key, member);
        } catch (JedisConnectionException e) {
            logger.error("zrevrank redis,失败key=" + key, e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
        return result;
    }
    
    //TODO 队列
    
    /**
     * 
     *
     * @param key
     * @param member
     * @return
     */
    public static long rpush(String key,  String... strings) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean borrowOrOprSuccess = true;
        long result = 0L;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            result = jedis.rpush(key, strings);
        } catch (JedisConnectionException e) {
            logger.error("rpush redis,失败key=" + key, e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
        return result;
    }
    
    /**
     * 
     *
     * @param key
     * @param member
     * @return
     */
    public static String rpop(String key) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean borrowOrOprSuccess = true;
        String  result = null;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            result = jedis.rpop(key);
        } catch (JedisConnectionException e) {
            logger.error("zrevrank redis,失败key=" + key, e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
        return result;
    }
    
    
    public static boolean set(String key, String xvalue) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean borrowOrOprSuccess = true;
        boolean result = false;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            jedis.set(key, xvalue);
            result = true;
        } catch (JedisConnectionException e) {
            logger.error("set redis,失败key=" + key, e);
            borrowOrOprSuccess = false;
            result = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
        return result;

    }
    
	public static long setnx(String key, String xvalue) {
		Jedis jedis = null;
		JedisPool jedisPool = null;
		boolean borrowOrOprSuccess = true;
		long result = 0l;
		try {
			jedis = RedisManager.getJedis();
			jedisPool = RedisManager.getJedisPool();
			result = jedis.setnx(key, xvalue);
		} catch (JedisConnectionException e) {
			logger.error("set redis,失败key=" + key, e);
			borrowOrOprSuccess = false;
			if (jedis != null)
				jedisPool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				jedisPool.returnResource(jedis);
		}
		return result;
	}

    public static String get(String key) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean borrowOrOprSuccess = true;
        String value = null;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            value = jedis.get(key);           
        } catch (JedisConnectionException e) {
            logger.error("get redis,失败key=" + key, e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
        return value;
    }

    /**
     * 清空redis 里值
     * @param index
     */
    public static void flushDB(int index) {
        logger.info(" 清除redis缓存: db:"+index);
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean borrowOrOprSuccess = true;
        try {
            jedis = RedisManager.getJedis();
            jedis.select(index);
            jedisPool = RedisManager.getJedisPool();
            jedis.flushDB();
        } catch (JedisConnectionException e) {
            logger.error(String.format(" 清除redis异常: db:%d", index), e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
    }
    
    /**
     * 设置 key 的有效时间，
     *
     * @param key
     * @param seconds 秒单位
     * @return
     */
    public static Long expireObject(Object key, int seconds) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean borrowOrOprSuccess = true;
        Long result = 0L;
        try {
            jedis = RedisManager.getJedis();
            jedis.select(PropertiesUtil.getObjectCacheDBIndex());
            jedisPool = RedisManager.getJedisPool();
            result = jedis.expire(SerializationUtils.serialize(key.hashCode()), seconds);
        } catch (JedisConnectionException e) {
            logger.error("hset redis,失败key=" + key, e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
        return result;

    }



    public static boolean zadd(int index,String key, Long score,  String member) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean borrowOrOprSuccess = true;
        boolean result = false;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            jedis.select(index);
            jedis.zadd(key,score,member);
            result = true;
        } catch (JedisConnectionException e) {
            logger.error("zdd redis,失败key=" + key, e);
            borrowOrOprSuccess = false;
            result = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
        return result;
    }

    public static Set<String>  zrange(int index,String key, long start, final long end) {
        Jedis jedis = null;
        JedisPool jedisPool = null;
        boolean borrowOrOprSuccess = true;
        Set<String> set = null;
        try {
            jedis = RedisManager.getJedis();
            jedisPool = RedisManager.getJedisPool();
            jedis.select(index);
            set  = jedis.zrange(key, start, end);
        } catch (JedisConnectionException e) {
            logger.error("zdd redis,失败key=" + key, e);
            borrowOrOprSuccess = false;
            if (jedis != null)
                jedisPool.returnBrokenResource(jedis);
        } finally {
            if (borrowOrOprSuccess)
                jedisPool.returnResource(jedis);
        }
        return set;
    }

	public static List<String> lrangeAll(String key) {
		Jedis jedis = null;
		JedisPool jedisPool = null;
		List<String> value = Lists.newArrayList();
		boolean borrowOrOprSuccess = true;
		try {
			jedis = RedisManager.getJedis();
			jedisPool = RedisManager.getJedisPool();
			value = jedis.lrange(key, 0l, -1l);
		} catch (JedisConnectionException e) {
			logger.error("lrangeAll,失败key=" + key, e);
			borrowOrOprSuccess = false;
			if (jedis != null)
				jedisPool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				jedisPool.returnResource(jedis);
		}
		return value;
	}

	public static Long ttl(String key) {
		Jedis jedis = null;
		JedisPool jedisPool = null;
		Long value = 0l;
		boolean borrowOrOprSuccess = true;
		try {
			jedis = RedisManager.getJedis();
			jedisPool = RedisManager.getJedisPool();
			value = jedis.ttl(key);
		} catch (JedisConnectionException e) {
			logger.error("ttl,失败key=" + key, e);
			borrowOrOprSuccess = false;
			if (jedis != null)
				jedisPool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				jedisPool.returnResource(jedis);
		}
		return value;
	}

    public static void main(String[] args) {
	}
    
    
    
    
    
    
    


}
