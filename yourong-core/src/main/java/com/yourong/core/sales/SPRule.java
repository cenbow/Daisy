package com.yourong.core.sales;

import java.io.Serializable;
import java.util.List;

public class SPRule implements Serializable {
	private String key;
	private List<SPRuleMethod> value;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public List<SPRuleMethod> getValue() {
		return value;
	}
	public void setValue(List<SPRuleMethod> value) {
		this.value = value;
	}
	
	

}
