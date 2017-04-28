package com.yourong.core.sales;

import java.io.Serializable;

public class SPRuleMethod implements Serializable {
	private String name;
	private String value;
	private String rule;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}
	
	
}
