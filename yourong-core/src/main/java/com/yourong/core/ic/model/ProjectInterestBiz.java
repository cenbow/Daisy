package com.yourong.core.ic.model;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
/**
 * 
 * @desc 直投项目债权本息表
 * @author chaisen
 * 2016年1月6日上午9:38:13
 */
public class ProjectInterestBiz extends AbstractBaseObject {
   
	private static final long serialVersionUID = 6185227493502905513L;
	
	private Long id;
	
	/**本息id**/
	private Long interestId;
	/** 项目编号 */
	private String originalProjectNumber;
	
	/*项目主键*/
	private Long projectId;

	/** 项目名称 */
	private String projectName;
	
	 /**项目类型code**/
    private String projectType;
    
	/** 项目总额 */
	private BigDecimal totalAmount;
	
	/**年化利率**/
    private BigDecimal annualizedRate;
	
	/*项目上线时间*/
	private Date onlineTime;
	
	 /**借款人姓名**/
    private String borrowerName;
    
    /*借款人手机号*/
    private Long mobile;

    /** 总期数 */
	private Integer totalPeriods;
	
	/**管理费利率**/
    private BigDecimal manageFeeRate;
    
	/* 当前期数 */
	private Integer currentPeriods;

	/* 当期还款到期日 */
	private Date currentDeadline;

	/* 距离到期日 */
	private Integer expireDays;
	
	/*距离到期日时间*/
	private Integer expireHours;
	
	/*项目每期结束时间*/
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date endDate;
	
	/* 支付本金 */
	private BigDecimal payablePrincipal;
	
	/*总支付利息*/
	private BigDecimal payableInterest;

	/* 平台贴息 */
	private BigDecimal extraInterest;

	/*实际平台贴息*/
	private BigDecimal realExtraInterest;
	
	/*借款人支付投资人利息*/
	private BigDecimal borrowPayableInterest;
	
	/*实际支付利息*/
	private BigDecimal realInterest;
	
	  /**实付本金**/
    private BigDecimal realPayPrincipal;

    /**实付利息**/
    private BigDecimal realPayInterest;
	
	/* 项目状态 */
	private Integer projectStatus;
	
	/* 还款状态 */
	private Integer interestStatus;
	
	/* 逾期还款状态 */
	private Integer overdueStatus;
	
	/*垫付状态*/
	private Integer underwriteState;
	
	/*交易本息状态*/
	private Integer status;
	
	/*垫资第三方*/
	private String thirdPayName;

	/*逾期天数*/
	private Integer overdueDays;
    
	/*滞纳金*/
	private BigDecimal lateFees;
	
	/*备注*/
	private String remarks;

	/*借款人id*/
	private Long borrowerId;
	
	private String periods;
	
	/**本金+利息**/
	private BigDecimal totalPayAmount;
	
	/**第三方账户余额**/
	private BigDecimal thirdAmount;
	
	/**第三方垫资会员id**/
	private Long thirdMemberId;
	
	/**垫付时间**/
	private Date repayTime;
	
	 /**逾期还款方式，1线上，2线下**/
    private Integer refundType;
    
    /**逾期本金**/
    private BigDecimal overduePrincipal;
    
    /**逾期利息**/
    private BigDecimal overdueInterest;
    
    /**违约金**/
    private BigDecimal breachAmount;
    
    /**应付金额= 逾期金额+逾期利息+滞纳金+（违约金，只有线下有违约金）**/
    private BigDecimal payableAmount;

    /**实际付金额**/
    private BigDecimal realPayAmount;
    
    /**还款时间**/
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date refundTime;
	
    /**逾期最终状态**/
    private Integer allStatus;
    
    /**垫付记录ID**/
    private Long underwriteId;
    
    /**逾期记录id**/
	private Long overdueId;
    
    private String dataTime;

    /**垫付发起的时间**/
    private Date underwriteTime;
    
    /**担保方式（pledge-质押；collateral-抵押；credit-信用）**/
    private String securityType;
    
    /**借款周期**/
    private Integer borrowPeriod;
    
    /**借款周期类型（1-日；2-月；3-年）**/
    private Integer borrowPeriodType;
    
    
    /**管理费**/
    private BigDecimal managementFee;

    /**逾期还款状态[1:未收取，2：收取中，3 已收取]**/
    private Integer managermentState;

    /**管理收取时间**/
    private Date gatherTime;
    
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    /**销售截止日期(年月日时分秒)**/
    private Date saleEndTime;
    
    /**收益类型**/
    private String profitType;
    
    /** 逾期金额**/
    private BigDecimal overdueAmount;
    
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date startDate;
    
    /***逾期罚息**/
    private BigDecimal overdueFeeRate;
    
    /**逾期记录数**/
    private Integer overdueRecord;
    
    
    /**z借款人存钱罐账号**/
    private String sinaAccount;
    
    /**可放款金额**/
    private BigDecimal loanAmount;
    /**
	 * 项目投资进度
	 */
	private String progress;
	
	/**逾期记录ID**/
    private String overdueIds;
	
    /**未还本金**/
    private BigDecimal unreturnPrincipal;
    
    /**代还本金+利息**/
    private BigDecimal waitTotalPayAmount;
    
    
    /**项目销售完成时间*/
    private Date saleComplatedTime;
    
    /**
     * 还款类型  1 本金+利息  2 利息
     */
    private Integer payType;
    
    /**
     * 操作id
     */
    private Long operateId;
    
    /**还款状态**/
    private Integer repayStatus;
    
    /**逾期类型**/
    private Integer type;
    
    /**期数**/
    private String interestPeriods;
    
    private BigDecimal overdueFine;
    /**未还利息**/
    private BigDecimal noPayInterest;
    /**未还本金+利息**/
    private BigDecimal noPayPrincipalInterest;
    
    /**逾期开始时间**/
    private Date overdueStartDate;
    
    /**逾期结算id**/
    private Long overdueRepayId;
    
    private Integer overdueUnder;
    
    private Integer collect;
    
    /**保证金费率**/
    private BigDecimal guaranteeFeeRate;
    
    /**风险金费率**/
    private BigDecimal riskFeeRate;
    
    /**介绍费率**/
    private BigDecimal introducerFeeRate;
    
    private Integer commonRecord;
    
    /**快投奖励金额**/
    private  BigDecimal quickReward;
    
    private boolean canelUnderWrite=false;
    
    
    
    /**
	 * @return the canelUnderWrite
	 */
	public boolean isCanelUnderWrite() {
		return canelUnderWrite;
	}

	/**
	 * @param canelUnderWrite the canelUnderWrite to set
	 */
	public void setCanelUnderWrite(boolean canelUnderWrite) {
		this.canelUnderWrite = canelUnderWrite;
	}

	public BigDecimal getQuickReward() {
		return quickReward;
	}

	public void setQuickReward(BigDecimal quickReward) {
		this.quickReward = quickReward;
	}

	public Integer getCommonRecord() {
		return commonRecord;
	}

	public void setCommonRecord(Integer commonRecord) {
		this.commonRecord = commonRecord;
	}

	public Date getRepayDate() {
		return repayDate;
	}

	public Integer getOverdueUnder() {
		return overdueUnder;
	}

	public void setOverdueUnder(Integer overdueUnder) {
		this.overdueUnder = overdueUnder;
	}

	public Integer getCollect() {
		return collect;
	}

	public void setCollect(Integer collect) {
		this.collect = collect;
	}

	public BigDecimal getGuaranteeFeeRate() {
		return guaranteeFeeRate;
	}

	public void setGuaranteeFeeRate(BigDecimal guaranteeFeeRate) {
		this.guaranteeFeeRate = guaranteeFeeRate;
	}

	public BigDecimal getRiskFeeRate() {
		return riskFeeRate;
	}

	public void setRiskFeeRate(BigDecimal riskFeeRate) {
		this.riskFeeRate = riskFeeRate;
	}

	public BigDecimal getIntroducerFeeRate() {
		return introducerFeeRate;
	}

	public void setIntroducerFeeRate(BigDecimal introducerFeeRate) {
		this.introducerFeeRate = introducerFeeRate;
	}

	public void setRepayDate(Date repayDate) {
		this.repayDate = repayDate;
	}
	/**还款时间**/
    private Date repayDate;
    
	public BigDecimal getNoPayPrincipalInterest() {
		return noPayPrincipalInterest;
	}

	public void setNoPayPrincipalInterest(BigDecimal noPayPrincipalInterest) {
		this.noPayPrincipalInterest = noPayPrincipalInterest;
	}

	public BigDecimal getRealPayPrincipal() {
		return realPayPrincipal;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public BigDecimal getOverdueFine() {
		return overdueFine;
	}

	public void setOverdueFine(BigDecimal overdueFine) {
		this.overdueFine = overdueFine;
	}

	public void setRealPayPrincipal(BigDecimal realPayPrincipal) {
		this.realPayPrincipal = realPayPrincipal;
	}

	public BigDecimal getRealPayInterest() {
		return realPayInterest;
	}

	public BigDecimal getNoPayInterest() {
		return noPayInterest;
	}

	public void setNoPayInterest(BigDecimal noPayInterest) {
		this.noPayInterest = noPayInterest;
	}

	public void setRealPayInterest(BigDecimal realPayInterest) {
		this.realPayInterest = realPayInterest;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public String getInterestPeriods() {
		return interestPeriods;
	}

	public void setInterestPeriods(String interestPeriods) {
		this.interestPeriods = interestPeriods;
	}

	public String getOverdueIds() {
		return overdueIds;
	}

	public BigDecimal getWaitTotalPayAmount() {
		return waitTotalPayAmount;
	}

	public Long getOperateId() {
		return operateId;
	}

	public void setOperateId(Long operateId) {
		this.operateId = operateId;
	}

	public void setWaitTotalPayAmount(BigDecimal waitTotalPayAmount) {
		this.waitTotalPayAmount = waitTotalPayAmount;
	}

	public Date getSaleComplatedTime() {
		return saleComplatedTime;
	}

	public void setSaleComplatedTime(Date saleComplatedTime) {
		this.saleComplatedTime = saleComplatedTime;
	}

	public void setOverdueIds(String overdueIds) {
		this.overdueIds = overdueIds;
	}

	public String getProgress() {
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public BigDecimal getRealInterest() {
		return realInterest;
	}

	public void setRealInterest(BigDecimal realInterest) {
		this.realInterest = realInterest;
	}

	public BigDecimal getManageFeeRate() {
		return manageFeeRate;
	}

	public void setManageFeeRate(BigDecimal manageFeeRate) {
		this.manageFeeRate = manageFeeRate;
	}

	public BigDecimal getOverdueFeeRate() {
		return overdueFeeRate;
	}

	public String getProjectType() {
		return projectType;
	}

	public Integer getRepayStatus() {
		return repayStatus;
	}

	public Date getOverdueStartDate() {
		return overdueStartDate;
	}

	public void setOverdueStartDate(Date overdueStartDate) {
		this.overdueStartDate = overdueStartDate;
	}

	public void setRepayStatus(Integer repayStatus) {
		this.repayStatus = repayStatus;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	public void setOverdueFeeRate(BigDecimal overdueFeeRate) {
		this.overdueFeeRate = overdueFeeRate;
	}

	public BigDecimal getOverdueAmount() {
		return overdueAmount;
	}

	public void setOverdueAmount(BigDecimal overdueAmount) {
		this.overdueAmount = overdueAmount;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public BigDecimal getManagementFee() {
		return managementFee;
	}

	public void setManagementFee(BigDecimal managementFee) {
		this.managementFee = managementFee;
	}

	public BigDecimal getUnreturnPrincipal() {
		return unreturnPrincipal;
	}

	public void setUnreturnPrincipal(BigDecimal unreturnPrincipal) {
		this.unreturnPrincipal = unreturnPrincipal;
	}

	public Integer getManagermentState() {
		return managermentState;
	}

	public void setManagermentState(Integer managermentState) {
		this.managermentState = managermentState;
	}

	public Date getGatherTime() {
		return gatherTime;
	}

	public void setGatherTime(Date gatherTime) {
		this.gatherTime = gatherTime;
	}

	public String getDataTime() {
		return dataTime;
	}

	public void setDataTime(String dataTime) {
		this.dataTime = dataTime;
	}


	public String getPeriods() {
		return periods;
	}

	public void setPeriods(String periods) {
		this.periods = periods;
	}

	public String getOriginalProjectNumber() {
		return originalProjectNumber;
	}

	public void setOriginalProjectNumber(String originalProjectNumber) {
		this.originalProjectNumber = originalProjectNumber;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Date getOnlineTime() {
		return onlineTime;
	}

	public void setOnlineTime(Date onlineTime) {
		this.onlineTime = onlineTime;
	}

	public String getBorrowerName() {
		return borrowerName;
	}

	public void setBorrowerName(String borrowerName) {
		this.borrowerName = borrowerName;
	}

	public Long getMobile() {
		return mobile;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	public Integer getTotalPeriods() {
		return totalPeriods;
	}

	public void setTotalPeriods(Integer totalPeriods) {
		this.totalPeriods = totalPeriods;
	}

	public Integer getCurrentPeriods() {
		return currentPeriods;
	}

	public void setCurrentPeriods(Integer currentPeriods) {
		this.currentPeriods = currentPeriods;
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

	public BigDecimal getRealExtraInterest() {
		return realExtraInterest;
	}

	public void setRealExtraInterest(BigDecimal realExtraInterest) {
		this.realExtraInterest = realExtraInterest;
	}

	public BigDecimal getBorrowPayableInterest() {
		return borrowPayableInterest;
	}

	public void setBorrowPayableInterest(BigDecimal borrowPayableInterest) {
		this.borrowPayableInterest = borrowPayableInterest;
	}

	/*实际借款人支付利息*/
	public String getBorrowPayableInterestStr() {
		return FormulaUtil.formatCurrencyNoUnit(borrowPayableInterest);
	}

	public String getSinaAccount() {
		return sinaAccount;
	}

	public void setSinaAccount(String sinaAccount) {
		this.sinaAccount = sinaAccount;
	}

	public BigDecimal getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(BigDecimal loanAmount) {
		this.loanAmount = loanAmount;
	}

	public Integer getOverdueStatus() {
		return overdueStatus;
	}

	public void setOverdueStatus(Integer overdueStatus) {
		this.overdueStatus = overdueStatus;
	}

	public String getThirdPayName() {
		return thirdPayName;
	}

	public void setThirdPayName(String thirdPayName) {
		this.thirdPayName = thirdPayName;
	}

	public Integer getOverdueDays() {
		return overdueDays;
	}

	public void setOverdueDays(Integer overdueDays) {
		this.overdueDays = overdueDays;
	}

	public BigDecimal getLateFees() {
		return lateFees;
	}

	public void setLateFees(BigDecimal lateFees) {
		this.lateFees = lateFees;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getProjectStatus() {
		return projectStatus;
	}

	public void setProjectStatus(Integer projectStatus) {
		this.projectStatus = projectStatus;
	}

	public Integer getInterestStatus() {
		return interestStatus;
	}

	public void setInterestStatus(Integer interestStatus) {
		this.interestStatus = interestStatus;
	}

	public Long getBorrowerId() {
		return borrowerId;
	}

	public void setBorrowerId(Long borrowerId) {
		this.borrowerId = borrowerId;
	}

	public Long getInterestId() {
		return interestId;
	}

	public void setInterestId(Long interestId) {
		this.interestId = interestId;
	}

	public BigDecimal getTotalPayAmount() {
		return totalPayAmount;
	}

	public void setTotalPayAmount(BigDecimal totalPayAmount) {
		this.totalPayAmount = totalPayAmount;
	}

	public BigDecimal getThirdAmount() {
		return thirdAmount;
	}

	public void setThirdAmount(BigDecimal thirdAmount) {
		this.thirdAmount = thirdAmount;
	}

	public Long getThirdMemberId() {
		return thirdMemberId;
	}

	public void setThirdMemberId(Long thirdMemberId) {
		this.thirdMemberId = thirdMemberId;
	}

	public Date getRepayTime() {
		return repayTime;
	}

	public void setRepayTime(Date repayTime) {
		this.repayTime = repayTime;
	}

	public Integer getRefundType() {
		return refundType;
	}

	public void setRefundType(Integer refundType) {
		this.refundType = refundType;
	}

	public BigDecimal getOverduePrincipal() {
		return overduePrincipal;
	}

	public void setOverduePrincipal(BigDecimal overduePrincipal) {
		this.overduePrincipal = overduePrincipal;
	}

	public BigDecimal getOverdueInterest() {
		return overdueInterest;
	}

	public void setOverdueInterest(BigDecimal overdueInterest) {
		this.overdueInterest = overdueInterest;
	}

	public BigDecimal getBreachAmount() {
		return breachAmount;
	}

	public void setBreachAmount(BigDecimal breachAmount) {
		this.breachAmount = breachAmount;
	}

	public BigDecimal getPayableAmount() {
		return payableAmount;
	}

	public void setPayableAmount(BigDecimal payableAmount) {
		this.payableAmount = payableAmount;
	}

	public BigDecimal getRealPayAmount() {
		return realPayAmount;
	}

	public void setRealPayAmount(BigDecimal realPayAmount) {
		this.realPayAmount = realPayAmount;
	}

	public Date getRefundTime() {
		return refundTime;
	}

	public void setRefundTime(Date refundTime) {
		this.refundTime = refundTime;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public Integer getExpireHours() {
		return expireHours;
	}

	public void setExpireHours(Integer expireHours) {
		this.expireHours = expireHours;
	}

	public Integer getUnderwriteState() {
		return underwriteState;
	}

	public void setUnderwriteState(Integer underwriteState) {
		this.underwriteState = underwriteState;
	}

	public Long getOverdueRepayId() {
		return overdueRepayId;
	}

	public void setOverdueRepayId(Long overdueRepayId) {
		this.overdueRepayId = overdueRepayId;
	}

	public Integer getAllStatus() {
		return allStatus;
	}

	public void setAllStatus(Integer allStatus) {
		this.allStatus = allStatus;
	}

	public Long getUnderwriteId() {
		return underwriteId;
	}

	public void setUnderwriteId(Long underwriteId) {
		this.underwriteId = underwriteId;
	}

	public Date getUnderwriteTime() {
		return underwriteTime;
	}

	public void setUnderwriteTime(Date underwriteTime) {
		this.underwriteTime = underwriteTime;
	}

	public String getSecurityType() {
		return securityType;
	}

	public void setSecurityType(String securityType) {
		this.securityType = securityType;
	}

	public Integer getBorrowPeriod() {
		return borrowPeriod;
	}

	public void setBorrowPeriod(Integer borrowPeriod) {
		this.borrowPeriod = borrowPeriod;
	}

	public Integer getBorrowPeriodType() {
		return borrowPeriodType;
	}

	public void setBorrowPeriodType(Integer borrowPeriodType) {
		this.borrowPeriodType = borrowPeriodType;
	}

	public BigDecimal getAnnualizedRate() {
		return annualizedRate;
	}

	public void setAnnualizedRate(BigDecimal annualizedRate) {
		this.annualizedRate = annualizedRate;
	}

	public Date getSaleEndTime() {
		return saleEndTime;
	}

	public void setSaleEndTime(Date saleEndTime) {
		this.saleEndTime = saleEndTime;
	}

	public String getProfitType() {
		return profitType;
	}

	public void setProfitType(String profitType) {
		this.profitType = profitType;
	}

	public Long getOverdueId() {
		return overdueId;
	}

	public void setOverdueId(Long overdueId) {
		this.overdueId = overdueId;
	}

	public Integer getOverdueRecord() {
		return overdueRecord;
	}

	public void setOverdueRecord(Integer overdueRecord) {
		this.overdueRecord = overdueRecord;
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
		BigDecimal userInterest=BigDecimal.ZERO;
		if(extraInterest==null){
			return "";
		}
		if(extraInterest.compareTo(BigDecimal.ZERO)>0){
			userInterest = payableInterest.subtract(extraInterest);
		}else if(extraInterest.compareTo(BigDecimal.ZERO)==0){
			userInterest=payableInterest;
		}
		return FormulaUtil.formatCurrencyNoUnit(userInterest);
	}
	
	
	public String getBorrowerInterestStr() {
		BigDecimal userInterest=BigDecimal.ZERO;
		if(realExtraInterest==null){
			return "";
		}
		if(realExtraInterest.compareTo(BigDecimal.ZERO)>0){
			userInterest = realPayInterest.subtract(realExtraInterest);
		}else if(realExtraInterest.compareTo(BigDecimal.ZERO)==0){
			userInterest=realPayInterest;
		}
		return FormulaUtil.formatCurrencyNoUnit(userInterest);
	}
	/**
	 * 
	 * @Description:web 借款类型
	 * @return
	 * @author: chaisen
	 * @time:2016年2月16日 下午1:22:20
	 */
	public String getSecurityTypeName(){
		if(securityType==null){
			return "";
		}
		if(securityType.equals("guarantee")){
			return "担保";
		}
		if(securityType.equals("credit")){
			return "信用";
		}
		return "";
	}
	
	/**
	 * 
	 * @Description:借款金额格式化
	 * @return
	 * @author: chaisen
	 * @time:2016年2月16日 下午1:22:34
	 */
	public String getFormatTotalAmount() {
		if(totalAmount == null || BigDecimal.ZERO.compareTo(totalAmount)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(totalAmount);
	}
	
	public String getPayableInterestFor(){
		if(payableInterest==null){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(payableInterest);
	}
	
	public String getPayablePrincipalFor(){
		if(payablePrincipal==null){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(payablePrincipal);
	}
	/**
	 * 
	 * @Description:逾期金额格式化
	 * @return
	 * @author: chaisen
	 * @time:2016年2月16日 下午1:22:34
	 */
	public String getFormatOverdueAmount() {
		if(overdueAmount == null || BigDecimal.ZERO.compareTo(overdueAmount)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(overdueAmount);
	}
	
	/**
	 * 
	 * @Description:滞纳金格式化
	 * @return
	 * @author: chaisen
	 * @time:2016年2月16日 下午1:22:34
	 */
	public String getFormatLateFees() {
		if(lateFees == null || BigDecimal.ZERO.compareTo(lateFees)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(lateFees);
	}
	/**
	 * 
	 * @Description:获取管理费
	 * @return
	 * @author: chaisen
	 * @time:2016年2月16日 下午1:23:47
	 */
	public String getFormatManageFee() {
		BigDecimal manageFee = BigDecimal.ZERO;
		if(totalAmount == null || BigDecimal.ZERO.compareTo(totalAmount)==0){
			return "￥0";
		}
		if(manageFeeRate == null || BigDecimal.ZERO.compareTo(manageFeeRate)==0){
			return "￥0";
		}
		manageFee=FormulaUtil.getManagerAmount(totalAmount,manageFeeRate);
		if(manageFee == null || BigDecimal.ZERO.compareTo(manageFee)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(manageFee);
	}
	/**
	 * 
	 * @Description:保证金
	 * @return
	 * @author: chaisen
	 * @time:2016年6月7日 上午11:12:19
	 */
	public String getGuaranteeFee() {
		BigDecimal guarantee = BigDecimal.ZERO;
		if(totalAmount==null){
			totalAmount= BigDecimal.ZERO;
		}
		if(guaranteeFeeRate==null){
			guaranteeFeeRate= BigDecimal.ZERO;
		}
		guarantee=FormulaUtil.getManagerAmount(totalAmount,guaranteeFeeRate);
		if(guarantee == null){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(guarantee);
	}
	/**
	 * 
	 * @Description:风险金
	 * @return
	 * @author: chaisen
	 * @time:2016年6月7日 上午11:18:15
	 */
	public String getRiskFee() {
		BigDecimal riskFee = BigDecimal.ZERO;
		if(totalAmount == null ){
			totalAmount= BigDecimal.ZERO;
		}
		if(riskFeeRate == null ){
			riskFeeRate= BigDecimal.ZERO;
		}
		riskFee=FormulaUtil.getManagerAmount(totalAmount,riskFeeRate);
		if(riskFee == null ){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(riskFee);
	}
	/**
	 * 
	 * @Description:介绍费
	 * @return
	 * @author: chaisen
	 * @time:2016年6月7日 上午11:21:11
	 */
	public String getIntroducerFee() {
		BigDecimal introducerFee = BigDecimal.ZERO;
		if(totalAmount == null){
			totalAmount= BigDecimal.ZERO;
		}
		if(introducerFeeRate == null ){
			introducerFeeRate= BigDecimal.ZERO;
		}
		introducerFee=FormulaUtil.getManagerAmount(totalAmount,introducerFeeRate);
		if(introducerFee == null ){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(introducerFee);
	}
	
	public String getFormatStartDate() {
		if(startDate!=null){
			return DateUtils.getDateStrFromDate(startDate);
		}
		return "";
	}
	
	public String getFormatEndDate() {
		if(endDate!=null){
			return DateUtils.getDateStrFromDate(endDate);
		}
		return "";
	}
	public String getOverdueStartDateStr() {
		if(overdueStartDate!=null){
			return DateUtils.getDateStrFromDate(overdueStartDate);
		}
		return "";
	}
	//募集周期
	public String getFormatOnlineDate() {
		if(onlineTime!=null){
			return DateUtils.getDateStrFromDate(onlineTime);
		}
		return "";
	}
	public String getFormatSaleEndDate() {
		if(saleEndTime!=null){
			return DateUtils.getDateStrFromDate(saleEndTime);
		}
		return "";
	}
	public String getFormatRefundTime() {
		if(refundTime!=null){
			return DateUtils.getDateStrFromDate(refundTime);
		}
		return "";
	}
	
	public String getFormatTotalPayAmount() {
		if(totalPayAmount == null || BigDecimal.ZERO.compareTo(totalPayAmount)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(totalPayAmount);
	}
	//可放款金额 
	public String getFormatLoanAmount() {
		BigDecimal loanMount = BigDecimal.ZERO;
		if(totalAmount!=null&&manageFeeRate!=null){
			loanMount=totalAmount.subtract(FormulaUtil.getManagerAmount(totalAmount,manageFeeRate));
		}
		if(loanMount == null || BigDecimal.ZERO.compareTo(loanMount)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(loanMount);
	}
	//募集状态
	public String getProjectStatusStr(){
		if(projectStatus==null){
			return "";
		}
		//投资中
		if(StatusEnum.PROJECT_STATUS_INVESTING.getStatus()==projectStatus){
			return StatusEnum.PROJECT_STATUS_INVESTING.getDesc();
		}
		//已暂停
		if(StatusEnum.PROJECT_STATUS_STOP.getStatus()==projectStatus){
			return "已暂停";
		}
		//已满额，待放款-已满额
		if(StatusEnum.PROJECT_STATUS_FULL.getStatus()==projectStatus||StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus()==projectStatus
				||StatusEnum.PROJECT_STATUS_AUTH_PASS.getStatus()==projectStatus){
			return StatusEnum.PROJECT_STATUS_FULL.getDesc();
		}
		//流标中（风控审核不通过）-已满额
		if(StatusEnum.PROJECT_STATUS_LOSING.getStatus()==projectStatus&&saleComplatedTime!=null){
			return StatusEnum.PROJECT_STATUS_FULL.getDesc();
		}
		//已截止 -已截止
		if(StatusEnum.PROJECT_STATUS_END.getStatus()==projectStatus){
			return StatusEnum.PROJECT_STATUS_END.getDesc();
		}
		//流标中（募集未完成）-已截止 
		if(StatusEnum.PROJECT_STATUS_LOSING.getStatus()==projectStatus&&saleComplatedTime==null){
			return StatusEnum.PROJECT_STATUS_END.getDesc();
		}
		return "";
	}
	//借款周期
	public String getBorrowPeriodStr(){
		if(borrowPeriodType==null){
			return "";
		}
		if(StatusEnum.BORROW_PERIOD_TYPE_DAY.getStatus()==borrowPeriodType){
			return StatusEnum.BORROW_PERIOD_TYPE_DAY.getDesc();
		}
		if(StatusEnum.BORROW_PERIOD_TYPE_MONTH.getStatus()==borrowPeriodType){
			return StatusEnum.BORROW_PERIOD_TYPE_MONTH.getDesc();
		}
		if(StatusEnum.BORROW_PERIOD_TYPE_YEAR.getStatus()==borrowPeriodType){
			return StatusEnum.BORROW_PERIOD_TYPE_YEAR.getDesc();
		}
		return "";
	}
	//流标原因
	public String getLabelReasonStr(){
		if(remarks==null){
			return "";
		}
		if(Integer.toString(StatusEnum.LABEL_REASON_RAISE_NOCOMP.getStatus()).equals(remarks)){
			return StatusEnum.LABEL_REASON_RAISE_NOCOMP.getDesc();
		}
		if(Integer.toString(StatusEnum.LABEL_REASON_RAISE_NOPASS.getStatus()).equals(remarks)){
			return StatusEnum.LABEL_REASON_RAISE_NOPASS.getDesc();
		}
		return "";
	}
	//代还本息格式化
	public String getWaitTotalPayAmountStr() {
		if(waitTotalPayAmount == null || BigDecimal.ZERO.compareTo(waitTotalPayAmount)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(waitTotalPayAmount);
	}
	//项目名称显示到期之前的
	public String getPrefixProjectName() {
		if(projectName==null){
			return "";
		}
		if (projectName.contains("期")) {
			return projectName.substring(0, projectName.indexOf("期") + 1);
		} else {
			return "";
		}
	}
	/**
	 * 
	 * @Description:还款状态
	 * @return
	 * @author: chaisen
	 * @time:2016年3月9日 下午2:13:09
	 */
	public String getPayUnderStatusStr(){
		if(overdueStatus==null){
			return "";
		}
		if(StatusEnum.INTEREST_OVERDUE_REMARK_NORMALPAY.getStatus()==overdueStatus){
			return "已支付";
		}
		if(StatusEnum.INTEREST_OVERDUE_REMARK_UNDERPPAY.getStatus()==overdueStatus){
			return StatusEnum.INTEREST_OVERDUE_REMARK_UNDERPPAY.getDesc();
		}
		if(StatusEnum.INTEREST_OVERDUE_REMARK_UNDERHADPAY.getStatus()==overdueStatus){
			return StatusEnum.INTEREST_OVERDUE_REMARK_UNDERHADPAY.getDesc();
		}
		if(StatusEnum.INTEREST_OVERDUE_REMARK_NO.getStatus()==overdueStatus){
			return StatusEnum.INTEREST_OVERDUE_REMARK_NO.getDesc();
		}
		if(StatusEnum.INTEREST_OVERDUE_WAIT_PAY.getStatus()==overdueStatus){
			return StatusEnum.INTEREST_OVERDUE_WAIT_PAY.getDesc();
		}
		return "";
	}
	/**
	 * 
	 * @Description:还款类型
	 * @return
	 * @author: chaisen
	 * @time:2016年3月10日 下午1:25:43
	 */
	public String getPayTypeStr(){
		if(payType==null){
			return "";
		}
		if(1==payType){
			return "本金+利息";
		}
		if(2==payType){
			return "利息";
		}
		return "";
	}
	public String getRealPayPrincipalStr() {
		if(realPayPrincipal == null || BigDecimal.ZERO.compareTo(realPayPrincipal)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(realPayPrincipal);
	}
	public String getRealPayInterestStr() {
		if(realPayInterest == null || BigDecimal.ZERO.compareTo(realPayInterest)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(realPayInterest);
	}
	public String getOverduePrincipalStr() {
		if(overduePrincipal == null || BigDecimal.ZERO.compareTo(overduePrincipal)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(overduePrincipal);
	}
	public String getOverdueInterestStr() {
		if(overdueInterest == null || BigDecimal.ZERO.compareTo(overdueInterest)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(overdueInterest);
	}
	public String getOverdueFineStr() {
		if(overdueFine == null || BigDecimal.ZERO.compareTo(overdueFine)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(overdueFine);
	}
	public String getTotalAmountStr() {
		if(overdueFine == null ){
			overdueFine= BigDecimal.ZERO;
		}
		if(overduePrincipal == null ){
			overduePrincipal= BigDecimal.ZERO;
		}
		if(overdueInterest == null ){
			overdueInterest= BigDecimal.ZERO;
		}
		return "￥"+FormulaUtil.getFormatPrice(overduePrincipal.add(overdueInterest).add(overdueFine));
	}
	public String getAvailableAmount(){
		BigDecimal manage = BigDecimal.ZERO;
		BigDecimal guarantee = BigDecimal.ZERO;
		BigDecimal risk = BigDecimal.ZERO;
		BigDecimal introducer = BigDecimal.ZERO;
		if(manageFeeRate==null){
			manage=BigDecimal.ZERO;
		}else{
			manage=FormulaUtil.getManagerAmount(totalAmount,manageFeeRate);
		}
		if(guaranteeFeeRate==null){
			guarantee=BigDecimal.ZERO;
		}else{
			guarantee=FormulaUtil.getManagerAmount(totalAmount,guaranteeFeeRate);
		}
		if(riskFeeRate==null){
			risk=BigDecimal.ZERO;
		}else{
			risk=FormulaUtil.getManagerAmount(totalAmount,riskFeeRate);
		}
		if(introducerFeeRate==null){
			introducer=BigDecimal.ZERO;
		}else{
			introducer=FormulaUtil.getManagerAmount(totalAmount,introducerFeeRate);
		}
		return "￥"+FormulaUtil.getFormatPrice(totalAmount.subtract(manage).subtract(guarantee).subtract(risk).subtract(introducer));
	}
	public String getProjectFeeStr(){
		BigDecimal manage = BigDecimal.ZERO;
		BigDecimal guarantee = BigDecimal.ZERO;
		BigDecimal risk = BigDecimal.ZERO;
		BigDecimal introducer = BigDecimal.ZERO;
		if(manageFeeRate==null){
			manage=BigDecimal.ZERO;
		}else{
			manage=FormulaUtil.getManagerAmount(totalAmount,manageFeeRate);
		}
		if(guaranteeFeeRate==null){
			guarantee=BigDecimal.ZERO;
		}else{
			guarantee=FormulaUtil.getManagerAmount(totalAmount,guaranteeFeeRate);
		}
		if(riskFeeRate==null){
			risk=BigDecimal.ZERO;
		}else{
			risk=FormulaUtil.getManagerAmount(totalAmount,riskFeeRate);
		}
		if(introducerFeeRate==null){
			introducer=BigDecimal.ZERO;
		}else{
			introducer=FormulaUtil.getManagerAmount(totalAmount,introducerFeeRate);
		}
		if(quickReward==null){
			quickReward=BigDecimal.ZERO;
		}
		return "￥"+FormulaUtil.getFormatPrice(manage.add(guarantee).add(risk).add(introducer).add(quickReward));
	}
}