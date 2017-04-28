package com.yourong.core.sales.engine;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yourong.core.sales.rule.SPRuleBase;

/**
 * 引擎规则工厂
 * 负责创建引擎规则实例，一个规则只创建一次
 */
public class SPRuleFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(SPRuleFactory.class);

	/**
	 * 促销规则类容器
	 */
	private static Map<String, SPRuleBase>  salesRuleMap = new HashMap<String, SPRuleBase>();
	
	/**
	 * 创建促销规则类
	 * @param key
	 * @return
	 */
	public static SPRuleBase createSalesRule(String key){
		return loadSalesRule(key);
	}
	
	/**
	 * 加载促销规则类
	 * 为了性能考虑，促销规则类在JVM中只加载一次
	 * @param key
	 * @return
	 */
	private synchronized static SPRuleBase loadSalesRule(String key){
		SPRuleBase salesRule = null;
		if(!salesRuleMap.containsKey(key)){
			if(SPEnum.getNames().contains(key)){
				String className = SPEnum.valueOf(key).getValue();
				try {
					salesRule = (SPRuleBase)Class.forName(className).newInstance();
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
					logger.error("创建促销规则类异常", e);
				}
				salesRuleMap.put(key, salesRule);
			}
		}else{
			salesRule = salesRuleMap.get(key);
		}
		return salesRule;
	}
}
