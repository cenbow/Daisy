/**
 * 
 */
package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.Digits;

import com.yourong.common.constant.Constant;
import com.yourong.common.enums.TypeEnum;
import com.yourong.core.uc.model.MemberBankCard;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年8月9日下午3:11:12
 */
public class OrderForAppDto {

	/****/
    private Long id;

    /**内部交易号，由前台生成**/
    private String orderNo;

    /**外部交易号，由第三方返回**/
    private String outOrderNo;

    /**用户id**/
    private Long memberId;

    /****/
    private Long projectId;

    /**银行编号**/
    private String bankCode;


    /**年化收益率**/
    private BigDecimal annualizedRate;
    
    /**使用收益券增加的年化收益**/
    private BigDecimal extraAnnualizedRate;

    /**项目加息的年化收益**/
    private BigDecimal extraProjectAnnualizedRate;
    
  /*  *//**支付金额**//*
    private BigDecimal payAmount;*/

    /**1:待支付 2：已支付，投资失败 3：已支付，投资成功 4：支付失败**/
    private Integer status;

 /*   *//**1-新浪支付，2-盛付通**//*
    private Integer payMethod;*/
    
    /**备注**/
    private String remarks;
/*    *//**
     * 使用的收益权编号
     *//*
    private String profitCouponNo;*/
    /**
     * 是否第一次创建订单
     *//*
    private Boolean isFirstCreaterOrder ;*/
    /**
     * 银行卡ID
     */
    private Long cardId;
    
    /**
     * 项目名称
     */
    private String prefixProjectName;
    
    private String profitPeriod;
    
    /**投资类型（1-债权；2-直投）**/
    private Integer investType; 
    

    /**投资额**/
    @Digits (integer=10, fraction=2)
    private BigDecimal investAmount;
    
    /**存钱罐余额**/
    private BigDecimal savingPotBalance;
    /**
     * 优惠券列表
     */
    private List<OrderCouponDto> coupons;
    
    private BigDecimal expectAmount;
    
    private BigDecimal extraExpectAmount;
    
    private BigDecimal totalExpectAmount;
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
    
    private boolean isWithholdAuthority;

    
    /*项目类型*/
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
	 * 产品价值
	 */
	private BigDecimal projectValue;
	
	
	 private String extraName;
	 
	 

	public String getExtraName() {
		return extraName;
	}

	public void setExtraName(String extraName) {
		this.extraName = extraName;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the orderNo
	 */
	public String getOrderNo() {
		return orderNo;
	}

	/**
	 * @param orderNo the orderNo to set
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	/**
	 * @return the outOrderNo
	 */
	public String getOutOrderNo() {
		return outOrderNo;
	}

	/**
	 * @param outOrderNo the outOrderNo to set
	 */
	public void setOutOrderNo(String outOrderNo) {
		this.outOrderNo = outOrderNo;
	}

	/**
	 * @return the memberId
	 */
	public Long getMemberId() {
		return memberId;
	}

	/**
	 * @param memberId the memberId to set
	 */
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

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
	 * @return the bankCode
	 */
	public String getBankCode() {
		return bankCode;
	}

	/**
	 * @param bankCode the bankCode to set
	 */
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	/**
	 * @return the annualizedRate
	 */
	public BigDecimal getAnnualizedRate() {
		return annualizedRate;
	}

	/**
	 * @param annualizedRate the annualizedRate to set
	 */
	public void setAnnualizedRate(BigDecimal annualizedRate) {
		this.annualizedRate = annualizedRate;
	}

	/**
	 * @return the extraAnnualizedRate
	 */
	public BigDecimal getExtraAnnualizedRate() {
		return extraAnnualizedRate;
	}

	/**
	 * @param extraAnnualizedRate the extraAnnualizedRate to set
	 */
	public void setExtraAnnualizedRate(BigDecimal extraAnnualizedRate) {
		this.extraAnnualizedRate = extraAnnualizedRate;
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


	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * @return the cardId
	 */
	public Long getCardId() {
		return cardId;
	}

	/**
	 * @param cardId the cardId to set
	 */
	public void setCardId(Long cardId) {
		this.cardId = cardId;
	}

	/**
	 * @return the prefixProjectName
	 */
	public String getPrefixProjectName() {
		return prefixProjectName;
	}

	/**
	 * @param prefixProjectName the prefixProjectName to set
	 */
	public void setPrefixProjectName(String prefixProjectName) {
		this.prefixProjectName = prefixProjectName;
	}

	/**
	 * @return the profitPeriod
	 */
	public String getProfitPeriod() {
		return profitPeriod;
	}

	/**
	 * @param profitPeriod the profitPeriod to set
	 */
	public void setProfitPeriod(String profitPeriod) {
		this.profitPeriod = profitPeriod;
	}

	/**
	 * @return the investType
	 */
	public Integer getInvestType() {
		return investType;
	}

	/**
	 * @param investType the investType to set
	 */
	public void setInvestType(Integer investType) {
		this.investType = investType;
	}

	/**
	 * @return the investAmount
	 */
	public BigDecimal getInvestAmount() {
		return investAmount;
	}

	/**
	 * @param investAmount the investAmount to set
	 */
	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}

	/**
	 * @return the savingPotBalance
	 */
	public BigDecimal getSavingPotBalance() {
		return savingPotBalance;
	}

	/**
	 * @param savingPotBalance the savingPotBalance to set
	 */
	public void setSavingPotBalance(BigDecimal savingPotBalance) {
		this.savingPotBalance = savingPotBalance;
	}

	/**
	 * @return the coupons
	 */
	public List<OrderCouponDto> getCoupons() {
		return coupons;
	}

	/**
	 * @param coupons the coupons to set
	 */
	public void setCoupons(List<OrderCouponDto> coupons) {
		this.coupons = coupons;
	}


	/**
	 * @return the bankCardList
	 */
	public List<MemberBankCard> getBankCardList() {
		return bankCardList;
	}

	/**
	 * @param bankCardList the bankCardList to set
	 */
	public void setBankCardList(List<MemberBankCard> bankCardList) {
		this.bankCardList = bankCardList;
	}

	/**
	 * @return the expectAmount
	 */
	public BigDecimal getExpectAmount() {
		return expectAmount;
	}

	/**
	 * @param expectAmount the expectAmount to set
	 */
	public void setExpectAmount(BigDecimal expectAmount) {
		this.expectAmount = expectAmount;
	}

	/**
	 * @return the extraExpectAmount
	 */
	public BigDecimal getExtraExpectAmount() {
		return extraExpectAmount;
	}

	/**
	 * @param extraExpectAmount the extraExpectAmount to set
	 */
	public void setExtraExpectAmount(BigDecimal extraExpectAmount) {
		this.extraExpectAmount = extraExpectAmount;
	}

	/**
	 * @return the isWithholdAuthority
	 */
	public boolean getIsWithholdAuthority() {
		return isWithholdAuthority;
	}

	/**
	 * @param isWithholdAuthority the isWithholdAuthority to set
	 */
	public void setWithholdAuthority(boolean isWithholdAuthority) {
		this.isWithholdAuthority = isWithholdAuthority;
	}

	/**
	 * @return the orderPayAmount
	 */
	public BigDecimal getOrderPayAmount() {
		return orderPayAmount;
	}

	/**
	 * @param orderPayAmount the orderPayAmount to set
	 */
	public void setOrderPayAmount(BigDecimal orderPayAmount) {
		this.orderPayAmount = orderPayAmount;
	}

	/**
	 * @return the orderSavingPotAmount
	 */
	public BigDecimal getOrderSavingPotAmount() {
		return orderSavingPotAmount;
	}

	/**
	 * @param orderSavingPotAmount the orderSavingPotAmount to set
	 */
	public void setOrderSavingPotAmount(BigDecimal orderSavingPotAmount) {
		this.orderSavingPotAmount = orderSavingPotAmount;
	}

	/**
	 * @return the orderUsedCashAmount
	 */
	public BigDecimal getOrderUsedCashAmount() {
		return orderUsedCashAmount;
	}

	/**
	 * @param orderUsedCashAmount the orderUsedCashAmount to set
	 */
	public void setOrderUsedCashAmount(BigDecimal orderUsedCashAmount) {
		this.orderUsedCashAmount = orderUsedCashAmount;
	}

	/**
	 * @return the orderUsedCashNo
	 */
	public String getOrderUsedCashNo() {
		return orderUsedCashNo;
	}

	/**
	 * @param orderUsedCashNo the orderUsedCashNo to set
	 */
	public void setOrderUsedCashNo(String orderUsedCashNo) {
		this.orderUsedCashNo = orderUsedCashNo;
	}

	/**
	 * @return the orderCashEndDate
	 */
	public String getOrderCashEndDate() {
		return orderCashEndDate;
	}

	/**
	 * @param orderCashEndDate the orderCashEndDate to set
	 */
	public void setOrderCashEndDate(String orderCashEndDate) {
		this.orderCashEndDate = orderCashEndDate;
	}

	/**
	 * @return the totalExpectAmount
	 */
	public BigDecimal getTotalExpectAmount() {
		return totalExpectAmount;
	}

	/**
	 * @param totalExpectAmount the totalExpectAmount to set
	 */
	public void setTotalExpectAmount(BigDecimal totalExpectAmount) {
		this.totalExpectAmount = totalExpectAmount;
	}
	
	/**
	 * 
	 * @Description:合同名称
	 * @return
	 * @author: zhanghao
	 * @time:2016年7月14日 上午11:14:33
	 */
	public String getContractTitle() {
		
		if(projectCategory==TypeEnum.PROJECT_CATEGORY_NORMAL.getType()){
			if (2 == this.investType) {
				return Constant.CONTRACT_P2P_TILE+"范本";
			}
			return Constant.CONTRACT_DEBT_TILE+"范本";
		}

		if(projectCategory==TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){
			if (2 == this.investType) {
				return Constant.CONTRACT_TRANSFER_P2P_TILE+"范本";
			}
			return Constant.CONTRACT_TRANSFER_DEBT_TILE+"范本";
		}
		return "";
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

	public BigDecimal getExtraProjectAnnualizedRate() {
		return extraProjectAnnualizedRate;
	}

	public void setExtraProjectAnnualizedRate(BigDecimal extraProjectAnnualizedRate) {
		this.extraProjectAnnualizedRate = extraProjectAnnualizedRate;
	}
	
}
