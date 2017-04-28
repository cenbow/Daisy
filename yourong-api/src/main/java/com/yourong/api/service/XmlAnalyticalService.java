package com.yourong.api.service;

import java.util.HashMap;

import org.springframework.web.bind.ServletRequestBindingException;

import com.yourong.common.domain.ResultDO;
import com.yourong.core.tc.model.Order;

public interface XmlAnalyticalService {
	
	/**
	 * 解析版本信息xml
	 * 
	 * @param req
	 * @param systemType
	 *            系统类型 1-android;2-ios
	 * @param versionNum
	 *            版本号
	 * @return
	 * @throws ServletRequestBindingException
	 */  
	public String XmlAnalytical(String systemType, String versionNum) ;
	
	
	
	

}
