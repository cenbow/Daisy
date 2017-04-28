package com.yourong.common.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourong.common.constant.RedisConstant;

/**
 * 微信redis相关
 */
public class RedisWeiXinClient {

	private static final Logger log = LoggerFactory.getLogger(RedisWeiXinClient.class);

	/**
	 * 
	 * @Description:获取accessToken
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月25日 下午5:25:50
	 */
	public static String getAccessToken() {
		return RedisManager
				.get(RedisConstant.REDIS_KEY_WEIXIN + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_WEIXIN_ACCESSTOKEN);
	}

	/**
	 * 
	 * @Description:设置accessToken
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月25日 下午5:25:50
	 */
	public static void setAccessToken(String accessTokenValue, int expiresIn) {
		String key = RedisConstant.REDIS_KEY_WEIXIN + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_WEIXIN_ACCESSTOKEN;
		// 官方有效期7200秒，本地默认减1800秒
		RedisManager.set(key, accessTokenValue);
		RedisManager.expire(key, expiresIn - 1800);
	}

	/**
	 * 
	 * @Description:获取ticket
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月25日 下午5:25:50
	 */
	public static String getTicket() {
		return RedisManager.get(RedisConstant.REDIS_KEY_WEIXIN + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_WEIXIN_TICKET);
	}

	/**
	 * 
	 * @Description:设置ticket
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月25日 下午5:25:50
	 */
	public static void setTicket(String ticketValue, int expiresIn) {
		String key = RedisConstant.REDIS_KEY_WEIXIN + RedisConstant.REDIS_SEPERATOR + RedisConstant.REDIS_KEY_WEIXIN_TICKET;
		// 官方有效期7200秒，本地默认减1800秒
		RedisManager.set(key, ticketValue);
		RedisManager.expire(key, expiresIn - 1800);
	}
}
