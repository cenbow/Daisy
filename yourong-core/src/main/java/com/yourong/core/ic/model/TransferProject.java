/**
 * 
 */
package com.yourong.core.ic.model;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.StringUtil;

/**
 * @desc TODO
 * @author zhanghao
 * 2016年8月28日上午10:35:41
 */
public class TransferProject extends AbstractBaseObject {

	/**转让项目ID**/
    private Long id;

    /**原项目ID**/
    private Long projectId;

    /**转让人id**/
    private Long memberId;
    
    /**交易id**/
    private Long transactionId;
    
    /**转让开始时间**/
    private Date transferStartDate;

    /**转让结束时间**/
    private Date transferEndDate;
    
    /**转让成功时间*/
    private Date transferSaleComplatedTime;
    
    /**转让项目名称*/
    private String transferName;
    
    /**转让项目年化利率**/
    private BigDecimal transferAnnualizedRate;    
    
    /**转让项目可认购本金**/
    private BigDecimal subscriptionPrincipal;
    
    /**单位转让金额**/
    private BigDecimal unitTransferAmount;
    
    /**单位认购金额**/
    private BigDecimal unitSubscriptionAmount;
    
    /**折价**/
    private BigDecimal discount;
    
    /**原交易金额**/
    private BigDecimal transactionAmount;
    
	/** 当期利息 */
    private BigDecimal currentInterest;
    
    /** 剩余利息 */
    private BigDecimal residualInterest;
    
	/** 剩余本金 */
    private BigDecimal residualPrincipal;
    
    /**转让价格**/
    private BigDecimal transferAmount;
    
    /**已转本金**/
    private BigDecimal transferredPrincipal;
    
    /**状态（30：投资中，52：已转让,70：已还款,90：流标）**/
    private Integer status;
    
    /**0-未失败，1-流标，2-撤销**/
    private Integer failFlag;
    
    /**收益类型**/
    private String profitType;

    /**起息日，转让市场为T+0**/
    private Integer interestFrom;
    
    /**项目缩略图**/
    private String thumbnail;
    
    /**创建时间**/
    private Date createTime;
    
    /**更新时间**/
    private Date updateTime;
    
    /**流标时间**/
    private Date failTime;

    /**备注**/
    private String remarks;

    /**删除标记**/
    private Integer delFlag;
    
	 /**投资人数*/
	private Integer investNum;
	
	 /**最高投资额*/
	private BigDecimal mostInvestAmount;
	
	 /**累计总收益*/
	private BigDecimal totalIncome;

	/**进度条*/
	private String transferProgress;

	/**转让手续费率 */
	private BigDecimal transferRate;
	
	/**转让手续费 */
	private BigDecimal transferFee;
	
	/**转让最终获得*/
	private BigDecimal transferGain;
	
	/**转让剩余时间*/
	private String transferRemainTime;
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
	 * @return the transactionId
	 */
	public Long getTransactionId() {
		return transactionId;
	}

	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	/**
	 * @return the transferStartDate
	 */
	public Date getTransferStartDate() {
		return transferStartDate;
	}

	/**
	 * @param transferStartDate the transferStartDate to set
	 */
	public void setTransferStartDate(Date transferStartDate) {
		this.transferStartDate = transferStartDate;
	}

	/**
	 * @return the transferEndDate
	 */
	public Date getTransferEndDate() {
		return transferEndDate;
	}

	/**
	 * @param transferEndDate the transferEndDate to set
	 */
	public void setTransferEndDate(Date transferEndDate) {
		this.transferEndDate = transferEndDate;
	}

	/**
	 * @return the transferSaleComplatedTime
	 */
	public Date getTransferSaleComplatedTime() {
		return transferSaleComplatedTime;
	}

	/**
	 * @param transferSaleComplatedTime the transferSaleComplatedTime to set
	 */
	public void setTransferSaleComplatedTime(Date transferSaleComplatedTime) {
		this.transferSaleComplatedTime = transferSaleComplatedTime;
	}

	/**
	 * @return the transferName
	 */
	public String getTransferName() {
		return transferName;
	}

	/**
	 * @param transferName the transferName to set
	 */
	public void setTransferName(String transferName) {
		this.transferName = transferName;
	}

	/**
	 * @return the transferAnnualizedRate
	 */
	public BigDecimal getTransferAnnualizedRate() {
		return transferAnnualizedRate;
	}

	/**
	 * @param transferAnnualizedRate the transferAnnualizedRate to set
	 */
	public void setTransferAnnualizedRate(BigDecimal transferAnnualizedRate) {
		this.transferAnnualizedRate = transferAnnualizedRate;
	}
	
	/**
	 * @return the unitTransferAmount
	 */
	public BigDecimal getUnitTransferAmount() {
		return unitTransferAmount;
	}

	/**
	 * @param unitTransferAmount the unitTransferAmount to set
	 */
	public void setUnitTransferAmount(BigDecimal unitTransferAmount) {
		this.unitTransferAmount = unitTransferAmount;
	}

	/**
	 * @return the unitSubscriptionAmount
	 */
	public BigDecimal getUnitSubscriptionAmount() {
		return unitSubscriptionAmount;
	}

	/**
	 * @param unitSubscriptionAmount the unitSubscriptionAmount to set
	 */
	public void setUnitSubscriptionAmount(BigDecimal unitSubscriptionAmount) {
		this.unitSubscriptionAmount = unitSubscriptionAmount;
	}

	/**
	 * @return the discount
	 */
	public BigDecimal getDiscount() {
		return discount;
	}

	/**
	 * @param discount the discount to set
	 */
	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	/**
	 * @return the transferAmount
	 */
	public BigDecimal getTransferAmount() {
		return transferAmount;
	}

	/**
	 * @param transferAmount the transferAmount to set
	 */
	public void setTransferAmount(BigDecimal transferAmount) {
		this.transferAmount = transferAmount;
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
	 * @return the failFlag
	 */
	public Integer getFailFlag() {
		return failFlag;
	}

	/**
	 * @param failFlag the failFlag to set
	 */
	public void setFailFlag(Integer failFlag) {
		this.failFlag = failFlag;
	}

	/**
	 * @return the profitType
	 */
	public String getProfitType() {
		return profitType;
	}

	/**
	 * @param profitType the profitType to set
	 */
	public void setProfitType(String profitType) {
		this.profitType = profitType;
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
	 * @return the thumbnail
	 */
	public String getThumbnail() {
		return thumbnail;
	}

	/**
	 * @param thumbnail the thumbnail to set
	 */
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	/**
	 * @return the createTime
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime the createTime to set
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return the updateTime
	 */
	public Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * @param updateTime the updateTime to set
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
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
	 * @return the delFlag
	 */
	public Integer getDelFlag() {
		return delFlag;
	}

	/**
	 * @param delFlag the delFlag to set
	 */
	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	/**
	 * @return the transactionAmount
	 */
	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

	/**
	 * @param transactionAmount the transactionAmount to set
	 */
	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	/**
	 * @return the investNum
	 */
	public Integer getInvestNum() {
		return investNum;
	}

	/**
	 * @param investNum the investNum to set
	 */
	public void setInvestNum(Integer investNum) {
		this.investNum = investNum;
	}

	/**
	 * @return the mostInvestAmount
	 */
	public BigDecimal getMostInvestAmount() {
		return mostInvestAmount;
	}

	/**
	 * @param mostInvestAmount the mostInvestAmount to set
	 */
	public void setMostInvestAmount(BigDecimal mostInvestAmount) {
		this.mostInvestAmount = mostInvestAmount;
	}

	/**
	 * @return the totalIncome
	 */
	public BigDecimal getTotalIncome() {
		return totalIncome;
	}

	/**
	 * @param totalIncome the totalIncome to set
	 */
	public void setTotalIncome(BigDecimal totalIncome) {
		this.totalIncome = totalIncome;
	}


	/**
	 * @return the failTime
	 */
	public Date getFailTime() {
		return failTime;
	}

	/**
	 * @param failTime the failTime to set
	 */
	public void setFailTime(Date failTime) {
		this.failTime = failTime;
	}

	/**
	 * @return the currentInterest
	 */
	public BigDecimal getCurrentInterest() {
		return currentInterest;
	}

	/**
	 * @param currentInterest the currentInterest to set
	 */
	public void setCurrentInterest(BigDecimal currentInterest) {
		this.currentInterest = currentInterest;
	}

	/**
	 * @return the residualPrincipal
	 */
	public BigDecimal getResidualPrincipal() {
		return residualPrincipal;
	}

	/**
	 * @param residualPrincipal the residualPrincipal to set
	 */
	public void setResidualPrincipal(BigDecimal residualPrincipal) {
		this.residualPrincipal = residualPrincipal;
	}
	
	public String getTransferProgress(){
		return transferProgress;
	}
	
	public void setTransferProgress(String transferPprogress){
		this. transferProgress=transferPprogress;
	}

	public BigDecimal getTransferFee() {
		return transferFee;
	}

	public void setTransferFee(BigDecimal transferFee) {
		this.transferFee = transferFee;
	}

	public BigDecimal getTransferGain() {
		return transferGain;
	}

	public void setTransferGain(BigDecimal transferGain) {
		this.transferGain = transferGain;
	}
	
	public String getTransferRemainTime() {
		String time = "";
		if(transferEndDate==null){
			return transferRemainTime;
		}
		Date currentDate = DateUtils.getCurrentDate();
		if ( DateUtils.daysOfTwo(currentDate, transferEndDate) > 0) {
			time = DateUtils.daysOfTwo(currentDate, transferEndDate)+"天";
		} else if (DateUtils.getTimeIntervalHours(currentDate, transferEndDate) > 0) {
			time = DateUtils.getTimeIntervalHours(currentDate, transferEndDate)+"小时";
		}else if (DateUtils.getTimeIntervalMins(currentDate, transferEndDate)>0){
			time = DateUtils.getTimeIntervalMins(currentDate, transferEndDate)+"分钟";
		}else if (DateUtils.getTimeIntervalSencond(currentDate, transferEndDate)>0){
			time = 1+"分钟";
		}
		return time;
	}

	public String getShortTransferName(){
		 return StringUtil.getShortProjectName(transferName);
	}

	/**
	 * @return the residualInterest
	 */
	public BigDecimal getResidualInterest() {
		return residualInterest;
	}

	/**
	 * @param residualInterest the residualInterest to set
	 */
	public void setResidualInterest(BigDecimal residualInterest) {
		this.residualInterest = residualInterest;
	}
	
	public String getPrefixTransferName() {
		if (transferName.contains("期")) {
			return transferName.substring(0, transferName.indexOf("期") + 1);
		} else {
			return transferName;
		}
	}

	/**
	 * @return the transferRate
	 */
	public BigDecimal getTransferRate() {
		return transferRate;
	}

	/**
	 * @param transferRate the transferRate to set
	 */
	public void setTransferRate(BigDecimal transferRate) {
		this.transferRate = transferRate;
	}

	/**
	 * @return the transferredPrincipal
	 */
	public BigDecimal getTransferredPrincipal() {
		return transferredPrincipal;
	}

	/**
	 * @param transferredPrincipal the transferredPrincipal to set
	 */
	public void setTransferredPrincipal(BigDecimal transferredPrincipal) {
		this.transferredPrincipal = transferredPrincipal;
	}

	/**
	 * @return the subscriptionPrincipal
	 */
	public BigDecimal getSubscriptionPrincipal() {
		return subscriptionPrincipal;
	}

	/**
	 * @param subscriptionPrincipal the subscriptionPrincipal to set
	 */
	public void setSubscriptionPrincipal(BigDecimal subscriptionPrincipal) {
		this.subscriptionPrincipal = subscriptionPrincipal;
	}
	
}
