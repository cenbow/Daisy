package com.yourong.api.service;

import java.util.Map;

/**
 * 微信APIservice
 * 
 * @author wangyanji
 *
 */
public interface WeixinApiService {

	/**
	 * 获取签名
	 * 
	 * @param param
	 * @return
	 */
	public Map<String, String> getSign(String url);

}
