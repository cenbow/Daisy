package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.core.uc.model.MemberBankCard;

public class PayOrderDto {
	/**订单id**/
    private Long orderId;
    
    /**项目id**/
    private Long projectId;
    
    /**会员id**/
    @JSONField(serialize=false)
    private Long memberId;
    
    /**订单编号**/
    private String orderNo;

    /**项目名称**/
    @JSONField(serialize=false)
    private String projectName;
    
    /**存钱罐余额**/
    private BigDecimal savingPotBalance;
    /**投资额**/
    private BigDecimal investAmount;
    /**使用现金券额**/
    private BigDecimal usedCouponAmount = BigDecimal.ZERO;
    /**使用存钱罐金额**/
    private BigDecimal usedCapital;
   
    
    private String cashCouponNo;

    /**
     * 优惠券列表
     */
    private List<OrderCouponDto> coupons;
    
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
    
    private List<MemberBankCard> bankCardList;
    
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
	public BigDecimal getUsedCouponAmount() {
		return usedCouponAmount;
	}
	public void setUsedCouponAmount(BigDecimal usedCouponAmount) {
		this.usedCouponAmount = usedCouponAmount;
	}
	public BigDecimal getUsedCapital() {
		return usedCapital;
	}
	public void setUsedCapital(BigDecimal usedCapital) {
		this.usedCapital = usedCapital;
	}
	public String getCashCouponNo() {
		return cashCouponNo;
	}
	public void setCashCouponNo(String cashCouponNo) {
		this.cashCouponNo = cashCouponNo;
	}
	public List<OrderCouponDto> getCoupons() {
		return coupons;
	}
	public void setCoupons(List<OrderCouponDto> coupons) {
		this.coupons = coupons;
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
	public String getOrderUsedCashNo() {
		return orderUsedCashNo;
	}
	public void setOrderUsedCashNo(String orderUsedCashNo) {
		this.orderUsedCashNo = orderUsedCashNo;
	}
	public String getOrderCashEndDate() {
		return orderCashEndDate;
	}
	public void setOrderCashEndDate(String orderCashEndDate) {
		this.orderCashEndDate = orderCashEndDate;
	}
	public List<MemberBankCard> getBankCardList() {
		return bankCardList;
	}
	public void setBankCardList(List<MemberBankCard> bankCardList) {
		this.bankCardList = bankCardList;
	}
    
    
    
    
    
}
