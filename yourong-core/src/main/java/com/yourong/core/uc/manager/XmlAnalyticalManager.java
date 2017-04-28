package com.yourong.core.uc.manager;

import java.util.HashMap;

import org.springframework.web.bind.ServletRequestBindingException;

public interface XmlAnalyticalManager {
	
	/**
	 * 解析版本信息xml
	 * 
	 * @param req
	 * @param systemType
	 *            系统类型 1-android;2-ios
	 * @param versionNum
	 *            版本号
	 * @param path
	 *            versioninfo.xml路径
	 * @return
	 * @throws ServletRequestBindingException
	 */  
	public String XmlAnalytical(String systemType, String versionNum,String path) throws Exception;

}
