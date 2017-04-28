package com.yourong.core.tc.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.StringUtil;

public class Transaction implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2784523806362490740L;

	/** 订单号 **/
	private Long id;

	/** 订单id **/
	private Long orderId;

	/** 用户ID **/
	private Long memberId;

	/** 项目ID **/
	private Long projectId;

	/** 项目名称 **/
	private String projectName;

	/** 投资金额 **/
	private BigDecimal investAmount;

	/** 使用账户资金 **/
	private BigDecimal usedCapital;

	/** 使用投资券金额 **/
	private BigDecimal usedCouponAmount;

	/** 年化收益 **/
	private BigDecimal annualizedRate;

	/** 额外年化收益 **/
	private BigDecimal extraAnnualizedRate;
	
	/**项目加息的年化**/
    private BigDecimal extraProjectAnnualizedRate;
    
    /**项目加息的年化收益**/
    private BigDecimal totalExtraProjectInterest;
    
    /**已收取的项目加息增加的年化收益**/
    private BigDecimal receivedExtraProjectInterest;
	
	/** 租赁分行增加的年化收益 **/
	private BigDecimal bonusAnnualizedRate;

	/** 收益总天数 **/
	private Integer totalDays;

	/** 分期期数 **/
	private Integer installmentNum;

	/** 总利息收益 **/
	private BigDecimal totalInterest;

	/** 已收取利息 **/
	private BigDecimal receivedInterest;

	/** 总本金 **/
	private BigDecimal totalPrincipal;

	/** 已收取本金 **/
	private BigDecimal receivedPrincipal;
	
	/**收益券增加的年化收益**/
	private BigDecimal totalExtraInterest;
	
	/***已收取的收益券收益**/
	private BigDecimal receivedExtraInterest;
	/** 滞纳金 **/
	private BigDecimal overdueFine;
	
	/** 租赁分红金额 **/
	private BigDecimal leaseBonusAmounts;

	/** 投资状态（1-回款中 2-已完结） **/
	private Integer status;

	private String remarks;

	/** 交易时间 **/
	private Date transactionTime;

	/** 创建时间 **/
	private Date createdTime;

	/****/
	private Date updateTime;
	
	/**放款状态（0-未放款 1-放款中 2-已放款）**/
	private Integer loanStatus;
	
	private Integer transferStatus;
	
	/**代付交易号**/
    private String payTradeNo;
    
    /**代收交易号**/
    private String collectTradeNo;
    
    /**签署状态（0初始化，1否，2是，3已过期）**/
	private Integer signStatus;
    
	
	/**投标标识（0-手动投资（默认null也为手动投资）；1-自动投资）**/
	private Integer investFlag;

	/** 项目类型 **/
	private Integer projectCategory;

	/**
	 * 转让项目id
	 */
	private Long transferId;

	/**
	 * 转让本金
	 */
	private BigDecimal transferPrincipal;
	
	/**
	 * 特殊收益天数
	 */
	private Integer extraInterestDay;
	
	/**
	 * 项目加息截止日期
	 */
	private Date extraEndDay;

	public Date getExtraEndDay() {
		return extraEndDay;
	}

	public void setExtraEndDay(Date extraEndDay) {
		this.extraEndDay = extraEndDay;
	}

	public Integer getExtraInterestDay() {
		return extraInterestDay;
	}

	public void setExtraInterestDay(Integer extraInterestDay) {
		this.extraInterestDay = extraInterestDay;
	}

	/**
	 * 可转让的日期
	 */
	private Date canTransferDate;

	public Integer getInvestFlag() {
		return investFlag;
	}

	public void setInvestFlag(Integer investFlag) {
		this.investFlag = investFlag;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
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

	public BigDecimal getInvestAmount() {
		return investAmount;
	}

	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}

	public BigDecimal getUsedCapital() {
		return usedCapital;
	}

	public void setUsedCapital(BigDecimal usedCapital) {
		this.usedCapital = usedCapital;
	}

	public BigDecimal getUsedCouponAmount() {
		return usedCouponAmount;
	}

	public void setUsedCouponAmount(BigDecimal usedCouponAmount) {
		this.usedCouponAmount = usedCouponAmount;
	}

	public BigDecimal getAnnualizedRate() {
		return annualizedRate;
	}

	public void setAnnualizedRate(BigDecimal annualizedRate) {
		this.annualizedRate = annualizedRate;
	}

	public BigDecimal getExtraAnnualizedRate() {
		return extraAnnualizedRate;
	}

	public void setExtraAnnualizedRate(BigDecimal extraAnnualizedRate) {
		this.extraAnnualizedRate = extraAnnualizedRate;
	}

	
	public BigDecimal getBonusAnnualizedRate() {
		return bonusAnnualizedRate;
	}

	public void setBonusAnnualizedRate(BigDecimal bonusAnnualizedRate) {
		this.bonusAnnualizedRate = bonusAnnualizedRate;
	}

	public Integer getTotalDays() {
		return totalDays;
	}

	public void setTotalDays(Integer totalDays) {
		this.totalDays = totalDays;
	}

	public Integer getInstallmentNum() {
		return installmentNum;
	}

	public void setInstallmentNum(Integer installmentNum) {
		this.installmentNum = installmentNum;
	}

	public BigDecimal getTotalInterest() {
		return totalInterest;
	}

	public void setTotalInterest(BigDecimal totalInterest) {
		this.totalInterest = totalInterest;
	}

	public BigDecimal getReceivedInterest() {
		return receivedInterest;
	}

	public void setReceivedInterest(BigDecimal receivedInterest) {
		this.receivedInterest = receivedInterest;
	}

	public BigDecimal getTotalPrincipal() {
		return totalPrincipal;
	}

	public void setTotalPrincipal(BigDecimal totalPrincipal) {
		this.totalPrincipal = totalPrincipal;
	}

	public BigDecimal getReceivedPrincipal() {
		return receivedPrincipal;
	}

	public void setReceivedPrincipal(BigDecimal receivedPrincipal) {
		this.receivedPrincipal = receivedPrincipal;
	}
	
	

	public BigDecimal getTotalExtraInterest() {
		return totalExtraInterest;
	}

	public void setTotalExtraInterest(BigDecimal totalExtraInterest) {
		this.totalExtraInterest = totalExtraInterest;
	}

	public BigDecimal getReceivedExtraInterest() {
		return receivedExtraInterest;
	}

	public void setReceivedExtraInterest(BigDecimal receivedExtraInterest) {
		this.receivedExtraInterest = receivedExtraInterest;
	}

	public BigDecimal getLeaseBonusAmounts() {
		return leaseBonusAmounts;
	}

	public void setLeaseBonusAmounts(BigDecimal leaseBonusAmounts) {
		this.leaseBonusAmounts = leaseBonusAmounts;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(Date transactionTime) {
		this.transactionTime = transactionTime;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	
	public Integer getLoanStatus() {
		return loanStatus;
	}

	public void setLoanStatus(Integer loanStatus) {
		this.loanStatus = loanStatus;
	}


	public String getPayTradeNo() {
		return payTradeNo;
	}

	public void setPayTradeNo(String payTradeNo) {
		this.payTradeNo = payTradeNo;
	}

	public String getCollectTradeNo() {
		return collectTradeNo;
	}

	public void setCollectTradeNo(String collectTradeNo) {
		this.collectTradeNo = collectTradeNo;
	}

	// 格式化交易时间
	public String getTransactionTimeStr() {
		return DateUtils.formatDatetoString(transactionTime,
				DateUtils.TIME_PATTERN);
	}

	// 项目年化收益
	public BigDecimal getTotalAnnualizedRate(){
		if(extraAnnualizedRate!=null){
			return annualizedRate.add(extraAnnualizedRate).setScale(2,BigDecimal.ROUND_HALF_UP);
		}else{
			return annualizedRate;
		}
	}
	
	//格式化预期收益
	public String getTotalInterestStr(){
		return FormulaUtil.formatCurrencyNoUnit(totalInterest);
	}
	
	//格式化投资本金
	public String getInvestAmountStr(){
		return FormulaUtil.formatCurrencyNoUnit(investAmount);
	}
	
	public String getMarkProjectName(){
		if(StringUtil.isBlank(projectName)){
			return "";
		}
		return projectName.substring(0, projectName.indexOf("期")+1);
	}

	public BigDecimal getOverdueFine() {
		return overdueFine;
	}

	public void setOverdueFine(BigDecimal overdueFine) {
		this.overdueFine = overdueFine;
	}

	/**
	 * @return the signStatus
	 */
	public Integer getSignStatus() {
		return signStatus;
	}

	/**
	 * @param signStatus the signStatus to set
	 */
	public void setSignStatus(Integer signStatus) {
		this.signStatus = signStatus;
	}

	public Integer getProjectCategory() {
		return projectCategory;
	}

	public void setProjectCategory(Integer projectCategory) {
		this.projectCategory = projectCategory;
	}

	public Long getTransferId() {
		return transferId;
	}

	public void setTransferId(Long transferId) {
		this.transferId = transferId;
	}

	public BigDecimal getTransferPrincipal() {
		return transferPrincipal;
	}

	public void setTransferPrincipal(BigDecimal transferPrincipal) {
		this.transferPrincipal = transferPrincipal;
	}

	public BigDecimal getExtraProjectAnnualizedRate() {
		return extraProjectAnnualizedRate;
	}

	public void setExtraProjectAnnualizedRate(BigDecimal extraProjectAnnualizedRate) {
		this.extraProjectAnnualizedRate = extraProjectAnnualizedRate;
	}

	public BigDecimal getReceivedExtraProjectInterest() {
		return receivedExtraProjectInterest;
	}

	public void setReceivedExtraProjectInterest(
			BigDecimal receivedExtraProjectInterest) {
		this.receivedExtraProjectInterest = receivedExtraProjectInterest;
	}

	public BigDecimal getTotalExtraProjectInterest() {
		return totalExtraProjectInterest;
	}

	public void setTotalExtraProjectInterest(BigDecimal totalExtraProjectInterest) {
		this.totalExtraProjectInterest = totalExtraProjectInterest;
	}

	public Date getCanTransferDate() {
		return canTransferDate;
	}

	public void setCanTransferDate(Date canTransferDate) {
		this.canTransferDate = canTransferDate;
	}

	/**
	 * @return the transferStatus
	 */
	public Integer getTransferStatus() {
		return transferStatus;
	}

	/**
	 * @param transferStatus the transferStatus to set
	 */
	public void setTransferStatus(Integer transferStatus) {
		this.transferStatus = transferStatus;
	}

}