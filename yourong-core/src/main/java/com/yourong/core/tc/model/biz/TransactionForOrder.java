package com.yourong.core.tc.model.biz;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;

public class TransactionForOrder implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4878830994444461328L;

	/** 订单号 **/
	private Long id;

	/** 订单id **/
	private Long orderId;

	/** 用户ID **/
	private Long memberId;
	
	/** 真实姓名 **/
	private String trueName;

	/** 用户名 **/
	private String username;

	/** 手机号 **/
	private Long mobile;
	
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
	
	/** 租赁分行增加的年化收益 **/
	private BigDecimal bonusAnnualizedRate;

	/** 收益总天数 **/
	private Integer totalDays;

	/** 分期期数 **/
	private Integer installmentNum;
	
	/** 已换期数 **/
	private Integer payedNum;

	/** 总利息收益 **/
	private BigDecimal totalInterest;

	/** 已收取利息 **/
	private BigDecimal receivedInterest;

	/** 总本金 **/
	private BigDecimal totalPrincipal;

	/** 已收取本金 **/
	private BigDecimal receivedPrincipal;
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
	
	/**代付交易号**/
    private String payTradeNo;
    
    /**代收交易号**/
    private String collectTradeNo;
    
    /** 订单号 **/
    private String orderNo;
    
    /** 支付金额 **/
	private BigDecimal payAmount;
	
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

	public String getTrueName() {
		return trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getMobile() {
		return mobile;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public BigDecimal getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}

	public Integer getPayedNum() {
		return payedNum;
	}
	
	public void setPayedNum(Integer payedNum) {
		this.payedNum = payedNum;
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



}