package com.yourong.web.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.common.util.StringUtil;

public class TransferForMemberDto {

	/**项目编号**/
	private Long projectId;
	
	/**项目名称**/
	@JSONField(serialize=false)
	private String projectName;
	 
	/** 金额 **/
	private BigDecimal amount;
    
	/** 时间 **/
	private Date happenTime;

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
    
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getHappenTime() {
		return happenTime;
	}

	public void setHappenTime(Date happenTime) {
		this.happenTime = happenTime;
	}

	public String getPrefixProjectName() {
		String name = getProjectName();
		if(StringUtil.isNotBlank(name)){
			if (name.contains("期")) {
				return name.substring(0, name.indexOf("期") + 1);
			} else {
				return name;
			}
		}
		return "";
	}
	
}
