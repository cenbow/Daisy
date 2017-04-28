package com.yourong.core.tc.model.biz;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.DateUtils;
import com.yourong.core.mc.model.Coupon;

/**
 * 支付订单biz
 * @author Leon Ray
 * 2014年10月8日-下午6:28:14
 */
public class PayOrderBiz extends AbstractBaseObject {

	/** 订单id **/
    private Long orderId;
    
    /**项目id**/
    private Long projectId;
    
    /**会员id**/
    private Long memberId;
    
    /**订单编号**/
    private String orderNo;

    /**项目名称**/
    private String projectName;
    
    /**存钱罐余额**/
    private BigDecimal savingPotBalance;
    /**投资额**/
    private BigDecimal investAmount;
    /**使用现金券额**/
    private BigDecimal usedCouponAmount = BigDecimal.ZERO;
    /**使用存钱罐金额**/
    private BigDecimal usedCapital;

	/***是否快捷，还是网银***/
	private Integer payMethod;

	public Integer getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(Integer payMethod) {
		this.payMethod = payMethod;
	}

	private String cashCouponNo;

    /**
     * 优惠券列表
     */
    private List<Coupon> coupons;
    
    /**订单支付金额（用户充值支付时使用）**/
    private BigDecimal orderPayAmount;
    /**订单使用存钱罐余额（用户充值支付时使用）**/
    private BigDecimal orderSavingPotAmount;
    /**订单使用现金券金额（用户充值支付时使用）**/
    private BigDecimal orderUsedCashAmount;
    /**订单使用现金券编号（用户充值支付时使用）**/
    private String orderUsedCashNo;
    /**订单现金券过期日**/
    private String orderCashEndDate;

	/** 年化收益率 **/
	private BigDecimal annualizedRate;
	/** 预期收益 **/
	private BigDecimal expectAmount;
	/** 购买时间 **/
	private String orderTime;
	/** 使用收益券增加的年化收益 **/
	private BigDecimal extraAnnualizedRate;
	/** 使用收益券增加的收益 **/
	private BigDecimal extraExpectAmount;
	/** 收益天数 **/
	private Integer profitDays;
	/** P2P收益周期 **/
	private String profitPeriod;
	/** 起息日，T+1则存1 **/
	private Integer interestFrom;
	/** 是否直投 **/
	private Boolean isDirectProject;
	/** 订单状态 **/
	private Integer orderStatus;
	  
	/** 项目类型 **/
    private Integer projectCategory;
	/** 转让项目id **/
	private Long transferId;
	/**
	 * 转让本金
	 */
	private BigDecimal transferPrincipal;
	/**
	 * 订单产品价值
	 */
	private BigDecimal projectValue;
	
	  /**原项目最终还款日**/
    private String originalProEndDate;
    
    
    private Integer totalNumber;
    
    
    private Integer extraInterestDay;
    
    
    /** 抽奖结束倒计时 **/
	private Integer lotteryEndCountDown;
    
    
    

	public Integer getExtraInterestDay() {
		return extraInterestDay;
	}

	public void setExtraInterestDay(Integer extraInterestDay) {
		this.extraInterestDay = extraInterestDay;
	}

	public boolean  isLongTimeCashCounpons(){
		if (com.yourong.common.util.StringUtil.isBlank(this.orderCashEndDate))
			return false;
		Date date = DateUtils.getDateFromString(this.orderCashEndDate);
		if (DateUtils.getYear(date)  - DateUtils.getYear(DateUtils.getCurrentDate()) > 99 ){
			return  true;
		}
		return false;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public BigDecimal getSavingPotBalance() {
		return savingPotBalance;
	}

	public void setSavingPotBalance(BigDecimal savingPotBalance) {
		this.savingPotBalance = savingPotBalance;
	}

	public BigDecimal getInvestAmount() {
		return investAmount;
	}

	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}

	public List<Coupon> getCoupons() {
		return coupons;
	}

	public void setCoupons(List<Coupon> coupons) {
		this.coupons = coupons;
	}

	public BigDecimal getUsedCouponAmount() {
		return usedCouponAmount;
	}

	public void setUsedCouponAmount(BigDecimal usedCouponAmount) {
		this.usedCouponAmount = usedCouponAmount;
	}

	public Integer getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(Integer totalNumber) {
		this.totalNumber = totalNumber;
	}

	public BigDecimal getUsedCapital() {
		if(this.usedCouponAmount!=null) {
			if(this.savingPotBalance.doubleValue()>=(this.investAmount.doubleValue()-this.usedCouponAmount.doubleValue())) {
				return (this.investAmount.subtract(this.usedCouponAmount)).setScale(2, BigDecimal.ROUND_HALF_UP);
			} else {
				return this.savingPotBalance.setScale(2, BigDecimal.ROUND_HALF_UP);
			}
		}
		return BigDecimal.ZERO;
	}

	public void setUsedCapital(BigDecimal usedCapital) {
		this.usedCapital = usedCapital;
	}

	/**
	 * 获取未使用优惠券时应付金额
	 * @return
	 */
	public BigDecimal getPayAmountInit() {
		return (this.investAmount.subtract(getUsedCapitalInit())).setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * 获取使用优惠券时应付金额
	 * @return
	 */
	public BigDecimal getPayAmount() {
		BigDecimal payedAmount = getUsedCapital().add(usedCouponAmount);
		if(investAmount.doubleValue()>payedAmount.doubleValue()) {
			return investAmount.subtract(payedAmount).setScale(2, BigDecimal.ROUND_HALF_UP);
		} else {
			return BigDecimal.ZERO;
		}
	}
	
	/**
	 * 获取未使用优惠券时使用存钱罐金额
	 * @return
	 */
	public BigDecimal getUsedCapitalInit() {
		if(this.savingPotBalance.doubleValue()>=this.investAmount.doubleValue()) {
			return this.investAmount;
		} else {
			return this.savingPotBalance;
		}
	}

	public String getCashCouponNo() {
		return cashCouponNo;
	}

	public void setCashCouponNo(String cashCouponNo) {
		this.cashCouponNo = cashCouponNo;
	}

	public BigDecimal getOrderPayAmount() {
		return orderPayAmount;
	}

	public void setOrderPayAmount(BigDecimal orderPayAmount) {
		this.orderPayAmount = orderPayAmount;
	}

	public BigDecimal getOrderSavingPotAmount() {
		return orderSavingPotAmount;
	}

	public void setOrderSavingPotAmount(BigDecimal orderSavingPotAmount) {
		this.orderSavingPotAmount = orderSavingPotAmount;
	}

	public BigDecimal getOrderUsedCashAmount() {
		return orderUsedCashAmount;
	}

	public void setOrderUsedCashAmount(BigDecimal orderUsedCashAmount) {
		this.orderUsedCashAmount = orderUsedCashAmount;
	}

	public String getOrderCashEndDate() {
		return orderCashEndDate;
	}

	public void setOrderCashEndDate(String orderCashEndDate) {
		this.orderCashEndDate = orderCashEndDate;
	}

	public String getOrderUsedCashNo() {
		return orderUsedCashNo;
	}

	public void setOrderUsedCashNo(String orderUsedCashNo) {
		this.orderUsedCashNo = orderUsedCashNo;
	}

	public BigDecimal getAnnualizedRate() {
		return annualizedRate;
	}

	public void setAnnualizedRate(BigDecimal annualizedRate) {
		this.annualizedRate = annualizedRate;
	}

	public BigDecimal getExpectAmount() {
		return expectAmount;
	}

	public void setExpectAmount(BigDecimal expectAmount) {
		this.expectAmount = expectAmount;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public BigDecimal getExtraAnnualizedRate() {
		return extraAnnualizedRate;
	}

	public void setExtraAnnualizedRate(BigDecimal extraAnnualizedRate) {
		this.extraAnnualizedRate = extraAnnualizedRate;
	}

	public BigDecimal getExtraExpectAmount() {
		return extraExpectAmount;
	}

	public void setExtraExpectAmount(BigDecimal extraExpectAmount) {
		this.extraExpectAmount = extraExpectAmount;
	}

	public Integer getProfitDays() {
		return profitDays;
	}

	public void setProfitDays(Integer profitDays) {
		this.profitDays = profitDays;
	}

	public String getProfitPeriod() {
		return profitPeriod;
	}

	public void setProfitPeriod(String profitPeriod) {
		this.profitPeriod = profitPeriod;
	}

	public Integer getInterestFrom() {
		return interestFrom;
	}

	public void setInterestFrom(Integer interestFrom) {
		this.interestFrom = interestFrom;
	}

	public Boolean getIsDirectProject() {
		return isDirectProject;
	}

	public void setIsDirectProject(Boolean isDirectProject) {
		this.isDirectProject = isDirectProject;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}
	
	/**
	 * @return the projectCategory
	 */
	public Integer getProjectCategory() {
		return projectCategory;
	}

	/**
	 * @param projectCategory the projectCategory to set
	 */
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

	public BigDecimal getProjectValue() {
		return projectValue;
	}

	public void setProjectValue(BigDecimal projectValue) {
		this.projectValue = projectValue;
	}

	/**
	 * @return the originalProEndDate
	 */
	public String getOriginalProEndDate() {
		return originalProEndDate;
	}

	/**
	 * @param originalProEndDate the originalProEndDate to set
	 */
	public void setOriginalProEndDate(String originalProEndDate) {
		this.originalProEndDate = originalProEndDate;
	}

	
	
	public Integer getLotteryEndCountDown() {
		return lotteryEndCountDown;
	}
	public void setLotteryEndCountDown(Integer lotteryEndCountDown) {
		this.lotteryEndCountDown = lotteryEndCountDown;
	}
	
	
	

}