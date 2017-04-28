package com.yourong.core.mc.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class PdGeneralMonth implements Serializable {
	/**
	 * 数据频道综合月更表
	 */
	private static final long serialVersionUID = -1556656446043628674L;

	/** 月份 **/
	private String month;

	/**累计投资总额 **/
	private BigDecimal totalInvestAmount;

	/** 项目类型分布,债权占比 **/
	private BigDecimal debtRate;

	/** 项目类型分布,直投占比 **/
	private BigDecimal directRate;

	/** 项目期限分布,0~1月占比**/
	private BigDecimal cycle01Rate;

	/** 项目期限分布,1~2月占比 **/
	private BigDecimal cycle12Rate;

	/** 项目期限分布,2~3月占比 **/
	private BigDecimal cycle23Rate;

	/** 项目期限分布,3~6月占比 **/
	private BigDecimal cycle36Rate;

	/** 项目期限分布,6~12月占比 **/
	private BigDecimal cycle612Rate;

	/** 项目期限分布,其他占比 **/
	private BigDecimal cycleElseRate;

	/** 投资终端分布,pc占比 **/
	private BigDecimal webRate;

	/** 投资终端分布,移动端占比 **/
	private BigDecimal mobileRate;
	
	private BigDecimal unpaidAmount;
	
	private BigDecimal paidAmount;

	/** 项目还款情况,待还金额 **/
	private BigDecimal unpaidAmountRate;

	/** 项目还款情况,已还金额 **/
	private BigDecimal paidAmountRate;

	/**投资人数性别占比,女投资人数占比**/
	private BigDecimal womanNumRate;

	/**投资人数性别占比,男投资人数占比**/
	private BigDecimal manNumRate;

	/**投资额度性别占比,男投资额占比**/
	private BigDecimal womanInvestAmountRate;

	/** 投资额度性别占比,女投资额占比**/
	private BigDecimal manInvestAmountRate;

	/** 用户年龄分布,50后占比 **/
	private BigDecimal year50Rate;

	/** 用户年龄分布,60后占比 */
	private BigDecimal year60Rate;

	/** 用户年龄分布,70后占比*/
	private BigDecimal year70Rate;

	/** 用户年龄分布,80后占比 */
	private BigDecimal year80Rate;

	/** 用户年龄分布,90后占比 */
	private BigDecimal year90Rate;
	
	/**用户年龄分布,其他占比*/
	private BigDecimal yearElseRate;
	
	/**逾期信息披露个数*/
	private Integer projectCountOverdue;
	
	/**累计逾期金额*/
	private BigDecimal overduePrincipal;
	
	/**项目逾期率*/
	private BigDecimal projectCountOverdueRate;
	
	/**金额逾期率*/
	private BigDecimal overduePrincipalRate;
	
	/**平台历史兑付率*/
	private BigDecimal payRate;

	
	
	public BigDecimal getUnpaidAmount() {
		return unpaidAmount;
	}

	public void setUnpaidAmount(BigDecimal unpaidAmount) {
		this.unpaidAmount = unpaidAmount;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public BigDecimal getTotalInvestAmount() {
		return totalInvestAmount;
	}

	public void setTotalInvestAmount(BigDecimal totalInvestAmount) {
		this.totalInvestAmount = totalInvestAmount;
	}

	public BigDecimal getDebtRate() {
		return debtRate;
	}

	public void setDebtRate(BigDecimal debtRate) {
		this.debtRate = debtRate;
	}

	public BigDecimal getDirectRate() {
		return directRate;
	}

	public void setDirectRate(BigDecimal directRate) {
		this.directRate = directRate;
	}

	public BigDecimal getCycle01Rate() {
		return cycle01Rate;
	}

	public void setCycle01Rate(BigDecimal cycle01Rate) {
		this.cycle01Rate = cycle01Rate;
	}

	public BigDecimal getCycle12Rate() {
		return cycle12Rate;
	}

	public void setCycle12Rate(BigDecimal cycle12Rate) {
		this.cycle12Rate = cycle12Rate;
	}

	public BigDecimal getCycle23Rate() {
		return cycle23Rate;
	}

	public void setCycle23Rate(BigDecimal cycle23Rate) {
		this.cycle23Rate = cycle23Rate;
	}

	public BigDecimal getCycle36Rate() {
		return cycle36Rate;
	}

	public void setCycle36Rate(BigDecimal cycle36Rate) {
		this.cycle36Rate = cycle36Rate;
	}

	public BigDecimal getCycle612Rate() {
		return cycle612Rate;
	}

	public void setCycle612Rate(BigDecimal cycle612Rate) {
		this.cycle612Rate = cycle612Rate;
	}

	public BigDecimal getCycleElseRate() {
		return cycleElseRate;
	}

	public void setCycleElseRate(BigDecimal cycleElseRate) {
		this.cycleElseRate = cycleElseRate;
	}


	public BigDecimal getWebRate() {
		return webRate;
	}

	public void setWebRate(BigDecimal webRate) {
		this.webRate = webRate;
	}

	public BigDecimal getMobileRate() {
		return mobileRate;
	}

	public void setMobileRate(BigDecimal mobileRate) {
		this.mobileRate = mobileRate;
	}

	public BigDecimal getUnpaidAmountRate() {
		return unpaidAmountRate;
	}

	public void setUnpaidAmountRate(BigDecimal unpaidAmountRate) {
		this.unpaidAmountRate = unpaidAmountRate;
	}

	public BigDecimal getPaidAmountRate() {
		return paidAmountRate;
	}

	public void setPaidAmountRate(BigDecimal paidAmountRate) {
		this.paidAmountRate = paidAmountRate;
	}

	public BigDecimal getWomanNumRate() {
		return womanNumRate;
	}

	public void setWomanNumRate(BigDecimal womanNumRate) {
		this.womanNumRate = womanNumRate;
	}

	public BigDecimal getManNumRate() {
		return manNumRate;
	}

	public void setManNumRate(BigDecimal manNumRate) {
		this.manNumRate = manNumRate;
	}

	public BigDecimal getWomanInvestAmountRate() {
		return womanInvestAmountRate;
	}

	public void setWomanInvestAmountRate(BigDecimal womanInvestAmountRate) {
		this.womanInvestAmountRate = womanInvestAmountRate;
	}

	public BigDecimal getManInvestAmountRate() {
		return manInvestAmountRate;
	}

	public void setManInvestAmountRate(BigDecimal manInvestAmountRate) {
		this.manInvestAmountRate = manInvestAmountRate;
	}

	public BigDecimal getYear50Rate() {
		return year50Rate;
	}

	public void setYear50Rate(BigDecimal year50Rate) {
		this.year50Rate = year50Rate;
	}

	public BigDecimal getYear60Rate() {
		return year60Rate;
	}

	public void setYear60Rate(BigDecimal year60Rate) {
		this.year60Rate = year60Rate;
	}

	public BigDecimal getYear70Rate() {
		return year70Rate;
	}

	public void setYear70Rate(BigDecimal year70Rate) {
		this.year70Rate = year70Rate;
	}

	public BigDecimal getYear80Rate() {
		return year80Rate;
	}

	public void setYear80Rate(BigDecimal year80Rate) {
		this.year80Rate = year80Rate;
	}

	public BigDecimal getYear90Rate() {
		return year90Rate;
	}

	public void setYear90Rate(BigDecimal year90Rate) {
		this.year90Rate = year90Rate;
	}

	public BigDecimal getYearElseRate() {
		return yearElseRate;
	}

	public void setYearElseRate(BigDecimal yearElseRate) {
		this.yearElseRate = yearElseRate;
	}

	public Integer getProjectCountOverdue() {
		return projectCountOverdue;
	}

	public void setProjectCountOverdue(Integer projectCountOverdue) {
		this.projectCountOverdue = projectCountOverdue;
	}

	public BigDecimal getOverduePrincipal() {
		return overduePrincipal;
	}

	public void setOverduePrincipal(BigDecimal overduePrincipal) {
		this.overduePrincipal = overduePrincipal;
	}

	public BigDecimal getProjectCountOverdueRate() {
		return projectCountOverdueRate;
	}

	public void setProjectCountOverdueRate(BigDecimal projectCountOverdueRate) {
		this.projectCountOverdueRate = projectCountOverdueRate;
	}

	public BigDecimal getOverduePrincipalRate() {
		return overduePrincipalRate;
	}

	public void setOverduePrincipalRate(BigDecimal overduePrincipalRate) {
		this.overduePrincipalRate = overduePrincipalRate;
	}

	public BigDecimal getPayRate() {
		return payRate;
	}

	public void setPayRate(BigDecimal payRate) {
		this.payRate = payRate;
	}

	
}