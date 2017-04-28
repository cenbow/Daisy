package com.yourong.api.utils;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.google.common.collect.Lists;
import com.yourong.api.cache.MyCacheManager;
import com.yourong.common.util.SpringContextHolder;
import com.yourong.common.util.StringUtil;
import com.yourong.core.sys.model.SysDict;

/**
 * api项目获取配置项
 * Created by py on 2015/6/23.
 */
public final class APIPropertiesUtil {
    private static final Logger logger = LoggerFactory.getLogger(APIPropertiesUtil.class);
    private static Properties webloadProperties;
    private static String configFilePath = "api_config.properties";
    private APIPropertiesUtil(){
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
     * 是否开启提现手续费
     * @return
     */
    public static boolean isOpenFees(){
    	String str = getProperties("isOpenFees");
//        String str = SysServiceUtils.getDictValue("isOpenFees", "isOpen_fees", "0");
    	if(StringUtil.isNotBlank(str) && str.equals("true")){
    		return true;
    	}
    	return false;
    }
    
    /**
     * 提现手续费
     * @return
     */
    public static int getWithdrawalFees(){
    	String fees = getProperties("withdrawalFees");
    	if(StringUtil.isNotBlank(fees)){
    		return Integer.parseInt(fees);
    	}
    	return 0;
    }
    
}
