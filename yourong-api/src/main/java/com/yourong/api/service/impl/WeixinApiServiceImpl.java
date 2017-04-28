package com.yourong.api.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourong.api.service.WeixinApiService;
import com.yourong.core.weixin.manager.WeixinApiManager;

@Service
public class WeixinApiServiceImpl implements WeixinApiService {

	private static Logger logger = LoggerFactory.getLogger(WeixinApiServiceImpl.class);

	@Autowired
	private WeixinApiManager WeiXinApiManager;

	@Override
	public Map<String, String> getSign(String url) {
		return WeiXinApiManager.getSign(url);
	}

}
