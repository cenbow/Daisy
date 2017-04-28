package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.common.constant.Config;
import com.yourong.common.constant.Constant;
import com.yourong.common.enums.DebtEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.StringUtil;

public class TransactionForMemberDto {

	/** 交易号 **/
	private Long transactionId;

	/** 项目ID **/
	@JSONField(name = "pid")
	private Long projectId;

	/** 项目名称 **/
	@JSONField(name = "name")
	private String projectName;

	/** 投资金额 **/
	private BigDecimal investAmount;

	/** 交易状态 **/
	private int status;
	
	/**
	 * 转让状态（0-未转让 1-转让中 2-部分转让 3-全部转让）
	 */
	private int transferStatus;

	/** 订单编号 **/
	private String orderNo;

	/** 交易时间 **/
	private Date transactionTime;

	/** 项目到期日 **/
	private Date endDate;

	/** 收益天数 **/
	private int totalDays;

	/** 年化收益 **/
	private BigDecimal annualizedRate;

	/** 总收益 **/
	private BigDecimal totalInterest;
	
	/** 已收收益 **/
	private BigDecimal receivedInterest;
	

	/** 总本金 **/
	private BigDecimal totalPrincipal;
	
	/** 已收取本金 **/
	private BigDecimal receivedPrincipal;

	/** 收益列表 **/
	private List<TransactionInterestDto> transactionInterest;

	/** 租赁分红 0：否 1：是 **/
	private Integer joinLease;

	/** 订单ID **/
	private Long orderId;

	/** 额外年化收益 **/
	private BigDecimal extraAnnualizedRate = BigDecimal.ZERO;
	
	/** 项目加息收益 **/
	private BigDecimal extraProjectAnnualizedRate  = BigDecimal.ZERO;

	/** 现金劵金额 **/

	private BigDecimal usedCouponAmount;

	/** 现金券编号 **/
	private String cashCouponNo;


	/**
	 * 特殊项目标识
	 */
	private Integer activitySign;

	/**
	 * 活动标识
	 */
	private boolean isActivity = false;

	/**
	 * 红包标识
	 */
	private Integer redPackageStatus = 0;

	/**
	 * 项目状态
	 */
	private Integer projectStatus;

	/** 投资类型（1-债权；2-直投） **/
	private Integer investType;

	/** 起息日，T+1则存1 **/
	private Integer interestFrom;

	/** P2P收益周期 **/
	private String profitPeriod;

	/** 收益类型 **/
	private String profitType;

	/** 项目是否已满额 **/
	private boolean saleComplatedFlag = false;
	/** 下一笔还款日期 **/
	private Date nextEndDate;
	/** 待还本息 **/
	private BigDecimal repayPrincipal = BigDecimal.ZERO;
	/** 还款日期 **/
	private Date payDate;
	/**逾期、提前还款标志位**/
	private Integer flag;
	/**滞纳金**/
	private BigDecimal overdueFine = BigDecimal.ZERO;
	/**逾期期数**/
	private String overPeriod;
	/**募集进度**/
    private String progress;
    /**募集剩余时间**/
    private String remainingTime;
    /**项目图片**/
    private String thumbnail;
    
    /** 用户ID **/
	private Long memberId;
	
	 /** 未签署合同数 **/
	private Integer unSignContracts;
	
	/**签署状态（0初始化，1否，2是）**/
	private Integer signStatus;
	
	private Long transferId;
	
	/*交易类型：1-普通项目交易 2-转让项目交易*/
	private Integer projectCategory;
	
	/*操作类型   1：无， 2：可转让 */
	private Integer operaType;
	
	private String  operaPrompt; 
	
	/** 认购本金 **/
	private BigDecimal subscriptionAmount;
	
	/**转让本金*/
	private BigDecimal transferPrincipal;
	
	private Integer quickRewardFlag;
	
	private String extraName;
	
	public String getExtraName() {
		return extraName;
	}

	public void setExtraName(String extraName) {
		this.extraName = extraName;
	}

	private Integer transferFlag;
	
	public String getCashCouponNo() {
		return cashCouponNo;
	}

	public void setCashCouponNo(String cashCouponNo) {
		this.cashCouponNo = cashCouponNo;
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

	public Date getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(Date transactionTime) {
		this.transactionTime = transactionTime;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getTotalDays() {
		return totalDays;
	}

	public void setTotalDays(int totalDays) {
		this.totalDays = totalDays;
	}

	public BigDecimal getAnnualizedRate() {
		return annualizedRate;
	}

	public void setAnnualizedRate(BigDecimal annualizedRate) {
		this.annualizedRate = annualizedRate;
	}

	public BigDecimal getUsedCouponAmount() {
		return usedCouponAmount;
	}

	public void setUsedCouponAmount(BigDecimal usedCouponAmount) {
		this.usedCouponAmount = usedCouponAmount;
	}

	public BigDecimal getTotalInterest() {
		return totalInterest;
	}

	public void setTotalInterest(BigDecimal totalInterest) {
		this.totalInterest = totalInterest;
	}

	public List<TransactionInterestDto> getTransactionInterest() {
		return transactionInterest;
	}

	public void setTransactionInterest(List<TransactionInterestDto> transactionInterest) {
		this.transactionInterest = transactionInterest;
	}

	public Integer getJoinLease() {
		return joinLease;
	}

	public void setJoinLease(Integer joinLease) {
		this.joinLease = joinLease;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public BigDecimal getExtraAnnualizedRate() {
		return extraAnnualizedRate;
	}

	public void setExtraAnnualizedRate(BigDecimal extraAnnualizedRate) {
		this.extraAnnualizedRate = extraAnnualizedRate;
	}

	public Integer getActivitySign() {
		if(activitySign == null) {
			return 0;
		}
		return activitySign;
	}

	public void setActivitySign(Integer activitySign) {
		this.activitySign = activitySign;
	}

	public boolean isActivity() {
		return isActivity;
	}

	public void setActivity(boolean isActivity) {
		this.isActivity = isActivity;
	}

	public Integer getRedPackageStatus() {
		return redPackageStatus;
	}

	public void setRedPackageStatus(Integer redPackageStatus) {
		this.redPackageStatus = redPackageStatus;
	}

	public Integer getProjectStatus() {
		return projectStatus;
	}

	public void setProjectStatus(Integer projectStatus) {
		this.projectStatus = projectStatus;
	}

	public Integer getInvestType() {
		return investType;
	}

	public void setInvestType(Integer investType) {
		this.investType = investType;
	}

	public Integer getInterestFrom() {
		return interestFrom;
	}

	public void setInterestFrom(Integer interestFrom) {
		this.interestFrom = interestFrom;
	}

	public String getProfitPeriod() {
		return profitPeriod;
	}

	public void setProfitPeriod(String profitPeriod) {
		this.profitPeriod = profitPeriod;
	}

	public String getProfitType() {
		return profitType;
	}

	public void setProfitType(String profitType) {
		this.profitType = profitType;
	}

	public boolean isSaleComplatedFlag() {
		return saleComplatedFlag;
	}

	public void setSaleComplatedFlag(boolean saleComplatedFlag) {
		this.saleComplatedFlag = saleComplatedFlag;
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
	
	/**
	 * 
	 * @Description:直投进度状态
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月13日 上午11:02:35
	 */
	public String getDirectProcessStatus() {
		if (this.projectStatus == null) {
			return "";
		}
		
		if(projectCategory==TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){
			
			if (StatusEnum.TRANSACTION_REPAYMENT.getStatus() == this.status) {
				return "transferPerform";
			}  else if (StatusEnum.TRANSACTION_COMPLETE.getStatus() == this.status) {
				return "transferRepayed";
			} else {
				return "transferInvested";
			}
		}
		
		if (StatusEnum.PROJECT_STATUS_INVESTING.getStatus() == this.projectStatus
				|| StatusEnum.PROJECT_STATUS_STOP.getStatus() == this.projectStatus
				|| StatusEnum.PROJECT_STATUS_END.getStatus() == this.projectStatus
				|| (StatusEnum.PROJECT_STATUS_LOSING.getStatus() == this.projectStatus && !isSaleComplatedFlag())
				|| (StatusEnum.PROJECT_STATUS_LOSE.getStatus() == this.projectStatus && !isSaleComplatedFlag())) {
			return "invested";
		} else if (StatusEnum.PROJECT_STATUS_FULL.getStatus() == this.projectStatus
				|| (StatusEnum.PROJECT_STATUS_LOSING.getStatus() == this.projectStatus && isSaleComplatedFlag())
				|| (StatusEnum.PROJECT_STATUS_LOSE.getStatus() == this.projectStatus && isSaleComplatedFlag())) {
			return "saleComplated";
		} else if (StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus() == this.projectStatus
				|| StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus() == this.projectStatus
				|| StatusEnum.PROJECT_STATUS_AUTH_PASS.getStatus() == this.projectStatus) {
			return "auditPass";
		} else if (StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus() == this.projectStatus) {
			return "repayed";
		}
		return "";
	}

	/**
	 * 
	 * @Description:直投项目状态
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月13日 上午11:02:35
	 */
	public String getDirectProjectStatus() {
		if (this.projectStatus == null) {
			return "";
		}
		if(projectCategory==TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){
			
			if (StatusEnum.TRANSFER_PROJECT_STATUS_INVESTING.getStatus() == this.projectStatus
					) {
				return "投资中";
			} else if (StatusEnum.TRANSFER_PROJECT_STATUS_LOAN.getStatus() == this.projectStatus) {
				return "履约中";
			}  else if (StatusEnum.TRANSFER_PROJECT_STATUS_REPAYMENT.getStatus() == this.projectStatus) {
				return "已还款";
			} 
		}
		
			if (StatusEnum.PROJECT_STATUS_INVESTING.getStatus() == this.projectStatus) {
				return "募集中";
			} else if (StatusEnum.PROJECT_STATUS_STOP.getStatus() == this.projectStatus) {
				return "募集暂停";
			} else if (StatusEnum.PROJECT_STATUS_END.getStatus() == this.projectStatus
					|| StatusEnum.PROJECT_STATUS_LOSING.getStatus() == this.projectStatus && !isSaleComplatedFlag()) {
				return "已截止";
			} else if (StatusEnum.PROJECT_STATUS_FULL.getStatus() == this.projectStatus
					|| (StatusEnum.PROJECT_STATUS_LOSING.getStatus() == this.projectStatus && isSaleComplatedFlag())
					||StatusEnum.PROJECT_STATUS_AUTH_PASS.getStatus() == this.projectStatus) {
				return "已投满";
			} else if (StatusEnum.PROJECT_STATUS_LOSE.getStatus() == this.projectStatus) {
				return "流标";
			} else if (StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus() == this.projectStatus
					|| StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus() == this.projectStatus
					) {
				return "履约中";
			} else if (StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus() == this.projectStatus) {
				return "已还款";
			}
		return "";
	}

	/**
	 * 
	 * @Description:合同名称
	 * @return
	 * @author: wangyanji
	 * @time:2016年4月13日 上午11:14:33
	 */
	public String getContractTitle() {
		if(projectCategory==TypeEnum.PROJECT_CATEGORY_NORMAL.getType()){
			if (2 == this.investType) {
				return Constant.CONTRACT_P2P_TILE;
			}
			return Constant.CONTRACT_DEBT_TILE;
		}

		if(projectCategory==TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){
			if (2 == this.investType) {
				return Constant.CONTRACT_TRANSFER_P2P_TILE;
			}
			return Constant.CONTRACT_TRANSFER_DEBT_TILE;
		}
		return "";
	}

	public Date getNextEndDate() {
		return nextEndDate;
	}

	public void setNextEndDate(Date nextEndDate) {
		this.nextEndDate = nextEndDate;
	}

	public BigDecimal getRepayPrincipal() {
		return repayPrincipal.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public void setRepayPrincipal(BigDecimal repayPrincipal) {
		this.repayPrincipal = repayPrincipal;
	}

	public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public BigDecimal getOverdueFine() {
		if(overdueFine!=null){
			return overdueFine.setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		return BigDecimal.ZERO;
	}

	public void setOverdueFine(BigDecimal overdueFine) {
		this.overdueFine = overdueFine;
	}

	public String getOverPeriod() {
		return overPeriod;
	}

	public void setOverPeriod(String overPeriod) {
		this.overPeriod = overPeriod;
	}

	public String getProgress() {
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public String getRemainingTime() {
		return remainingTime;
	}

	public void setRemainingTime(String remainingTime) {
		this.remainingTime = remainingTime;
	}
		
	public String getThumbnail() {
		if(thumbnail != null)
		{
			if(getProjectId() != null && getProjectId() >= 989800156L){
				return Config.ossPicUrl+StringUtil.getFilePath(thumbnail,"300");
			}else{
				return Config.ossPicUrl+thumbnail;
			}
		}
		return null;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	
	/**
	 * 结束时间格式化
	 * @return
	 */
	public String getPayDateStr() {
		if(payDate!=null){
			return DateUtils.formatDatetoString(payDate, DateUtils.DATE_FMT_3);
		}
		return "";
	}
	
	/**
	 * 结束时间格式化
	 * @return
	 */
	public String getNextEndDateStr() {
		if(nextEndDate!=null){
			return DateUtils.formatDatetoString(nextEndDate, DateUtils.DATE_FMT_3);
		}
		return "";
	}

	public BigDecimal getReceivedInterest() {
		return receivedInterest;
	}

	public void setReceivedInterest(BigDecimal receivedInterest) {
		this.receivedInterest = receivedInterest;
	}

	/**
	 * @return the unSignContracts
	 */
	public Integer getUnSignContracts() {
		return unSignContracts;
	}

	/**
	 * @param unSignContracts the unSignContracts to set
	 */
	public void setUnSignContracts(Integer unSignContracts) {
		this.unSignContracts = unSignContracts;
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

	/**
	 * @return the operaType
	 */
	public Integer getOperaType() {
		return operaType;
	}

	/**
	 * @param operaType the operaType to set
	 */
	public void setOperaType(Integer operaType) {
		this.operaType = operaType;
	}

	/**
	 * @return the operaPrompt
	 */
	public String getOperaPrompt() {
		return operaPrompt;
	}

	/**
	 * @param operaPrompt the operaPrompt to set
	 */
	public void setOperaPrompt(String operaPrompt) {
		this.operaPrompt = operaPrompt;
	}

	/**
	 * @return the subscriptionAmount
	 */
	public BigDecimal getSubscriptionAmount() {
		return subscriptionAmount;
	}

	/**
	 * @param subscriptionAmount the subscriptionAmount to set
	 */
	public void setSubscriptionAmount(BigDecimal subscriptionAmount) {
		this.subscriptionAmount = subscriptionAmount;
	}

	/**
	 * @return the transferPrincipal
	 */
	public BigDecimal getTransferPrincipal() {
		return transferPrincipal;
	}

	/**
	 * @param transferPrincipal the transferPrincipal to set
	 */
	public void setTransferPrincipal(BigDecimal transferPrincipal) {
		this.transferPrincipal = transferPrincipal;
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

	public Integer getQuickRewardFlag() {
		return quickRewardFlag;
	}

	public void setQuickRewardFlag(Integer quickRewardFlag) {
		this.quickRewardFlag = quickRewardFlag;
	}

	public BigDecimal getExtraProjectAnnualizedRate() {
		return extraProjectAnnualizedRate;
	}

	public void setExtraProjectAnnualizedRate(BigDecimal extraProjectAnnualizedRate) {
		this.extraProjectAnnualizedRate = extraProjectAnnualizedRate;
	}

	/**
	 * @return the transferFlag
	 */
	public Integer getTransferFlag() {
		return transferFlag;
	}

	/**
	 * @param transferFlag the transferFlag to set
	 */
	public void setTransferFlag(Integer transferFlag) {
		this.transferFlag = transferFlag;
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
	
}
