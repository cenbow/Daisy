package com.yourong.core.cache;

import java.util.*;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yourong.common.cache.RedisManager;
import com.yourong.common.constant.RedisConstant;
import com.yourong.core.tc.model.biz.TransactionForProject;

/**
 * 项目redis相关
 */
public class RedisForProjectClient {

	private static final Logger logger = LoggerFactory.getLogger(RedisForProjectClient.class);
	public static final int INDEX = 4;//redis数据库

	private static String transactionDetailKey = RedisConstant.TRANSACTION_DETAIL_FOR_PROJECT + RedisConstant.REDIS_SEPERATOR;

    private static String TRANSACTIONDETAILKEYFORINDEX = RedisConstant.REDIS_KEY_INDEX_TRANCTION + RedisConstant.REDIS_SEPERATOR+ RedisConstant.REDIS_KEY_INDEX_HISTROY;

    private static String TRANSACTIONDETAILKEYFOR_QUADRUPLEGIFT = RedisConstant.REDIS_KEY_INDEX_TRANCTION + RedisConstant.REDIS_SEPERATOR+ RedisConstant.REDIS_KEY_QUADRUPLEGIFT;
    /**
     * 清理项目交易明细缓存(改)
     */
    public static void clearTransactionDetail(Long projectId){
    	String key = transactionDetailKey + projectId;
    	@SuppressWarnings("unchecked")
		Map<String,Object> transMap = RedisManager.hgetAll(key);
		if (transMap != null && transMap.size() > 0) {
			List<String> fields = Lists.newArrayList();
			Iterator<String> it = transMap.keySet().iterator();
			while (it.hasNext()) {
				String field = (String) it.next();
				fields.add(field);
			}
			RedisManager.hdel(key, (String[])fields.toArray(new String[fields.size()]));
		}
    }
    
    /**
     * 项目交易明细批量添加到缓存
     */
    public static void addBatchTransactionDetail(Long projectId,List<TransactionForProject> transactionForProjects){
    	String key = transactionDetailKey + projectId;
	    for (TransactionForProject transactionForProject:transactionForProjects) {
			RedisManager.hset(key, String.valueOf(transactionForProject.getId()), JSON.toJSONString(transactionForProject));
		}
	    RedisManager.expire(key, 24*60*60);
    }
    
    
    /**
     * 从缓存中取出所有项目交易明细
     */
    public static List<TransactionForProject> getAllTransactionDetail(Long projectId){
    	String key = transactionDetailKey + projectId;
    	List<TransactionForProject> transactionForProjects = Lists.newArrayList();
    	Map<String, String> transactionMap = RedisManager.hgetAll(key);
    	TreeMap<String, String> treeMap = new TreeMap<String, String>(transactionMap);
		Set<Entry<String,String>> entrySet = treeMap.entrySet();
		for (Entry<String, String> entry : entrySet) {
			TransactionForProject project = JSONObject.parseObject(entry.getValue(), TransactionForProject.class);
			transactionForProjects.add(project);
		}
		return transactionForProjects;
    }
    
    /**
     * 单个添加项目交易明细
     */
    public static void addTransactionDetail(TransactionForProject transactionForProject){
		if (transactionForProject != null) {
			String key = transactionDetailKey + transactionForProject.getProjectId();
			RedisManager.hset(key, String.valueOf(transactionForProject.getId()),
					JSON.toJSONString(transactionForProject));
		}
    }

	/**
     * 单个添加项目交易明细   首页用
     */
    public static void addTransactionDetailForIndex(TransactionForProject transactionForProject){
		if (transactionForProject != null) {
			RedisManager.zadd(INDEX, TRANSACTIONDETAILKEYFORINDEX, transactionForProject.getId(), JSON.toJSONString(transactionForProject));
		}
		RedisManager.expire(TRANSACTIONDETAILKEYFORINDEX, 2*24*60*60);
    }

	/**
	 * 单个添加四重礼到redist   首页用
	 */
	public static void addTransactionDetailForQuadruplegift(TransactionForProject transactionForProject){
		if (transactionForProject != null) {
			transactionForProject.setInsertDate(new Date());
			RedisManager.zadd(INDEX,TRANSACTIONDETAILKEYFOR_QUADRUPLEGIFT, transactionForProject.getId(), JSON.toJSONString(transactionForProject));
		}
		RedisManager.expire(TRANSACTIONDETAILKEYFOR_QUADRUPLEGIFT, 2*24*60*60);
	}

	/**
	 * 单个添加项目交易明细   首页用
	 */
	public static Set<String> getTransactionDetailForIndex(long start,long end){
		return  RedisManager.zrange(INDEX,TRANSACTIONDETAILKEYFORINDEX, start, end);
	}

	/**
	 * 获取添加项目四重礼明细   首页用
	 */
	public static Set<String> getTransactionDetailForQuadruplegift(long start,long end){
		return  RedisManager.zrange(INDEX,TRANSACTIONDETAILKEYFOR_QUADRUPLEGIFT, start, end);
	}


}
