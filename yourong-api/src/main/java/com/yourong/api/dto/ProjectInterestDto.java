package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.core.ic.model.DebtInterest;
import com.yourong.core.ic.model.LotteryRuleAmountNumber;

public class ProjectInterestDto {
	/**项目编号**/
	@JSONField(name="pid")
	private Long projectId;
	
	 /**项目名称**/
    private String projectName;
	
	/**余额**/
    private BigDecimal availableBalance;
    /**递增收益**/
    private BigDecimal incrementAnnualizedRate;
    /**收益计算方式(1-阶梯收益， 2-利率随递增额递增)**/
    private Integer annualizedRateType;
    /**收益天数**/
    private Integer earningsDays;
    
    /**上线时间(年月日时分秒)**/
    private Date onlineTime;
    /**服务器当前时间**/
    private Date currentDate;
    /**项目状态**/
    private Integer status;
    /**一鸣惊人金额**/
    private BigDecimal mostInvestAmount;
    
    private List<ProjectInterestInfoDto> projectInterestInfoList;
    
    private DebtDto debtDto;
    
    private Integer interestFrom;
    
    /**收益类型*/
    private String profitType;
    
    private Integer investType;
    
    private Integer earningPeriod;
    
    /**一鸣惊人**/
    private Integer mostInvestPopularity;
    /**一锤定音**/
    private Integer lastInvestPopularity;
    /**一掷千金**/
    private Integer mostAndLastInvestPopularity;
    
    /** 优惠券列表**/
    private List<OrderCouponDto> coupons;

    
    /*项目类型*/
    private Integer projectCategory;

	/**
	 * 转让项目年化
	 */
	private BigDecimal transferAnnualizedRate;

	/**
	 * 转让项目id
	 */
	private Long transferId;
	
	 /**单位转让金额**/
    private BigDecimal unitTransferAmount;
    

	/** 原交易金额 **/
	private BigDecimal transactionAmount;
	
	/** 产品价值 */
	private BigDecimal projectValue;

	/** 转让价格 **/
	private BigDecimal transferAmount;
	
	private List<LotteryRuleAmountNumber> lottery;

	/** 折价 **/
	private BigDecimal discount;

	public Long getProjectId() {
		return projectId;
	}




	public Integer getMostInvestPopularity() {
		return mostInvestPopularity;
	}




	public void setMostInvestPopularity(Integer mostInvestPopularity) {
		this.mostInvestPopularity = mostInvestPopularity;
	}




	public Integer getLastInvestPopularity() {
		return lastInvestPopularity;
	}




	public void setLastInvestPopularity(Integer lastInvestPopularity) {
		this.lastInvestPopularity = lastInvestPopularity;
	}




	public Integer getMostAndLastInvestPopularity() {
		return mostAndLastInvestPopularity;
	}




	public void setMostAndLastInvestPopularity(Integer mostAndLastInvestPopularity) {
		this.mostAndLastInvestPopularity = mostAndLastInvestPopularity;
	}




	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public BigDecimal getAvailableBalance() {
		return availableBalance;
	}

	public void setAvailableBalance(BigDecimal availableBalance) {
		this.availableBalance = availableBalance;
	}

	public List<ProjectInterestInfoDto> getProjectInterestInfoList() {
		return projectInterestInfoList;
	}

	public void setProjectInterestInfoList(
			List<ProjectInterestInfoDto> projectInterestInfoList) {
		this.projectInterestInfoList = projectInterestInfoList;
	}

	public BigDecimal getIncrementAnnualizedRate() {
		return incrementAnnualizedRate;
	}

	public void setIncrementAnnualizedRate(BigDecimal incrementAnnualizedRate) {
		this.incrementAnnualizedRate = incrementAnnualizedRate;
	}

	public Integer getAnnualizedRateType() {
		return annualizedRateType;
	}

	public void setAnnualizedRateType(Integer annualizedRateType) {
		this.annualizedRateType = annualizedRateType;
	}

	public Integer getEarningsDays() {
		return earningsDays;
	}

	public void setEarningsDays(Integer earningsDays) {
		this.earningsDays = earningsDays;
	}

	public Date getOnlineTime() {
		return onlineTime;
	}

	public void setOnlineTime(Date onlineTime) {
		this.onlineTime = onlineTime;
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public BigDecimal getMostInvestAmount() {
		return mostInvestAmount;
	}

	public void setMostInvestAmount(BigDecimal mostInvestAmount) {
		this.mostInvestAmount = mostInvestAmount;
	}
    
	/**
	 * 获取第一期的收益天数
	 */

	public Integer getFirstRealEarningDays(){
		if(investType !=null&& investType == 2){
			return 0 ;
		}
		if(debtDto!=null && Collections3.isNotEmpty(debtDto.getDebtInterests())){
			for (DebtInterest debtInterest : debtDto.getDebtInterests()) {
				//开始时间小于这期债权的收益时间，则不计交易本息
				Date startInterestDate = DateUtils.formatDate(
						DateUtils.addDate(DateUtils.getCurrentDate(), interestFrom),
						DateUtils.DATE_FMT_3);
				if (!startInterestDate.after(debtInterest.getEndDate())) {
					return DateUtils.daysOfTwo(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3), debtInterest.getEndDate())+1 - interestFrom;
				}
			}
		}
		return 0;
	}

	/**
	 * 获取第一期的债权收益天数
	 */
	public Integer getFirstDebtEarningDays(){
		if(investType !=null&& investType == 2){
			return 0 ;
		}
		if(debtDto!=null && Collections3.isNotEmpty(debtDto.getDebtInterests())){
			for (DebtInterest debtInterest : debtDto.getDebtInterests()) {
				//开始时间小于这期债权的收益时间，则不计交易本息
				Date startInterestDate = DateUtils.formatDate(
						DateUtils.addDate(DateUtils.getCurrentDate(), interestFrom),
						DateUtils.DATE_FMT_3);
				if (!startInterestDate.after(debtInterest.getEndDate())) {
					return DateUtils.daysOfTwo(debtInterest.getStartDate(), debtInterest.getEndDate())+1;
				}
			}
		}
		
		return 0;
		
	}

	/**
	 * 获取收益期数
	 */
	public Integer getEarningPeriod(){
		if(projectCategory==TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()){
			return earningPeriod ;
		}
		if(investType !=null&& investType == 2){
			return earningPeriod ;
		}
		Integer period = 0;
		if(debtDto!=null && Collections3.isNotEmpty(debtDto.getDebtInterests())){
			for (DebtInterest debtInterest : debtDto.getDebtInterests()) {
				//开始时间小于这期债权的收益时间，则不计交易本息
				Date startInterestDate = DateUtils.formatDate(
						DateUtils.addDate(DateUtils.getCurrentDate(), interestFrom),
						DateUtils.DATE_FMT_3);
				if (!startInterestDate.after(debtInterest.getEndDate())) {
					period = period +1;
				}
			}
			return period;
		}
		return period;
	}
	

	public void setEarningPeriod(Integer earningPeriod) {
		this.earningPeriod = earningPeriod;
	}

	public void setDebtDto(DebtDto debtDto) {
		this.debtDto = debtDto;
	}


	public void setInterestFrom(Integer interestFrom) {
		this.interestFrom = interestFrom;
	}

	public String getProfitType() {
		return profitType;
	}

	public void setProfitType(String profitType) {
		this.profitType = profitType;
	}

	public Integer getInvestType() {
		return investType;
	}

	public void setInvestType(Integer investType) {
		this.investType = investType;
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

	public BigDecimal getTransferAnnualizedRate() {
		return transferAnnualizedRate;
	}

	public void setTransferAnnualizedRate(BigDecimal transferAnnualizedRate) {
		this.transferAnnualizedRate = transferAnnualizedRate;
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

	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}


	public BigDecimal getTransferAmount() {
		return transferAmount;
	}

	public void setTransferAmount(BigDecimal transferAmount) {
		this.transferAmount = transferAmount;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}




	public List<LotteryRuleAmountNumber> getLottery() {
		return lottery;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}



	public void setLottery(List<LotteryRuleAmountNumber> lottery) {
		this.lottery = lottery;
	}




	public BigDecimal getProjectValue() {
		return projectValue;
	}


	public void setProjectValue(BigDecimal projectValue) {
		this.projectValue = projectValue;
	}
	
	
	
}
