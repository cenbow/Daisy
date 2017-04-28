package com.yourong.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateQuestionMd5 {
	private static Logger logger = LoggerFactory.getLogger(GenerateQuestionMd5.class);

	@SuppressWarnings("unchecked")
	public static String sortParam(List keyList, Map map) {
		String md5_param = "";
		logger.debug(keyList.toString());
		Collections.sort(keyList);
		logger.debug(keyList.toString());
		for (int i = 0; i < keyList.size(); i++) {
			md5_param += map.get(keyList.get(i));
		}
		return md5_param;
	}

	public static void main(String[] args) {
		Map map = new HashMap();
		map.put("user", "1001");
		map.put("site", "100");
		map.put("ctime", "2014-01-13 10:11");
		map.put("email", "keithsun@**.com");
		List keyList = new ArrayList();
		keyList.add("user");
		keyList.add("site");
		keyList.add("ctime");
		keyList.add("email");
		String md5_param = sortParam(keyList, map);
		// md5 equals md5_param + key
		logger.debug(md5_param);
	}
}
