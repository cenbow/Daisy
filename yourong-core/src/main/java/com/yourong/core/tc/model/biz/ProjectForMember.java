/**
 * 
 */
package com.yourong.core.tc.model.biz;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.enums.DebtEnum;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.StringUtil;

/**
 * @desc TODO
 * @author zhanghao 2016年1月27日下午1:34:31
 */
public class ProjectForMember implements Serializable {
	/**	募集中的项目前台展示
		 */
	private static final long serialVersionUID = -2784523806362490740L;

	/** 交易号 **/
	private Long transactionId;
	/** 项目ID **/
	private Long projectId;
	/** 年化收益 **/
	private BigDecimal annualizedRate;
	/** 投资金额 **/
	private BigDecimal investAmount;
	/**
	 * 交易状态
	 */
	
	/** 使用账户资金 **/
	private BigDecimal usedCapital;
	
	private int transactionStatus;
	
	/** 总收益 **/
	private BigDecimal totalInterest;
	
	/** 总本金 **/
	private BigDecimal totalPrincipal;

	/** 用户ID **/
	private Long memberId;

	/** 订单ID **/
	private Long orderId;
	
	/** 交易时间 **/
	private Date transactionTime;
	
	/** 项目名称 **/
	private String projectName;

	/** 项目到期日 **/
	private Date endDate;
	
	/** 收益类型 **/
	private String profitType;
	
	/** 订单编号 **/
	private String orderNo;
	/** 状态码(1:存盘,10:待审核,20:待发布,30:投资中,40:已暂停,50:已满额,60:已截止,70:已还款) **/
	private Integer projectStatus;
	
	/** 投资类型（1-债权；2-直投） **/
	private Integer investType;
	
	/** 现金券编号 **/
	private String cashCouponNo;
	
	/** 使用现金券投资金额 **/
	private BigDecimal usedCouponAmount;
	
	/** 收益券编号 **/
	private String profitCouponNo;

	/** 额外年化收益 **/
	private BigDecimal extraAnnualizedRate = BigDecimal.ZERO;
	
	/** 加息收益 **/
	private BigDecimal extraProjectAnnualizedRate = BigDecimal.ZERO;
	
	/**
	 * 投资人身份证号码
	 */
	private String identityNumber;
	
	/**
	 * 投资人手机号码
	 */
	private Long mobile;
	
	/** 投资人姓名 **/
	private String membertName;
	
	  /**借款周期**/
    private Integer borrowPeriod;

    /**借款周期类型（1-日；2-月；3-年）**/
    private Integer borrowPeriodType;
	
    /** P2P收益周期 **/
	private String profitPeriod;
	
	/**起息日，T+1则存1**/
    private Integer interestFrom;
    
    /** 是否为直投项目*/
	private boolean isDirectProject;

	/**签署状态（0-初始化，1-未签署，2-已签署，3-已过期）**/
	private Integer signStatus;
	
	/**转让id*/
	private Long transferId;
	/**转让本金*/
	private BigDecimal transferPrincipal;
	/**项目类型*/
	private Integer projectCategory;
	
	
	
	/**收益券增加的年化收益**/
	private BigDecimal totalExtraInterest;
	
	private Integer extraInterestDay;
	
	
	private BigDecimal totalAllInterest;
	
	
	
	public BigDecimal getTotalAllInterest() {
		return totalAllInterest;
	}

	public void setTotalAllInterest(BigDecimal totalAllInterest) {
		this.totalAllInterest = totalAllInterest;
	}

	public BigDecimal getTotalExtraInterest() {
		return totalExtraInterest;
	}

	public void setTotalExtraInterest(BigDecimal totalExtraInterest) {
		this.totalExtraInterest = totalExtraInterest;
	}

	public Integer getExtraInterestDay() {
		return extraInterestDay;
	}

	public void setExtraInterestDay(Integer extraInterestDay) {
		this.extraInterestDay = extraInterestDay;
	}

	/**
	 * @return the isDirectProject
	 */
	public boolean getIsDirectProject() {
		if (this.investType == ProjectEnum.PROJECT_TYPE_DIRECT.getType()){
            return true;
        }
        return false;  
	}

	/**
	 * @param isDirectProject the isDirectProject to set
	 */
	public void setDirectProject(boolean isDirectProject) {
		this.isDirectProject = isDirectProject;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
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


	public BigDecimal getInvestAmount() {
		return investAmount;
	}

	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}


	public BigDecimal getTotalInterest() {
		return totalInterest;
	}

	public void setTotalInterest(BigDecimal totalInterest) {
		this.totalInterest = totalInterest;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
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

	public String getTransactionTime() {
		return DateUtils.formatDatetoString(transactionTime,
				DateUtils.TIME_PATTERN);
	}
	public Date getTransactionTimeDate() {
		return transactionTime;
	}
	
	public String getTransactionTimeYear() {
		return DateUtils.formatDatetoString(transactionTime,
				DateUtils.DATE_FMT_3);
	}
	
	public String getTransactionTimeHour() {
		return DateUtils.formatDatetoString(transactionTime,
				DateUtils.DATE_FMT_8);
	}
	

	public void setTransactionTime(Date transactionTime) {
		this.transactionTime = transactionTime;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getProfitType() {
		return profitType;
	}

	public void setProfitType(String profitType) {
		this.profitType = profitType;
	}

	public BigDecimal getTotalAnnualizedRate() {
		if (this.extraAnnualizedRate == null) {
			return this.annualizedRate;
		} else {
			return this.annualizedRate.add(this.extraAnnualizedRate).setScale(
					2, BigDecimal.ROUND_HALF_UP);
		}
	}

	public String getStatusName() {
		if (StatusEnum.TRANSACTION_COMPLETE.getStatus() == projectStatus) {
			return StatusEnum.TRANSACTION_COMPLETE.getDesc();
		}
		if (StatusEnum.TRANSACTION_REPAYMENT.getStatus() == projectStatus) {
			return StatusEnum.TRANSACTION_REPAYMENT.getDesc();
		}
		return null;
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
	 * 项目到期日时间处理
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
	 * 投资日期处理
	 *
	 * @return
	 */
	public String getTransactionTimeStr() {
		if (transactionTime != null) {
			return DateUtils.formatDatetoString(transactionTime,
					DateUtils.TIME_PATTERN);
		}
		return null;
	}

	/**
	 * 获取格式化后的投资总额（不包括￥）
	 *
	 * @return
	 */
	public String getFormatInvestAmount() {
		return FormulaUtil.formatCurrencyNoUnit(investAmount);
	}

	//格式化预期收益
	public String getFormatExpectAmount() {
		return FormulaUtil.formatCurrencyNoUnit(totalInterest);
	}

	/**
	* 格式化手机号码
	 * @return
	*/
	public String getMaskMobile() {
		return StringUtil.maskMobile(mobile);
			
	}
		
	/**
	 * 收益类型的名称
	 *
	 * @return
	 */
	public String getProfitTypeName() {
		if (DebtEnum.RETURN_TYPE_DAY.getCode().equals(profitType)) {
			return DebtEnum.RETURN_TYPE_DAY.getDesc();
		}
		if (DebtEnum.RETURN_TYPE_ONCE.getCode().equals(profitType)) {
			return DebtEnum.RETURN_TYPE_ONCE.getDesc();
		}
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL.getCode().equals(profitType)) {
			return DebtEnum.RETURN_TYPE_AVG_PRINCIPAL.getDesc();
		}
		if (DebtEnum.RETURN_TYPE_AVG_INTEREST.getCode().equals(profitType)) {
			return DebtEnum.RETURN_TYPE_AVG_INTEREST.getDesc();
		}
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getCode().equals(profitType)) {
			return DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST.getDesc();
		}
		if (DebtEnum.RETURN_TYPE_DAY_SEASON.getCode().equals(profitType)) {
			return DebtEnum.RETURN_TYPE_DAY_SEASON.getDesc();
		}
		if (DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST_WEEK.getCode().equals(profitType)) {
			return DebtEnum.RETURN_TYPE_AVG_PRINCIPAL_INTEREST_WEEK.getDesc();
		}
		return null;
	}

	// 格式化总收益（不包括￥）
	public String getFormatTotalInterest() {
		if (totalInterest != null) {
			return FormulaUtil.formatCurrencyNoUnit(totalInterest);
		}
		return null;
	}

	public String getPrefixProjectName() {
		if (projectName.contains("期")) {
			return projectName.substring(0, projectName.indexOf("期") + 1);
		} else {
			return projectName;
		}
	}

	public String getSuffixProjectName() {
		if (projectName.contains("期")) {
			return projectName.substring(projectName.indexOf("期") + 1);
		} else {
			return projectName;
		}
	}

	

	public String getFormatUsedCouponAmount() {
		if (usedCouponAmount != null
				&& usedCouponAmount.compareTo(BigDecimal.ZERO) > 0) {
			return FormulaUtil.getFormatPrice(usedCouponAmount);
		} else {
			return "";
		}
	}

	public BigDecimal getusedCouponAmount() {
		return usedCouponAmount;
	}

	public void setUsedCouponAmount(BigDecimal usedCouponAmount) {
		this.usedCouponAmount = usedCouponAmount;
	}

	/** 使用用户的资金 **/
	public String getFormatUsedUserAmount() {
		if (usedCouponAmount != null) {
			return FormulaUtil.getFormatPrice(investAmount
					.subtract(usedCouponAmount));
		} else {
			return FormulaUtil.getFormatPrice(investAmount);
		}
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
	 * @return the projectStatus
	 */
	public Integer getProjectStatus() {
		return projectStatus;
	}

	/**
	 * @param projectStatus
	 *            the projectStatus to set
	 */
	public void setProjectStatus(Integer projectStatus) {
		this.projectStatus = projectStatus;
	}

	/**
	 * @return the transactionStatus
	 */
	public int getTransactionStatus() {
		return transactionStatus;
	}

	/**
	 * @param transactionStatus the transactionStatus to set
	 */
	public void setTransactionStatus(int transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	/**
	 * @return the identityNumber
	 */
	public String getIdentityNumber() {
		return identityNumber;
	}

	/**
	 * @param identityNumber the identityNumber to set
	 */
	public void setIdentityNumber(String identityNumber) {
		this.identityNumber = identityNumber;
	}

	

	/**
	 * @return the mobile
	 */
	public Long getMobile() {
		return mobile;
	}

	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	/**
	 * @return the membertName
	 */
	public String getMembertName() {
		return membertName;
	}

	/**
	 * @param membertName the membertName to set
	 */
	public void setMembertName(String membertName) {
		this.membertName = membertName;
	}

	/**
	 * @return the borrowPeriod
	 */
	public Integer getBorrowPeriod() {
		return borrowPeriod;
	}

	/**
	 * @param borrowPeriod the borrowPeriod to set
	 */
	public void setBorrowPeriod(Integer borrowPeriod) {
		this.borrowPeriod = borrowPeriod;
	}

	/**
	 * @return the borrowPeriodType
	 */
	public Integer getBorrowPeriodType() {
		return borrowPeriodType;
	}

	/**
	 * @param borrowPeriodType the borrowPeriodType to set
	 */
	public void setBorrowPeriodType(Integer borrowPeriodType) {
		this.borrowPeriodType = borrowPeriodType;
	}

	/**
	 * @return the profitPeriod
	 */
	public String getProfitPeriod() {
//		String period =(this.getBorrowPeriodType()==TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY.getType()?"天":
//			(this.getBorrowPeriodType()==TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_MONTH.getType()?"个月":"年"));
//		return this.getBorrowPeriod() + period;
		
		if (this.borrowPeriodType == TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY.getType()) {
			return this.getBorrowPeriod() + "天";
		}
		if (this.borrowPeriodType == TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_MONTH.getType()) {
			return this.getBorrowPeriod() + "个月";
		}
		if (this.borrowPeriodType == TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_YEAR.getType()) {
			return this.getBorrowPeriod() + "年";
		}
		if (this.borrowPeriodType == TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_WEEK.getType()) {
			return this.getBorrowPeriod() + "周";
		}
		return "";
		
	}

	/**
	 * @param profitPeriod the profitPeriod to set
	 */
	public void setProfitPeriod(String profitPeriod) {
		this.profitPeriod = profitPeriod;
	}

	/**
	 * @return the usedCapital
	 */
	public BigDecimal getUsedCapital() {
		return usedCapital;
	}

	/**
	 * @param usedCapital the usedCapital to set
	 */
	public void setUsedCapital(BigDecimal usedCapital) {
		this.usedCapital = usedCapital;
	}

	/**
	 * @return the interestFrom
	 */
	public Integer getInterestFrom() {
		return interestFrom;
	}

	/**
	 * @param interestFrom the interestFrom to set
	 */
	public void setInterestFrom(Integer interestFrom) {
		this.interestFrom = interestFrom;
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

	public Integer getProjectCategory() {
		return projectCategory;
	}

	public void setProjectCategory(Integer projectCategory) {
		this.projectCategory = projectCategory;
	}


	public BigDecimal getTotalPrincipal() {
		return totalPrincipal;
	}

	public void setTotalPrincipal(BigDecimal totalPrincipal) {
		this.totalPrincipal = totalPrincipal;
	}
	
	// 格式化转让本金（不包括￥）
	public String getFormatTransferPrincipal() {
		if (transferPrincipal != null) {
			return FormulaUtil.formatCurrencyNoUnit(transferPrincipal);
		}
		return null;
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
