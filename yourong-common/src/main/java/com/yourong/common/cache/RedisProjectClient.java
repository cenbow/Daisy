package com.yourong.common.cache;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourong.common.constant.RedisConstant;
import com.yourong.common.util.StringUtil;
/**
 * 项目redis相关
 */
public class RedisProjectClient {

    private static final Logger log = LoggerFactory.getLogger(RedisProjectClient.class);
    
    /**
     *  设置项目余额
     * @param mobile
     * @return
     */
    public static boolean setProjectBalance(Long projectId, BigDecimal balance){
    	String key = projectId + RedisConstant.REDIS_SEPERATOR
    			+ RedisConstant.REDIS_KEY_PROJECT;
    	return RedisManager.hset(key,
    			RedisConstant.REDIS_FIELD_PROJECT_PROJECT_BALANCE,
    			balance.toPlainString());

    }
    /**
     * 获取项目余额
     * @param mobile
     * @see com.yourong.web.service.ProjectService#getProjectBalanceById(Long projectId)
     * @return
     */
    public static BigDecimal getProjectBalance(Long projectId){
    	String key = projectId + RedisConstant.REDIS_SEPERATOR
    			+ RedisConstant.REDIS_KEY_PROJECT;
    	String projectBalanceStr = RedisManager.hget(key, RedisConstant.REDIS_FIELD_PROJECT_PROJECT_BALANCE);
    	if(StringUtil.isNotBlank(projectBalanceStr)) {
    		return new BigDecimal(projectBalanceStr).setScale(2, BigDecimal.ROUND_HALF_UP);
    	}
    	return null;
    }
    
    /**
     * 清理项目交易明细缓存(原)
     * @param projectId
     */
    public static void clearTransactionDetailForProject(Long projectId){
    	String key = RedisConstant.TRANSACTION_DETAIL_FOR_PROJECT+projectId;
    	RedisManager.removeObject(key);
    }
	
}
