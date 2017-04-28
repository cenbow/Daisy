package com.yourong.core.weixin.manager;

import java.util.Map;

public interface WeixinApiManager {

	/**
	 * 
	 * @Description:微信api获取accessToken
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月24日 下午3:33:46
	 */
	public String getAccessToken();

	/**
	 * 
	 * @Description:微信api获取Ticket
	 * @param accessToken
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月24日 下午5:36:13
	 */
	public String getTicket(String accessToken);

	/**
	 * 
	 * @Description:微信api获取签名
	 * @param url
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月24日 下午6:02:00
	 */
	public Map<String, String> getSign(String url);
}
