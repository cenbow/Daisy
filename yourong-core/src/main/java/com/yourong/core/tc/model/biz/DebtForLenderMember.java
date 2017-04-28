/**
 * 
 */
package com.yourong.core.tc.model.biz;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.util.DateUtils;
import com.yourong.common.util.StringUtil;
import com.yourong.common.util.FormulaUtil;
import com.yourong.core.tc.model.TransactionInterest;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年3月23日下午2:22:43
 */
public class DebtForLenderMember implements Serializable{
	
	/**	出借人债权管理，前台显示 **/
	
	/** 项目ID **/
	private Long projectId;
	
	/** 项目名称 **/
	private String projectName;

	/** 项目到期日 **/
	private Date endDate;
	
	/** 借款人姓名 **/
	private String borrowerName;
	
	/** 总期数**/
	private Integer totalPeriods;

	/**当前期数 **/
	private Integer currentPeriods;
	
	/**本金 **/
	private BigDecimal payablePrincipal = BigDecimal.ZERO;
	
	/**利息**/
	private BigDecimal payableInterest = BigDecimal.ZERO;
	
	/**利息+本金 **/
	private String curAmount ; 
	
	/**还本付息期数信息**/
	private List<TransactionInterest> transactionInterests;
	
	/**投资总金额**/
    private BigDecimal totalAmount;
    
    /**项目数量**/
    private Integer projectNum;

    /**本金之和 **/
	private BigDecimal totalPrincipal = BigDecimal.ZERO;
	
	/**利息之和**/
	private BigDecimal totalInterest = BigDecimal.ZERO;
	
	/**利息之和+本金之和 **/
	private BigDecimal sum;
	
	/**剩余天数**/
	private Integer  restDays;
	
	/** 0 - 待支付，1-已支付 ，2 - 部分支付**/
	private Integer status;
	
	
	/**
	 * @return the projectId
	 */
	public Long getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}
	
	
	/**
	 * @return the endDate
	 */
	public String getStrEndDate() {
		String date = DateUtils.formatDatetoString(endDate, DateUtils.DATE_FMT_3);
		return date;
		//return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the borrowerName
	 */
	public String getBorrowerName() {
		return borrowerName;
	}

	/**
	 * @param borrowerName the borrowerName to set
	 */
	public void setBorrowerName(String borrowerName) {
		this.borrowerName = borrowerName;
	}

	/**
	 * @return the totalPeriods
	 */
	public Integer getTotalPeriods() {
		return totalPeriods;
	}

	/**
	 * @param totalPeriods the totalPeriods to set
	 */
	public void setTotalPeriods(Integer totalPeriods) {
		this.totalPeriods = totalPeriods;
	}

	/**
	 * @return the currentPeriods
	 */
	public Integer getCurrentPeriods() {
		return currentPeriods;
	}

	/**
	 * @param currentPeriods the currentPeriods to set
	 */
	public void setCurrentPeriods(Integer currentPeriods) {
		this.currentPeriods = currentPeriods;
	}

	/**
	 * @return the payablePrincipal
	 */
	public BigDecimal getPayablePrincipal() {
		return payablePrincipal;
	}

	/**
	 * @param payablePrincipal the payablePrincipal to set
	 */
	public void setPayablePrincipal(BigDecimal payablePrincipal) {
		this.payablePrincipal = payablePrincipal;
	}

	/**
	 * @return the payableInterest
	 */
	public BigDecimal getPayableInterest() {
		return payableInterest;
	}

	/**
	 * @param payableInterest the payableInterest to set
	 */
	public void setPayableInterest(BigDecimal payableInterest) {
		this.payableInterest = payableInterest;
	}

	/**
	 * @return the transactionInterests
	 */
	public List<TransactionInterest> getTransactionInterests() {
		return transactionInterests;
	}

	/**
	 * @param transactionInterests the transactionInterests to set
	 */
	public void setTransactionInterests(
			List<TransactionInterest> transactionInterests) {
		this.transactionInterests = transactionInterests;
	}

	/**
	 * @return the totalAmount
	 */
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	/**
	 * @param totalAmount the totalAmount to set
	 */
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	/**
	 * @return the projectNum
	 */
	public Integer getProjectNum() {
		return projectNum;
	}

	/**
	 * @param projectNum the projectNum to set
	 */
	public void setProjectNum(Integer projectNum) {
		this.projectNum = projectNum;
	}

	/**
	 * @return the totalPrincipal
	 */
	public BigDecimal getTotalPrincipal() {
		return totalPrincipal;
	}

	/**
	 * @param totalPrincipal the totalPrincipal to set
	 */
	public void setTotalPrincipal(BigDecimal totalPrincipal) {
		this.totalPrincipal = totalPrincipal;
	}

	/**
	 * @return the totalInterest
	 */
	public BigDecimal getTotalInterest() {
		return totalInterest;
	}

	/**
	 * @param totalInterest the totalInterest to set
	 */
	public void setTotalInterest(BigDecimal totalInterest) {
		this.totalInterest = totalInterest;
	}
	
	public String getMarkProjectName(){
		if(StringUtil.isBlank(projectName)){
			return "";
		}
		return projectName.substring(0, projectName.indexOf("期")+1);
	}
	
	public String getRestMarkProjectName(){
		if(StringUtil.isBlank(projectName)){
			return "";
		}
		return projectName.substring(projectName.indexOf("期")+1,projectName.length());
	}
	
	
	/**
	 * @param restDays the restDays to set
	 */
	public void setRestDays(Integer restDays) {
		this.restDays = restDays;
	}

	/**
	 * 剩余天数
	 * 
	 * @return
	 */
	public Integer getRestDays() {
		int day = 0;
		day = DateUtils.getIntervalDays(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3),endDate)+1;
		return day > 0 ? day : 0;
	}
	

	
	/**
	 * @return the curAmount
	 */
	public String getCurAmount() {
		return curAmount;
	}

	/**
	 * @param curAmount the curAmount to set
	 */
	public void setCurAmount(String curAmount) {
		this.curAmount = curAmount;
	}

	/**
	 * 本息文本标志位
	 * 
	 * @return
	 */
	public int getPrincipalInterestFlag() {
		if(payablePrincipal==null||payablePrincipal.compareTo(BigDecimal.ZERO)==0){//本金为零表示还款为利息
			return 1;
		}
		if(payableInterest==null||payableInterest.compareTo(BigDecimal.ZERO)==0){//利息为零表示还款为本金
			return 2;
		}
		return 3;//都不为零表示还款为本息
	}

	/**
	 * @return the sum
	 */
	public String getSum() {
		if(totalPrincipal==null||totalPrincipal.compareTo(BigDecimal.ZERO)==0){//本金为零
			return FormulaUtil.formatCurrencyNoUnit(totalInterest);
		}
		if(totalInterest==null||totalInterest.compareTo(BigDecimal.ZERO)==0){//利息为零
			return FormulaUtil.formatCurrencyNoUnit(totalPrincipal);
		}
		return FormulaUtil.formatCurrencyNoUnit(totalInterest.add(totalPrincipal));
	}

	/**
	 * @param sum the sum to set
	 */
	public void setSum(BigDecimal sum) {
		this.sum = sum;
	}

	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
