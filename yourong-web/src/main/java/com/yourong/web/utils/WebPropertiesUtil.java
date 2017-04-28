package com.yourong.web.utils;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.SinaPropertiesUtil;

/**
 * web项目获取配置项
 * Created by py on 2015/6/23.
 */
public class WebPropertiesUtil {
    private static final Logger logger = LoggerFactory.getLogger(WebPropertiesUtil.class);
    private static final String YOURONG_WEB_MD5 = "yourong.web.md5";
    private static Properties webloadProperties;
    private static String configFilePath = "web_config.properties";
    private WebPropertiesUtil(){
    }
    public static String getProperties(String key) {
        String tempName = "";
        try {
            if(webloadProperties == null){
                Resource resource = new ClassPathResource(configFilePath);
                webloadProperties = PropertiesLoaderUtils.loadProperties(resource);
            }
            tempName = webloadProperties.getProperty(key);
        } catch (IOException e) {
            logger.error("获取配置文件config.properties失败", e);
        }
        return tempName;
    }
    public static String getMD5Key(){
        return  getProperties(YOURONG_WEB_MD5);
    }

	public static String getSinaCashDeskReturnUrl() {
		String returnUrl = null;
		try {
			int returnType = Integer.parseInt(WebPropertiesUtil.getProperties("sina.cashDesk.returnType"));
			if (TypeEnum.SINA_RETURN_TYPE_CLOSE.getType() == returnType) {
				returnUrl = SinaPropertiesUtil.getProperties("sinaPay.returnUrl");
			}
		} catch (Exception e) {
			logger.error("WEB获取新浪收银台回跳链接失败", e);
		}
		return returnUrl;
	}




}
