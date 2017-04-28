package com.yourong.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Date;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourong.common.cache.RedisManager;

public class VersionListener implements ServletContextListener {

	private static final Logger logger = LoggerFactory.getLogger(VersionListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			String path = sce.getServletContext().getRealPath("/META-INF/MANIFEST.MF");
			File manifest = new File(path);
			if (!manifest.exists()) {
				logger.error("加载版本信息失败，获取不到MANIFEST, path={}", path);
				return;
			}
			String lastVersion = null;
			InputStream inStream = new FileInputStream(manifest);
			Properties pros = new Properties();
			pros.load(inStream);
			String versionInfo = pros.getProperty("App-Version-static", "");
			logger.info("加载版本信息path={}...获取MANIFEST.App-Version-static, value={}", path, versionInfo);
			if(StringUtil.isBlank(versionInfo)) {
				lastVersion = "失败";
			} else if (StringUtil.isNumeric(versionInfo)) {
				versionInfo = DateUtils.getStrFromDate(new Date(Long.valueOf(versionInfo)), DateUtils.TIME_PATTERN);
			}
			String localName = InetAddress.getLocalHost().getHostName() + sce.getServletContext().getContextPath();
			String nowTime = DateUtils.getStrFromDate(DateUtils.getCurrentDate(), DateUtils.TIME_PATTERN);
			String info = "打包时间:" + versionInfo + ", 启动时间:" + nowTime;
			RedisManager.hset("local:context", localName, info);
			logger.info("localInfo: localName={} lastVersion={}", localName, lastVersion);
		} catch (Exception e) {
			logger.error("加载版本信息失败", e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}

}
