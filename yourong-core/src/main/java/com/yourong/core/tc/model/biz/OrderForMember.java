package com.yourong.core.tc.model.biz;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.StringUtil;

public class OrderForMember implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 690063366925237829L;

	/****/
	private Long orderId;

	/** 内部交易号，由前台生成 **/
	private String orderNo;

	/** 用户id **/
	private Long memberId;

	private String trueName;

	private String username;

	private Long mobile;

	private String identityNumber;

	private String bankCode;

	private String cardNumber;

	/****/
	private Long projectId;

	private String projectName;

	/** 用户使用资金账户金额 **/
	private BigDecimal usedCapital;
	/** 用户使用资金账户金额 **/
	private BigDecimal usedCouponAmount;
	/** 支付金额 **/
	private BigDecimal payAmount;
	/** 支付方式 **/
	private int payMethod;
	/** 预期收益 **/
	private BigDecimal expectAmount;
	
	/** 预期赚取 **/
	private BigDecimal totalInterest;
	
	/** 投资额 **/
	private BigDecimal investAmount;

	/** 年化收益率 **/
	private BigDecimal annualizedRate;

	/** 使用收益券增加的年化收益 **/
	private BigDecimal extraAnnualizedRate;
	
	/**项目加息的年化收益**/
    private BigDecimal extraProjectAnnualizedRate;

	/** 1:待支付 2：已支付，投资失败 3：已支付，投资成功 4：支付失败 **/
	private Integer status;

	/** 投资类型（1-债权；2-直投） **/
	private Integer investType;

	/** 收益天数 **/
	private int profitDays;

	/** 备注 **/
	private String remarks;

	/** P2P收益周期 **/
	private String profitPeriod;

	/** 起息日，T+1则存1 **/
	private Integer interestFrom;

	/** 使用的现金券编号 **/
	private String cashCouponNo;
	/**
	 * 使用的收益权编号
	 */
	private String profitCouponNo;

	/****/
	private Date orderTime;

	/** 项目到期日 **/
	private Date endDate;

	/****/
	private Date updateTime;

	private Integer orderSource;

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
	
	private Integer extraInterestDay;
	
	
	

	public Integer getExtraInterestDay() {
		return extraInterestDay;
	}

	public void setExtraInterestDay(Integer extraInterestDay) {
		this.extraInterestDay = extraInterestDay;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getTrueName() {
		return trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}

	public Long getMobile() {
		return mobile;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	public String getIdentityNumber() {
		return identityNumber;
	}

	public void setIdentityNumber(String identityNumber) {
		this.identityNumber = identityNumber;
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

	public BigDecimal getExpectAmount() {
		return expectAmount;
	}

	public void setExpectAmount(BigDecimal expectAmount) {
		this.expectAmount = expectAmount;
	}

	public BigDecimal getInvestAmount() {
		return investAmount;
	}

	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public int getProfitDays() {
		return profitDays;
	}

	public void setProfitDays(int profitDays) {
		this.profitDays = profitDays;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getCashCouponNo() {
		return cashCouponNo;
	}

	public void setCashCouponNo(String cashCouponNo) {
		this.cashCouponNo = cashCouponNo;
	}

	public String getProfitCouponNo() {
		return profitCouponNo;
	}

	public void setProfitCouponNo(String profitCouponNo) {
		this.profitCouponNo = profitCouponNo;
	}

	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getStatusName() {
		if (StatusEnum.ORDER_CLOSED.getStatus() == status) {
			return StatusEnum.ORDER_CLOSED.getDesc();
		}
		if (StatusEnum.ORDER_PAYED_FAILED.getStatus() == status
				|| StatusEnum.ORDER_PAYED_INVEST_FAILED.getStatus() == status) {
			return StatusEnum.ORDER_PAYED_FAILED.getDesc();
		}
		if (StatusEnum.ORDER_PAYED_INVESTED.getStatus() == status) {
			return StatusEnum.ORDER_PAYED_INVESTED.getDesc();
		}
		if (StatusEnum.ORDER_WAIT_PAY.getStatus() == status) {
			return StatusEnum.ORDER_WAIT_PAY.getDesc();
		}
		if (StatusEnum.ORDER_WAIT_PROCESS.getStatus() == status) {
			return StatusEnum.ORDER_WAIT_PROCESS.getDesc();
		}
		if (StatusEnum.ORDER_SINA_CASHDESK_CONFIRM.getStatus() == status) {
			return StatusEnum.ORDER_SINA_CASHDESK_CONFIRM.getDesc();
		}
		return null;
	}

	public String getOrderDate() {
		return DateUtils.getStrFromDate(orderTime, DateUtils.DATE_FMT_3);
	}

	public String getOrderHour() {
		return DateUtils.getStrFromDate(orderTime, DateUtils.DATE_FMT_8);
	}

	public String getOrderNoPrefix() {
		return orderNo.substring(0, 16);
	}

	public String getOrderNoSuffix() {
		return orderNo.substring(16);
	}

	/**
	 * 格式化的项目到期日
	 * 
	 * @return
	 */
	public String getEndDateStr() {
		if (endDate != null) {
			return DateUtils.formatDatetoString(endDate, DateUtils.DATE_FMT_3);
		}
		return null;
	}

	/**
	 * 格式化的下单时间
	 * 
	 * @return
	 */
	public String getOrderTimeStr() {
		return DateUtils.formatDatetoString(orderTime, DateUtils.TIME_PATTERN);
	}

	/**
	 * 格式化 投资额
	 * 
	 * @return
	 */
	public String getFormatInvestAmount() {
		return FormulaUtil.formatCurrencyNoUnit(investAmount);
	}

	public BigDecimal getTotalAnnualizedRate() {
		if (this.extraAnnualizedRate == null) {
			return this.annualizedRate;
		} else {
			return this.annualizedRate.add(this.extraAnnualizedRate).setScale(
					2, BigDecimal.ROUND_HALF_UP);
		}
	}

	/**
	 * 处理后的身份证号 例如：330621******2212
	 * 
	 * @return
	 */
	public String getMaskIdentityNumber() {
		return StringUtil.maskIdentityNumber(identityNumber);
	}

	/**
	 * 格式化手机号码
	 * 
	 * @return
	 */
	public String getMaskMobile() {
		return StringUtil.maskMobile(mobile);

	}

	// 格式化预期收益
	public String getFormatExpectAmount() {
		return FormulaUtil.formatCurrencyNoUnit(expectAmount);
	}

	public String getPrefixProjectName() {
		if (projectName.contains("期")) {
			return projectName.substring(0, projectName.indexOf("期") + 1);
		} else {
			return "";
		}
	}

	public String getSuffixProjectName() {
		if (projectName.contains("期")) {
			return projectName.substring(projectName.indexOf("期") + 1);
		} else {
			return projectName;
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public BigDecimal getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}

	public int getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(int payMethod) {
		this.payMethod = payMethod;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public Integer getOrderSource() {
		return orderSource;
	}

	public void setOrderSource(Integer orderSource) {
		this.orderSource = orderSource;
	}

	/**
	 * @return the investType
	 */
	public Integer getInvestType() {
		return investType;
	}

	/**
	 * @param investType
	 *            the investType to set
	 */
	public void setInvestType(Integer investType) {
		this.investType = investType;
	}

	/**
	 * @return the profitPeriod
	 */
	public String getProfitPeriod() {
		return profitPeriod;
	}

	/**
	 * @param profitPeriod
	 *            the profitPeriod to set
	 */
	public void setProfitPeriod(String profitPeriod) {
		this.profitPeriod = profitPeriod;
	}

	/**
	 * @return the interestFrom
	 */
	public Integer getInterestFrom() {
		return interestFrom;
	}

	/**
	 * @param interestFrom
	 *            the interestFrom to set
	 */
	public void setInterestFrom(Integer interestFrom) {
		this.interestFrom = interestFrom;
	}
	
	public boolean getIsDirectProject() {
		if(this.investType == null ){
			return false ;
		}
		if (this.investType == ProjectEnum.PROJECT_TYPE_DIRECT.getType()){
            return true;
        }
        return false;  
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

	public BigDecimal getTotalInterest() {
		return totalInterest;
	}

	public void setTotalInterest(BigDecimal totalInterest) {
		this.totalInterest = totalInterest;
	}

	public BigDecimal getExtraProjectAnnualizedRate() {
		return extraProjectAnnualizedRate;
	}

	public void setExtraProjectAnnualizedRate(BigDecimal extraProjectAnnualizedRate) {
		this.extraProjectAnnualizedRate = extraProjectAnnualizedRate;
	}
	
	public BigDecimal getProjectAnnualizedRate() {
		return this.annualizedRate.setScale(2, BigDecimal.ROUND_HALF_UP);
}

}