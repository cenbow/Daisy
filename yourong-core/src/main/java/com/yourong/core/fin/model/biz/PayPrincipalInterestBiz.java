package com.yourong.core.fin.model.biz;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.core.uc.model.Member;

/**
 * 托管还本付息
 * 
 * @author fuyili 2014年12月24日上午9:41:12
 */
public class PayPrincipalInterestBiz {

	/*项目编号*/
	private Long projectId;
	
	/*项目名称*/
	private String projectName;
	
	 /**项目类型code**/
    private String projectType;
	
	/*债权编号*/
	private Long debtId;
	
	/*项目上线时间*/
	private Date onlineTime;
	
	/*项目每期结束时间*/
	private Date endDate;

	/* 出借人信息（出借人手机、出借人姓名） */
	private Member lenderMember;
	
	/* 借款人信息 */
	private Member borrowerMember;

	/* 总期数 */
	private Integer totalPeriods;

	/* 当前期数 */
	private Integer currentPeriods;

	/* 当期还款到期日 */
	private Date currentDeadline;

	/* 距离到期日 */
	private Integer expireDays;
	
	/*距离到期日时间*/
	private Integer expireHours;

	/* 支付本金 */
	private BigDecimal payablePrincipal;
	
	/*总支付利息*/
	private BigDecimal payableInterest;

	/* 平台贴息 */
	private BigDecimal extraInterest;

	/* 还款状态 */
	private Integer status;
	
	/*垫资第三方*/
	private String thirdPayName;
	
	/*项目本息id*/
	private Long interestId;
	
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

	public Long getDebtId() {
		return debtId;
	}

	public void setDebtId(Long debtId) {
		this.debtId = debtId;
	}

	public Date getOnlineTime() {
		return onlineTime;
	}

	public void setOnlineTime(Date onlineTime) {
		this.onlineTime = onlineTime;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Member getLenderMember() {
		return lenderMember;
	}

	public void setLenderMember(Member lenderMember) {
		this.lenderMember = lenderMember;
	}

	public Member getBorrowerMember() {
		return borrowerMember;
	}

	public void setBorrowerMember(Member borrowerMember) {
		this.borrowerMember = borrowerMember;
	}

	public Integer getTotalPeriods() {
		return totalPeriods;
	}

	public void setTotalPeriods(Integer totalPeriods) {
		this.totalPeriods = totalPeriods;
	}

	public Date getCurrentDeadline() {
		return currentDeadline;
	}

	public void setCurrentDeadline(Date currentDeadline) {
		this.currentDeadline = currentDeadline;
	}

	public Integer getExpireDays() {
		return expireDays;
	}

	public void setExpireDays(Integer expireDays) {
		this.expireDays = expireDays;
	}

	public BigDecimal getPayablePrincipal() {
		return payablePrincipal;
	}

	public void setPayablePrincipal(BigDecimal payablePrincipal) {
		this.payablePrincipal = payablePrincipal;
	}

	public BigDecimal getPayableInterest() {
		return payableInterest;
	}

	public void setPayableInterest(BigDecimal payableInterest) {
		this.payableInterest = payableInterest;
	}

	public BigDecimal getExtraInterest() {
		return extraInterest;
	}

	public void setExtraInterest(BigDecimal extraInterest) {
		this.extraInterest = extraInterest;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	/*支付本金*/
	public String getPayablePrincipalStr() {
		return FormulaUtil.formatCurrencyNoUnit(payablePrincipal);
	}
	/*总支付利息*/
	public String getPayableInterestStr() {
		return FormulaUtil.formatCurrencyNoUnit(payableInterest);
	}
	
	/*平台支付利息*/
	public String getExtraInterestStr() {
		return FormulaUtil.formatCurrencyNoUnit(extraInterest);
	}
	
	/*出借人支付利息*/
	public String getUserInterestStr() {
		BigDecimal userInterest = payableInterest.subtract(extraInterest);
		return FormulaUtil.formatCurrencyNoUnit(userInterest);
	}

	public Integer getCurrentPeriods() {
		return currentPeriods;
	}

	public void setCurrentPeriods(Integer currentPeriods) {
		this.currentPeriods = currentPeriods;
	}

	public Integer getExpireHours() {
		return expireHours;
	}

	public void setExpireHours(Integer expireHours) {
		this.expireHours = expireHours;
	}
	
	

	/**
	 * @return the projectType
	 */
	public String getProjectType() {
		return projectType;
	}

	/**
	 * @param projectType the projectType to set
	 */
	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	public String getEndDateStr(){
		if(endDate!=null){
			return DateUtils.formatDatetoString(endDate, DateUtils.DATE_FMT_3);
		}
		return null;
	}

	public String getThirdPayName() {
		return thirdPayName;
	}

	public void setThirdPayName(String thirdPayName) {
		this.thirdPayName = thirdPayName;
	}
	
	
	/*出借人总支付金额*/
	public BigDecimal getUserTotalPayAmount() {
		BigDecimal userTotalPayAmount = payablePrincipal.add(payableInterest).subtract(extraInterest);
		return userTotalPayAmount;
	}
	
	/*出借人支付利息*/
	public BigDecimal getUserInterest() {
		return payableInterest.subtract(extraInterest);
	}

	public Long getInterestId() {
		return interestId;
	}

	public void setInterestId(Long interestId) {
		this.interestId = interestId;
	}

}
