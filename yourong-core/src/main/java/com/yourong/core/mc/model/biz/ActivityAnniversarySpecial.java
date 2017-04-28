package com.yourong.core.mc.model.biz;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.util.StringUtil;
import com.yourong.core.mc.model.PdGeneralMonth;
import com.yourong.core.mc.model.PdRegionMonth;

public class ActivityAnniversarySpecial implements Serializable  {
	
	
	private Date registerDate;
	
	private Date firstInvestDate;
	
	private String projectName;
	
	private Integer rank;
	
	private BigDecimal totalInvest;
	
	private Integer totalDays;
	
	private Integer transactionCount;
	
	private BigDecimal totalInvestAmount;
	
	private BigDecimal totalInvestInterest;
	
	private Integer number;
	
	private Integer vipLevel;
	
	

	public Integer getVipLevel() {
		return vipLevel;
	}

	public void setVipLevel(Integer vipLevel) {
		this.vipLevel = vipLevel;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	public Date getFirstInvestDate() {
		return firstInvestDate;
	}

	public void setFirstInvestDate(Date firstInvestDate) {
		this.firstInvestDate = firstInvestDate;
	}

	public String getProjectName() {
		if(StringUtil.isBlank(projectName)){
				return "";
		}
		if(!projectName.contains("期")){
			return projectName;
		}
		return projectName.substring(0, projectName.indexOf("期")+1);
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public BigDecimal getTotalInvest() {
		return totalInvest;
	}

	public void setTotalInvest(BigDecimal totalInvest) {
		this.totalInvest = totalInvest;
	}

	public Integer getTotalDays() {
		return totalDays;
	}

	public void setTotalDays(Integer totalDays) {
		this.totalDays = totalDays;
	}


	public Integer getTransactionCount() {
		return transactionCount;
	}

	public void setTransactionCount(Integer transactionCount) {
		this.transactionCount = transactionCount;
	}

	public BigDecimal getTotalInvestAmount() {
		return totalInvestAmount;
	}

	public void setTotalInvestAmount(BigDecimal totalInvestAmount) {
		this.totalInvestAmount = totalInvestAmount;
	}

	public BigDecimal getTotalInvestInterest() {
		return totalInvestInterest;
	}

	public void setTotalInvestInterest(BigDecimal totalInvestInterest) {
		this.totalInvestInterest = totalInvestInterest;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	
	

	
	
		
}
