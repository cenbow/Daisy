package com.yourong.core.sys.util;

import java.net.InetAddress;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourong.common.cache.RedisManager;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.PropertiesUtil;

public class GetVersion {

	private static Logger logger = LoggerFactory.getLogger(GetVersion.class);

	public void init() {
		try {
			logger.info("获取版本 init...");
			if (PropertiesUtil.isDev()) {
				return;
			}
			String localName = InetAddress.getLocalHost().getHostName();
			String lastVersion = DateUtils.getStrFromDate(new Date(Long.valueOf(PropertiesUtil.getStaticResourceVersion())),
					DateUtils.TIME_PATTERN);
			String nowTime = DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN);
			String info = "打包时间:" + lastVersion + ", 启动时间:" + nowTime;
			RedisManager.hset("local:context", localName, info);
			logger.info("localInfo: localName={} lastVersion={}", localName, lastVersion);
		} catch (Exception e) {
			logger.error("加载版本信息失败", e);
		}
	}

	public void close() {
		logger.info("获取版本 close...");
	}

}
