package com.yourong.core.mc.model.biz;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class ActivityBiz implements Serializable  {

	private Long id;
	private String name;
	private int type;
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
	private Date startTime;
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
	private Date endTime;
	private String obtainConditionsJson;
	private String ruleParameterJson;
	private boolean validityFlag;
	private String ruleType;
	private Integer bizType;
	private String description;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getObtainConditionsJson() {
		return obtainConditionsJson;
	}
	public void setObtainConditionsJson(String obtainConditionsJson) {
		this.obtainConditionsJson = obtainConditionsJson;
	}
	public String getRuleParameterJson() {
		return ruleParameterJson;
	}
	public void setRuleParameterJson(String ruleParameterJson) {
		this.ruleParameterJson = ruleParameterJson;
	}
	public boolean isValidityFlag() {
		return validityFlag;
	}
	public void setValidityFlag(boolean validityFlag) {
		this.validityFlag = validityFlag;
	}
	public String getRuleType() {
		return ruleType;
	}
	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}
	public Integer getBizType() {
		return bizType;
	}
	public void setBizType(Integer bizType) {
		this.bizType = bizType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
