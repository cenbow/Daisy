package com.yourong.common.util;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public  final  class SinaPropertiesUtil {
	private static final Logger logger = LoggerFactory.getLogger(SinaPropertiesUtil.class);	
	private static Properties loadProperties;
	private static String sinaConfigFilePath = "sina_pay.properties";
	/**静态资源版本号 */
	private static String STATIC_RESOURCE_VERSION = null;
	
	private SinaPropertiesUtil(){	    
	}

	public static String getProperties(String key) {			
		String tempName = "";
		try {
			if(loadProperties == null){
				Resource resource = new ClassPathResource(sinaConfigFilePath);
				loadProperties = PropertiesLoaderUtils.loadProperties(resource);
			}			
			tempName = loadProperties.getProperty(key);
		} catch (IOException e) {
			logger.error("获取配置文件sina_pay.properties失败", e);
		}
		return tempName;
	}
}
