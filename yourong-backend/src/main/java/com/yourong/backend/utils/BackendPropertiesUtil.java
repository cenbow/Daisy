package com.yourong.backend.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * backend项目获取配置项
 * Created by py on 2015/6/23.
 */
public final  class BackendPropertiesUtil {
    private static final Logger logger = LoggerFactory.getLogger(BackendPropertiesUtil.class);
    private static final String SEND_MESSAGE_THEAD = "send.message.thead";
    private static Properties webloadProperties;
    private static String configFilePath = "backend_config.properties";
    private BackendPropertiesUtil(){
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

    /**
     * 发送消息的线程数量
     * @return
     */
    public  static int  getSendMessageThreadCount(){
        int i = 1;
        String s = getProperties(SEND_MESSAGE_THEAD);
        i = Integer.valueOf(s);
        return i;
    }









}
