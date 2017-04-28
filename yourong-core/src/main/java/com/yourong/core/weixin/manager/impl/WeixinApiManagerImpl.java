package com.yourong.core.weixin.manager.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.common.cache.RedisWeiXinClient;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.common.weixin.WeixinApiUtil;
import com.yourong.core.sys.manager.SysDictManager;
import com.yourong.core.weixin.manager.WeixinApiManager;

/**
 * 
 * @desc 微信jsApi封装类
 * @author wangyanji 2015年12月24日下午3:35:52
 */
@Component
public class WeixinApiManagerImpl implements WeixinApiManager {

	private static Logger logger = LoggerFactory.getLogger(WeixinApiManagerImpl.class);

	@Autowired
	private SysDictManager sysDictManager;

	@Override
	public String getAccessToken() {
		try {
			// 从redis获取缓存
			String token = RedisWeiXinClient.getAccessToken();
			if (StringUtil.isNotBlank(token)) {
				return token;
			}
			// 重新获取token
			reflashToken();
			return RedisWeiXinClient.getAccessToken();
		} catch (ManagerException e) {
			logger.error("微信api获取accessToken失败", e);
		}
		return null;
	}

	@Override
	public String getTicket(String accessToken) {
		try {
			if (StringUtil.isBlank(accessToken)) {
				return null;
			}
			// 从redis获取缓存
			String ticket = RedisWeiXinClient.getTicket();
			if (StringUtil.isNotBlank(ticket)) {
				return ticket;
			}
			// 重新获取ticket
			reflashTicket();
			return RedisWeiXinClient.getTicket();
		} catch (ManagerException e) {
			logger.error("微信api获取accessToken失败", e);
		}
		return null;
	}

	@Override
	public Map<String, String> getSign(String url) {
		String ticket = getTicket(getAccessToken());
		if (StringUtil.isBlank(ticket)) {
			return null;
		}
		return WeixinApiUtil.getSign(ticket, url);
	}

	/**
	 * 
	 * @Description:重新获取token并放入字典缓存
	 * @author: wangyanji
	 * @throws ManagerException
	 * @time:2015年12月24日 下午4:53:45
	 */
	private boolean reflashToken() throws ManagerException {
		Map<String, Object> retMap = WeixinApiUtil.getAccessToken(PropertiesUtil.getWeixinAppID(), PropertiesUtil.getWeixinAppsecret());
		if (retMap == null) {
			return false;
		}
		String accessToken = retMap.get("access_token").toString();
		String expiresIn = retMap.get("expires_in").toString();
		RedisWeiXinClient.setAccessToken(accessToken, Integer.parseInt(expiresIn));
		return true;
	}

	/**
	 * 
	 * @Description:重新获取ticket并放入字典缓存
	 * @author: wangyanji
	 * @throws ManagerException
	 * @time:2015年12月24日 下午4:53:45
	 */
	private boolean reflashTicket() throws ManagerException {
		// 获取token
		String token = getAccessToken();
		if (StringUtil.isBlank(token)) {
			return false;
		}
		// 获取ticket
		Map<String, Object> retMap = WeixinApiUtil.getTicket(token);
		if (retMap == null) {
			return false;
		}
		String ticket = retMap.get("ticket").toString();
		String expiresIn = retMap.get("expires_in").toString();
		RedisWeiXinClient.setTicket(ticket, Integer.parseInt(expiresIn));
		return true;
	}

}
