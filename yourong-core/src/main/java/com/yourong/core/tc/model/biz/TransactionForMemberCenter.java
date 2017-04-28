package com.yourong.core.tc.model.biz;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.enums.DebtEnum;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.FormulaUtil;
import com.yourong.common.util.StringUtil;
import com.yourong.core.ic.model.LeaseBonusDetail;
import com.yourong.core.ic.model.ProjectExtra;
import com.yourong.core.tc.model.TransactionInterest;

public class TransactionForMemberCenter implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -2784523806362490740L;

	/** 交易号 **/
	private Long transactionId;

	/** 项目ID **/
	private Long projectId;

	private String projectName;
	
	/**状态码(1:存盘,10:待审核,20:待发布,30:投资中,40:已暂停,50:已满额,60:已截止,70:已还款)**/
    private Integer projectStatus;

	 /**投资类型（1-债权；2-直投）**/
    private Integer investType;
	
	/** 年化收益 **/
	private BigDecimal annualizedRate;

	/** 额外年化收益 **/
	private BigDecimal extraAnnualizedRate = BigDecimal.ZERO;
	
	/** 项目加息收益 **/
	private BigDecimal extraProjectAnnualizedRate  = BigDecimal.ZERO;

	/**
	 * 收益列表
	 */
	private List<TransactionInterest> transactionInterests;

	/**
	 * 收益天数
	 */
	private int totalDays;

	/** 投资金额 **/
	private BigDecimal investAmount;

	/**
	 * 交易状态(1:履约中2 已还款 3 流标)
	 */
	private int status;
	
	/**
	 * 转让状态（0-未转让 1-转让中 2-部分转让 3-全部转让）
	 */
	private int transferStatus;

	/** 总收益 **/
	private BigDecimal totalInterest;
	
	/** 预期赚取 **/
	private BigDecimal expectedEarning;
	
	
	/** 已收收益 **/
	private BigDecimal receivedInterest;
	
	
	/** 总本金 **/
	private BigDecimal totalPrincipal;
	
	/** 已收取本金 **/
	private BigDecimal receivedPrincipal;
	

	/** 用户ID **/
	private Long memberId;

	/**
	 * 投资人姓名
	 */
	private String trueName;

	/**
	 * 投资人身份证号码
	 */
	private String identityNumber;

	/**
	 * 投资人手机号码
	 */
	private String mobile;

	/** 订单ID **/
	private Long orderId;

	/** 订单状态 **/
	private int orderStatus;

	/** 订单编号 **/
	private String orderNo;
	
	/**现金券编号**/
	private String cashCouponNo;
	
	/**收益券编号**/
	private String profitCouponNo;
	
	/**使用现金券投资金额**/
	private BigDecimal usedCouponAmount;
	
	/**起息日，T+1则存1**/
    private Integer interestFrom;

    /** P2P收益周期 **/
	private String profitPeriod;

	/** 交易时间 **/
	private Date transactionTime;

	/** 项目到期日 **/
	private Date endDate;

	/** 收益类型 **/
	private String profitType;

	/** 租赁分红收益 **/
	private BigDecimal leaseBonusAmounts;

	/** 租赁分红总收益率 **/
	private BigDecimal bonusAnnualizedRate;

	/** 参与交易分红 **/
	private Integer joinLease;

	/** 分红明细列表 **/
	private List<LeaseBonusDetail> leaseBonusDetails;

	/** 债权收益权价值*/
	private BigDecimal price;
	
	/**
	 * 特殊项目标识
	 */
	private Integer activitySign;
	/**备注**/
    private String remarks;
	
	/** 是否为直投项目*/
	private boolean isDirectProject;
	
	/** 滞纳金*/
	private BigDecimal overdueFine;
	
	/** 提前还款标记*/
	private Integer prepayment;
	
	/** 收益类型标记 0-正常，1-逾期，2-提前，3-既逾期又提前*/
	private Integer flag;
	
	/**签署状态（0初始化，1否，2是）**/
	private Integer signStatus;
	
	private Long transferId;
	
	/*交易类型：1-普通项目交易 2-转让项目交易*/
	private Integer projectCategory;
	
	/**转让本金*/
	private BigDecimal transferPrincipal;
	
	private Integer residualDays;
	
	/**1:无，2：转让；3-转让详情*/
	private Integer operaType;

	private Integer extraInterestDay;
	
	 private BigDecimal totalExtraProjectInterest;
	
	 
	 private BigDecimal totalExtraInterest;
	 
	 private BigDecimal allInterest;
	 
	 
	 private BigDecimal receivedExtraInterest;

	/**
	 * 实际支付收益券产生的利息和
	 */
	private BigDecimal totalRealPayExtraInterest;

	/**
	 * 实付利息
	 */
	private BigDecimal totalRealPayInterest;
	 
	 
	 
	 private List<ProjectExtra> projectExtraList;
	 
	 /**
	  * 项目类型 0-不是直投项目 1-直投项目
	  */
	 private int projectType =0;
	
	 private String statusCNName;



	public int getProjectType() {
		return projectType;
	}

	public void setProjectType(int projectType) {
		this.projectType = projectType;
	}

	public BigDecimal getReceivedExtraInterest() {
		return receivedExtraInterest;
	}

	public void setReceivedExtraInterest(BigDecimal receivedExtraInterest) {
		this.receivedExtraInterest = receivedExtraInterest;
	}

	public BigDecimal getTotalExtraInterest() {
		return totalExtraInterest;
	}

	public void setTotalExtraInterest(BigDecimal totalExtraInterest) {
		this.totalExtraInterest = totalExtraInterest;
	}

	public BigDecimal getAllInterest() {
		return allInterest;
	}

	public void setAllInterest(BigDecimal allInterest) {
		this.allInterest = allInterest;
	}

	public BigDecimal getTotalExtraProjectInterest() {
		return totalExtraProjectInterest;
	}

	public void setTotalExtraProjectInterest(BigDecimal totalExtraProjectInterest) {
		this.totalExtraProjectInterest = totalExtraProjectInterest;
	}

	public Integer getExtraInterestDay() {
		return extraInterestDay;
	}

	public void setExtraInterestDay(Integer extraInterestDay) {
		this.extraInterestDay = extraInterestDay;
	}

	/**
	 * 交易的最后一条交易本息状态
	 */
	private Integer interestStatus;

	/**
	 * 交易状态
	 */
	private Integer transactionStatus;
	/**
	 * @return the isDirectProject
	 */
	public boolean getIsDirectProject() {
		if(this.investType==null){
			return false;
		}
		if (this.investType.intValue() == ProjectEnum.PROJECT_TYPE_DIRECT.getType()){
            return true;
        }
        return false;  
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
	 * @param isDirectProject the isDirectProject to set
	 */
	public void setIsDirectProject(boolean isDirectProject) {
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

	public int getTotalDays() {
		return totalDays;
	}

	public void setTotalDays(int totalDays) {
		this.totalDays = totalDays;
	}

	public BigDecimal getInvestAmount() {
		return investAmount;
	}

	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

	public String getTrueName() {
		return trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}

	public String getIdentityNumber() {
		return identityNumber;
	}

	public void setIdentityNumber(String identityNumber) {
		this.identityNumber = identityNumber;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public int getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(int orderStatus) {
		this.orderStatus = orderStatus;
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

	public Date getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(Date transactionTime) {
		this.transactionTime = transactionTime;
	}

	public List<TransactionInterest> getTransactionInterests() {
		return transactionInterests;
	}

	public void setTransactionInterests(List<TransactionInterest> transactionInterests) {
		this.transactionInterests = transactionInterests;
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
			return this.annualizedRate.add(this.extraAnnualizedRate).setScale(2, BigDecimal.ROUND_HALF_UP);
		}
	}

	public String getStatusName() {
		if (StatusEnum.TRANSACTION_INVESTMENTING.getStatus() == status) {
			return StatusEnum.TRANSACTION_INVESTMENTING.getDesc();
		}
		if (StatusEnum.TRANSACTION_COMPLETE.getStatus() == status) {
			return StatusEnum.TRANSACTION_COMPLETE.getDesc();
		}
		if (StatusEnum.TRANSACTION_REPAYMENT.getStatus() == status) {
			return StatusEnum.TRANSACTION_REPAYMENT.getDesc();
		}
		if (StatusEnum.TRANSACTION_LOSE.getStatus() == status) {
			return StatusEnum.TRANSACTION_LOSE.getDesc();
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
			return DateUtils.formatDatetoString(transactionTime, DateUtils.TIME_PATTERN);
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
	
	/**
	 * 获取格式化后的认购本金总额（不包括￥）
	 *
	 * @return
	 */
	public String getFormatTransferPrincipal() {
		if (transferPrincipal != null) {
			return FormulaUtil.formatCurrencyNoUnit(transferPrincipal);
		}
		return null;
	}
	
	/**
	 * 获取格式化后的回款总计总额（不包括￥）
	 *
	 * @return
	 */
	public String getFormatTotalPayment() {
		if(transferPrincipal!=null){
			return FormulaUtil.formatCurrencyNoUnit(transferPrincipal.add(totalInterest));
		}
		return "";
	}

	/**
	 * 手机号码
	 *
	 * @return
	 */
	public String getMaskMobile() {
		if(mobile!=null){
			return StringUtil.maskMobile(Long.valueOf(mobile));
		}
		return "";
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

	public BigDecimal getLeaseBonusAmounts() {
		return leaseBonusAmounts;
	}

	public void setLeaseBonusAmounts(BigDecimal leaseBonusAmounts) {
		this.leaseBonusAmounts = leaseBonusAmounts;
	}

	public BigDecimal getBonusAnnualizedRate() {
		return bonusAnnualizedRate;
	}

	public void setBonusAnnualizedRate(BigDecimal bonusAnnualizedRate) {
		this.bonusAnnualizedRate = bonusAnnualizedRate;
	}

	public boolean isLeaseBonus() {
		if (this.leaseBonusAmounts != null && this.leaseBonusAmounts.doubleValue() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	

	public boolean getJoinLease() {
		if (joinLease == null) {
			return false;
		}
		if (joinLease == 1) {
			return true;
		}
		return false;
	}

	public void setJoinLease(Integer joinLease) {
		this.joinLease = joinLease;
	}

	public List<LeaseBonusDetail> getLeaseBonusDetails() {
		return leaseBonusDetails;
	}

	public void setLeaseBonusDetails(List<LeaseBonusDetail> leaseBonusDetails) {
		this.leaseBonusDetails = leaseBonusDetails;
	}

	public String getLeaseTotalRentalStr() {
		if (leaseBonusAmounts != null) {
			return FormulaUtil.formatCurrencyNoUnit(leaseBonusAmounts);
		}
		return null;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getFormatUsedCouponAmount() {
		if(usedCouponAmount!=null && usedCouponAmount.compareTo(BigDecimal.ZERO)>0){
			return FormulaUtil.getFormatPrice(usedCouponAmount);
		}else{
			return "";
		}
	}
	
	public BigDecimal getusedCouponAmount() {
		return usedCouponAmount;
	}

	public void setUsedCouponAmount(BigDecimal usedCouponAmount) {
		this.usedCouponAmount = usedCouponAmount;
	}
	
	/**使用用户的资金**/
	public String getFormatUsedUserAmount(){
		if(usedCouponAmount!=null){
			return FormulaUtil.getFormatPrice(investAmount.subtract(usedCouponAmount));
		}else {
			return  FormulaUtil.getFormatPrice(investAmount);
		}
	}

	public Integer getActivitySign() {
		return activitySign;
	}

	public void setActivitySign(Integer activitySign) {
		this.activitySign = activitySign;
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
	 * @return the projectStatus
	 */
	public Integer getProjectStatus() {
		return projectStatus;
	}

	/**
	 * @param projectStatus the projectStatus to set
	 */
	public void setProjectStatus(Integer projectStatus) {
		this.projectStatus = projectStatus;
	}

	public BigDecimal getOverdueFine() {
		return overdueFine;
	}

	public void setOverdueFine(BigDecimal overdueFine) {
		this.overdueFine = overdueFine;
	}

	public BigDecimal getReceivedInterest() {
		return receivedInterest;
	}

	public void setReceivedInterest(BigDecimal receivedInterest) {
		this.receivedInterest = receivedInterest;
	}

	public Integer getPrepayment() {
		if(projectStatus!=null&&StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()==projectStatus&&StatusEnum.TRANSACTION_COMPLETE.getStatus() == status){
			return prepayment;
		}
		return 0;
	}

	public void setPrepayment(Integer prepayment) {
		this.prepayment = prepayment;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
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

	/**
	 * @return the transferId
	 */
	public Long getTransferId() {
		return transferId;
	}

	/**
	 * @param transferId the transferId to set
	 */
	public void setTransferId(Long transferId) {
		this.transferId = transferId;
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

	public BigDecimal getTransferPrincipal() {
		return transferPrincipal;
	}

	public void setTransferPrincipal(BigDecimal transferPrincipal) {
		this.transferPrincipal = transferPrincipal;
	}

	public Integer getOperaType() {
		return operaType;
	}

	public void setOperaType(Integer operaType) {
		this.operaType = operaType;
	}


	/**
	 * @return the residualDays
	 */
	public Integer getResidualDays() {
		return residualDays;
	}

	/**
	 * @param residualDays the residualDays to set
	 */
	public void setResidualDays(Integer residualDays) {
		this.residualDays = residualDays;
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

	public BigDecimal getExpectedEarning() {
		return expectedEarning;
	}

	public void setExpectedEarning(BigDecimal expectedEarning) {
		this.expectedEarning = expectedEarning;
	}

	public Integer getInterestStatus() {
		return interestStatus;
	}

	public void setInterestStatus(Integer interestStatus) {
		this.interestStatus = interestStatus;
	}

	// 格式化预期赚取（不包括￥）
	public String getFormatExpectedEarning() {
		if (expectedEarning != null) {
			return FormulaUtil.formatCurrencyNoUnit(expectedEarning);
		}
		return null;
	}

	public BigDecimal getExtraProjectAnnualizedRate() {
		return extraProjectAnnualizedRate;
	}

	public void setExtraProjectAnnualizedRate(BigDecimal extraProjectAnnualizedRate) {
		this.extraProjectAnnualizedRate = extraProjectAnnualizedRate;
	}

	public Integer getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(Integer transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	/**
	 * @return the transferStatus
	 */
	public int getTransferStatus() {
		return transferStatus;
	}

	/**
	 * @param transferStatus the transferStatus to set
	 */
	public void setTransferStatus(int transferStatus) {
		this.transferStatus = transferStatus;
	}

	public BigDecimal getProjectAnnualizedRate() {
			return this.annualizedRate.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public BigDecimal getTotalRealPayExtraInterest() {
		return totalRealPayExtraInterest;
	}

	public void setTotalRealPayExtraInterest(BigDecimal totalRealPayExtraInterest) {
		this.totalRealPayExtraInterest = totalRealPayExtraInterest;
	}

	public BigDecimal getTotalRealPayInterest() {
		return totalRealPayInterest;
	}

	public void setTotalRealPayInterest(BigDecimal totalRealPayInterest) {
		this.totalRealPayInterest = totalRealPayInterest;
	}
	public List<ProjectExtra> getProjectExtraList() {
		return projectExtraList;
	}
	public void setProjectExtraList(List<ProjectExtra> projectExtraList) {
		this.projectExtraList = projectExtraList;
	}


	public String getStatusCNName() {
		return statusCNName;
	}

	public void setStatusCNName(String statusCNName) {
		this.statusCNName = statusCNName;
	}
}