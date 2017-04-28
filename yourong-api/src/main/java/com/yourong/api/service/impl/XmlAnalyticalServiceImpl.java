package com.yourong.api.service.impl;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.ServletRequestBindingException;

import com.yourong.api.service.XmlAnalyticalService;
import com.yourong.api.utils.APIPropertiesUtil;
import com.yourong.core.uc.manager.XmlAnalyticalManager;

@Service
public class XmlAnalyticalServiceImpl implements XmlAnalyticalService {

	@Autowired
	private XmlAnalyticalManager xmlAnalyticalManager;
	
	 private static final Logger logger = LoggerFactory.getLogger(XmlAnalyticalServiceImpl.class);
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
	public String XmlAnalytical(String systemType,
			String versionNum) {
		String path = this.getClass().getClassLoader().getResource("")
				.getFile()
				+ File.separator + "versionInfo.xml";//获取xml文件路径

		try {
			return xmlAnalyticalManager.XmlAnalytical(systemType, versionNum,path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error( path+"解析XML出错啦~"+"systemType"+systemType+"versionNum"+versionNum);
		}
		return "解析XML出错啦~";
	}

}
