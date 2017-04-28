package com.yourong.core.ic.model;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.util.FormulaUtil;
/**
 * 
 * @desc 直投项目营收结算
 * @author chaisen
 * 2016年5月6日下午4:50:47
 */
public class DirectSettlementBiz {

	/** 项目编号 */
	private Long projectId;

	/** 项目名称 */
	private String projectName;

	/** 项目总额 */
	private BigDecimal totalAmount;

	/** 管理费率 */
	private BigDecimal manageFeeRate;
	
	/**管理费**/
	private BigDecimal manageFee;

	/** 借款人 */
	private String  borrowerName;
	
	/**放款时间***/
	private Date loanTime;

	/** 项目开始时间 */
	private Date startDate;

	/** 项目结束时间 */
	private Date endDate;


	/** 现金券支出 */
	private BigDecimal usedCouponAmount;


	/** 平台支付投资人利息 **/
	private BigDecimal extraInterest;

	/** 平台毛利润 */
	private BigDecimal grossProfit;
	
	 /**保证金费率**/
    private BigDecimal guaranteeFeeRate;
    
    /**风险金费率**/
    private BigDecimal riskFeeRate;
    
    /**介绍费率**/
    private BigDecimal introducerFeeRate;

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

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getManageFeeRate() {
		return manageFeeRate;
	}

	public void setManageFeeRate(BigDecimal manageFeeRate) {
		this.manageFeeRate = manageFeeRate;
	}

	public BigDecimal getManageFee() {
		return manageFee;
	}

	public void setManageFee(BigDecimal manageFee) {
		this.manageFee = manageFee;
	}

	public String getBorrowerName() {
		return borrowerName;
	}

	public void setBorrowerName(String borrowerName) {
		this.borrowerName = borrowerName;
	}


	public Date getLoanTime() {
		return loanTime;
	}

	public void setLoanTime(Date loanTime) {
		this.loanTime = loanTime;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public BigDecimal getUsedCouponAmount() {
		return usedCouponAmount;
	}

	public void setUsedCouponAmount(BigDecimal usedCouponAmount) {
		this.usedCouponAmount = usedCouponAmount;
	}

	public BigDecimal getExtraInterest() {
		return extraInterest;
	}

	public void setExtraInterest(BigDecimal extraInterest) {
		this.extraInterest = extraInterest;
	}

	public BigDecimal getGrossProfit() {
		return grossProfit;
	}

	public void setGrossProfit(BigDecimal grossProfit) {
		this.grossProfit = grossProfit;
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

	/**
	 * 
	 * @Description:格式化项目总额
	 * @return
	 * @author: chaisen
	 * @time:2016年5月6日 下午5:22:22
	 */
	public String getTotalAmountStr() {
		if(totalAmount == null || BigDecimal.ZERO.compareTo(totalAmount)==0){
			return "￥0";
		}
		return "￥"+FormulaUtil.getFormatPrice(totalAmount);
	}
	/**
	 * 
	 * @Description:获取管理费
	 * @return
	 * @author: chaisen
	 * @time:2016年2月16日 下午1:23:47
	 */
	public String getManageFeeStr() {
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
	/** 格式化收益券支出 */
	public String getExtraInterestStr() {
		if (extraInterest == null) {
			extraInterest = BigDecimal.ZERO;
		}
		return FormulaUtil.getFormatPrice(extraInterest);
	}
	/** 格式化现金券支出 */
	public String getUsedCouponAmountStr() {
		if (usedCouponAmount == null) {
			usedCouponAmount = BigDecimal.ZERO;
		}
		return FormulaUtil.getFormatPrice(usedCouponAmount);
	}
	/**平台毛利润**/
	public String getGrossProfitStr() {
		if (usedCouponAmount == null) {
			usedCouponAmount = BigDecimal.ZERO;
		}
		if (extraInterest == null) {
			extraInterest = BigDecimal.ZERO;
		}
		if(manageFeeRate==null){
			manageFeeRate=BigDecimal.ZERO;
		}
		manageFee=FormulaUtil.getManagerAmount(totalAmount,manageFeeRate);
		if (manageFee == null) {
			manageFee = BigDecimal.ZERO;
		}
		return FormulaUtil.getFormatPrice(manageFee.subtract(usedCouponAmount).subtract(extraInterest));
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
		return "￥"+FormulaUtil.getFormatPrice(manage.add(guarantee).add(risk).add(introducer));
	}
}
