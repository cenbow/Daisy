package com.yourong.api.dto;

import java.util.List;

import com.yourong.core.ic.model.biz.ProjectInvestingDto;

/**
 * 企业借款数据
 * 
 * @author Administrator
 *
 */
public class EnterpriseProjectInfoDto {

	/** 履约中的项目 **/
	private List<ProjectInvestingDto> projectInvestingDtos;

	/** 企业已借款总额 */
	private String currentTotalAmount;

	/** 企业历史借款中款 */
	private String historyTotalAmount;

	/** 企业已还总额 */
	private String repaymentTotalAmount;

	/** 企业已使用信用额度 */
	private String usedCreditAmount;

	/** 企业信用额度 */
	private String creditAmount;

	public List<ProjectInvestingDto> getProjectInvestingDtos() {
		return projectInvestingDtos;
	}

	public void setProjectInvestingDtos(List<ProjectInvestingDto> projectInvestingDtos) {
		this.projectInvestingDtos = projectInvestingDtos;
	}

	public String getCurrentTotalAmount() {
		return currentTotalAmount;
	}

	public void setCurrentTotalAmount(String currentTotalAmount) {
		this.currentTotalAmount = currentTotalAmount;
	}

	public String getHistoryTotalAmount() {
		return historyTotalAmount;
	}

	public void setHistoryTotalAmount(String historyTotalAmount) {
		this.historyTotalAmount = historyTotalAmount;
	}

	public String getRepaymentTotalAmount() {
		return repaymentTotalAmount;
	}

	public void setRepaymentTotalAmount(String repaymentTotalAmount) {
		this.repaymentTotalAmount = repaymentTotalAmount;
	}

	public String getUsedCreditAmount() {
		return usedCreditAmount;
	}

	public String getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(String creditAmount) {
		this.creditAmount = creditAmount;
	}

	public void setUsedCreditAmount(String usedCreditAmount) {
		this.usedCreditAmount = usedCreditAmount;
	}
	
}
