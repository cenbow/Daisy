package com.yourong.core.uc.manager.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;

import com.google.common.collect.Maps;
import com.yourong.core.uc.manager.XmlAnalyticalManager;

@Component
public class XmlAnalyticalManagerImpl implements XmlAnalyticalManager {

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
	public String XmlAnalytical(String systemType,
			String versionNum, String path) throws Exception {

		HashMap<Object, Object> versionMap = Maps.newHashMap();
		SAXReader reader = new SAXReader();
		String versionInfo = "";

		Document document = reader.read(new File(path));

		Element root = document.getRootElement();

		List<Element> version = root.elements("Version");

		for (Element el : version) {

			if (el.attributeValue("versionNum").equals(versionNum)) {
				if (el.elementText("systemType").equals(systemType)) {
					versionInfo = versionInfo
							+ el.elementText("VersionInfo");
				}
			}

		}
		
		versionInfo = versionInfo.replaceAll(" ","");
		return versionInfo;

	}

}
