package com.yourong.core.mc.model.biz;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.yourong.core.mc.model.PdGeneralMonth;
import com.yourong.core.mc.model.PdRegionMonth;

public class ChannelData implements Serializable  {
	
	private List<PdGeneralMonth> listTotalAmount;
	
	private PdGeneralMonth projectData;
	
	private List<PdRegionMonth> listRegionMonth;
	
	private BigDecimal totalInvest;
	
	private Long memberCount;
	
	private BigDecimal totalInvestInterest;
	
	private Long transactionCount;
	//成功履约项目总数
	private Long totalProjectCount;
	//成功借款人总数
	private Long totalBorrowiMemberCount; 
    //成功投资人总数
	private Long totalInvestMemberCount;
	public Long getTotalProjectCount() {
		return totalProjectCount;
	}
	public void setTotalProjectCount(Long totalProjectCount) {
		this.totalProjectCount = totalProjectCount;
	}

	public Long getTotalBorrowiMemberCount() {
		return totalBorrowiMemberCount;
	}

	public void setTotalBorrowiMemberCount(Long totalBorrowiMemberCount) {
		this.totalBorrowiMemberCount = totalBorrowiMemberCount;
	}

	public Long getTotalInvestMemberCount() {
		return totalInvestMemberCount;
	}

	public void setTotalInvestMemberCount(Long totalInvestMemberCount) {
		this.totalInvestMemberCount = totalInvestMemberCount;
	}

	private int projectTotalCount;

	public int getProjectTotalCount() {
		return projectTotalCount;
	}

	public void setProjectTotalCount(int projectTotalCount) {
		this.projectTotalCount = projectTotalCount;
	}

	public List<PdGeneralMonth> getListTotalAmount() {
		return listTotalAmount;
	}

	public void setListTotalAmount(List<PdGeneralMonth> listTotalAmount) {
		this.listTotalAmount = listTotalAmount;
	}

	public PdGeneralMonth getProjectData() {
		return projectData;
	}

	public void setProjectData(PdGeneralMonth projectData) {
		this.projectData = projectData;
	}

	public List<PdRegionMonth> getListRegionMonth() {
		return listRegionMonth;
	}

	public void setListRegionMonth(List<PdRegionMonth> listRegionMonth) {
		this.listRegionMonth = listRegionMonth;
	}

	public BigDecimal getTotalInvest() {
		return totalInvest;
	}

	public void setTotalInvest(BigDecimal totalInvest) {
		this.totalInvest = totalInvest;
	}

	public Long getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(Long memberCount) {
		this.memberCount = memberCount;
	}

	public BigDecimal getTotalInvestInterest() {
		return totalInvestInterest;
	}

	public void setTotalInvestInterest(BigDecimal totalInvestInterest) {
		this.totalInvestInterest = totalInvestInterest;
	}

	public Long getTransactionCount() {
		return transactionCount;
	}

	public void setTransactionCount(Long transactionCount) {
		this.transactionCount = transactionCount;
	}
	
	
	
		
}
