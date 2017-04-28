package com.yourong.core.sales;

import java.io.Serializable;
import java.util.Date;

public class SalesPromotion implements Serializable {
	private Long id;
	private String name;
	private String obtainConditionsJson;
	private String useConditionsJson;
	private Date startTime;
	private Date endTime;
	
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
	public String getObtainConditionsJson() {
		return obtainConditionsJson;
	}
	public void setObtainConditionsJson(String obtainConditionsJson) {
		this.obtainConditionsJson = obtainConditionsJson;
	}
	public String getUseConditionsJson() {
		return useConditionsJson;
	}
	public void setUseConditionsJson(String useConditionsJson) {
		this.useConditionsJson = useConditionsJson;
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
	
	

}
