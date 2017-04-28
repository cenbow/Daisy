package com.yourong.core.sales.engine;

import java.util.ArrayList;
import java.util.List;


/**
 * 引擎枚举
 * 用于配置引擎规则实现类
 */
public enum SPEnum {

	/**
	 * 会员促销规则
	 */
	member("com.yourong.core.sales.rule.MemberSalesRule"),
	
	/**
	 * 项目会员促销规则
	 */
	project("com.yourong.core.sales.rule.ProjectSalesRule"),
	
	/**
	 * 交易会员促销规则
	 */
	transaction("com.yourong.core.sales.rule.TransactionSalesRule");
	
	private String value = "";
	
	public String getValue(){
		return this.value;
	}
	
	private SPEnum(String value){
		this.value = value;
	}
	
	public static List<String> getNames(){
		SPEnum[] salesEnum = SPEnum.values();
		List<String> names = new ArrayList<String>();
		for(SPEnum se : salesEnum){
			names.add(se.name());
		}
		return names;
	}
}
