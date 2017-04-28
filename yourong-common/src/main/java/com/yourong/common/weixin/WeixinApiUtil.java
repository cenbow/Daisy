package com.yourong.common.weixin;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rop.thirdparty.com.alibaba.fastjson.JSON;

import com.yourong.common.util.HttpUtil;
import com.yourong.common.util.StringUtil;

public class WeixinApiUtil {

	private static Logger logger = LoggerFactory.getLogger(WeixinApiUtil.class);

	private static final String tokenUrl = "https://api.weixin.qq.com/cgi-bin/token";

	private static final String grantTypeForToken = "client_credential";

	private static final String typeForTicket = "jsapi";

	private static final String ticketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";

	/**
	 * 
	 * @Description:获取accessToken
	 * @param appid
	 * @param secret
	 * @throws Exception
	 * @author: wangyanji
	 * @time:2015年12月3日 下午2:05:54
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getAccessToken(String appid, String secret) {
		Map<String, String> params = new TreeMap<String, String>();
		params.put("grant_type", grantTypeForToken);
		params.put("appid", appid);
		params.put("secret", secret);
		String retCode = HttpUtil.doGet(tokenUrl, params);
		if (StringUtil.isBlank(retCode)) {
			logger.error("微信JsApi获取accessToken返回失败: retCode={}" + retCode);
			return null;
		}
		Map<String, Object> tokenMap = JSON.parseObject(retCode, java.util.HashMap.class);
		String accessToken = tokenMap.get("access_token").toString();
		String expiresIn = tokenMap.get("expires_in").toString();
		logger.info("获取微信jsAccessToken返回： accessToken={},expiresIn={}", accessToken, expiresIn);
		if (StringUtil.isBlank(accessToken) || StringUtil.isBlank(expiresIn)) {
			logger.error("微信JsApi获取accessToken返回失败:" + tokenMap);
			return null;
		}
		logger.info("微信JsApi获取accessToken返回:" + tokenMap);
		return tokenMap;
	}

	/**
	 * 
	 * @Description:获取ticket
	 * @param accessToken
	 * @return
	 * @throws Exception
	 * @author: wangyanji
	 * @time:2015年12月3日 下午2:42:53
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getTicket(String accessToken) {
		Map<String, String> params = new TreeMap<String, String>();
		params.put("type", typeForTicket);
		params.put("access_token", accessToken);
		String retCode = HttpUtil.doGet(ticketUrl, params);
		if (StringUtil.isBlank(retCode)) {
			logger.error("微信JsApi获取ticket返回失败: retCode={}" + retCode);
			return null;
		}
		Map<String, Object> ticketMap = JSON.parseObject(retCode, java.util.HashMap.class);
		String ticket = ticketMap.get("ticket").toString();
		String expiresIn = ticketMap.get("expires_in").toString();
		if (StringUtil.isBlank(ticket) || StringUtil.isBlank(expiresIn)) {
			logger.error("微信JsApi获取ticket返回失败:" + ticketMap);
			return null;
		}
		logger.info("微信JsApi获取Ticket返回:" + ticketMap);
		return ticketMap;
	}

	/**
	 * 
	 * @Description:整合签名方法
	 * @param ticket
	 * @param url
	 * @author: wangyanji
	 * @time:2015年12月3日 下午2:42:37
	 */
	public static Map<String, String> getSign(String ticket, String url) {
		// 注意 URL 一定要动态获取，不能 hardcode
		return sign(ticket, url);
	};

	/**
	 * 
	 * @Description:签名方法
	 * @param jsapi_ticket
	 * @param url
	 * @return
	 * @author: wangyanji
	 * @time:2015年12月3日 下午2:41:17
	 */
	public static Map<String, String> sign(String jsapi_ticket, String url) {
		Map<String, String> ret = new HashMap<String, String>();
		String nonce_str = create_nonce_str();
		String timestamp = create_timestamp();
		String string1;
		String signature = "";

		// 注意这里参数名必须全部小写，且必须有序
		string1 = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonce_str + "&timestamp=" + timestamp + "&url=" + url;

		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(string1.getBytes("UTF-8"));
			signature = byteToHex(crypt.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		ret.put("url", url);
		// ret.put("jsapi_ticket", jsapi_ticket);
		ret.put("nonceStr", nonce_str);
		ret.put("timestamp", timestamp);
		ret.put("signature", signature);

		return ret;
	}

	private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	private static String create_nonce_str() {
		return UUID.randomUUID().toString();
	}

	private static String create_timestamp() {
		return Long.toString(System.currentTimeMillis() / 1000);
	}

}
