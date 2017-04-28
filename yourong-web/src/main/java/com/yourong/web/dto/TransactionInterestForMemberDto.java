package com.yourong.web.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.common.util.StringUtil;

public class TransactionInterestForMemberDto {
	/**交易编号**/
	private Long transactionId;

	/**项目编号**/
	private Long projectId;
	
	/**项目名称**/
	@JSONField(serialize=false)
	private String projectName;
	 
	/**应付利息**/
    private BigDecimal payableInterest;

    /**应付本金**/
    private BigDecimal payablePrincipal;
    
    /**计息结束时间**/
    private Date endDate;
    
    /**还款类型**/
    private int payType;

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

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

	public BigDecimal getPayableInterest() {
		return payableInterest;
	}

	public void setPayableInterest(BigDecimal payableInterest) {
		this.payableInterest = payableInterest;
	}

	public BigDecimal getPayablePrincipal() {
		return payablePrincipal;
	}

	public void setPayablePrincipal(BigDecimal payablePrincipal) {
		this.payablePrincipal = payablePrincipal;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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

	public int getPayType() {
		return payType;
	}

	public void setPayType(int payType) {
		this.payType = payType;
	}
	
}
