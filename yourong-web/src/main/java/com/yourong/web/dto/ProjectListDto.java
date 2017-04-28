package com.yourong.web.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.util.DateUtils;

public class ProjectListDto {

	/**项目id**/
    private Long id;

    /**目前只有债权ID**/
    private Long debtId;

    /**项目名称**/
    private String name;
	
	/**收益计算方式(1-阶梯收益， 2-利率随递增额递增)**/
    private Integer annualizedRateType;

    /**最小收益**/
    private BigDecimal minAnnualizedRate;

    /**最大收益**/
    private BigDecimal maxAnnualizedRate;

    /**递增收益**/
    private BigDecimal incrementAnnualizedRate;
    
    /**起息日，T+1则存1**/
    private Integer interestFrom;
    
    /**状态码(1:存盘,10:待审核,20:待发布,30:投资中,40:已暂停,50:已满额,60:已截止,70:已还款)**/
    private Integer status;
    
    /**项目开始日期**/
    private Date startDate;
    
    /**还款时间**/
    private Date endDate;
    
    /**收益天数**/
    private Integer earningsDays;
    
    /**折价**/
    private BigDecimal discount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDebtId() {
		return debtId;
	}

	public void setDebtId(Long debtId) {
		this.debtId = debtId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAnnualizedRateType() {
		return annualizedRateType;
	}

	public void setAnnualizedRateType(Integer annualizedRateType) {
		this.annualizedRateType = annualizedRateType;
	}

	public BigDecimal getMinAnnualizedRate() {
		return minAnnualizedRate;
	}

	public void setMinAnnualizedRate(BigDecimal minAnnualizedRate) {
		this.minAnnualizedRate = minAnnualizedRate;
	}

	public BigDecimal getMaxAnnualizedRate() {
		return maxAnnualizedRate;
	}

	public void setMaxAnnualizedRate(BigDecimal maxAnnualizedRate) {
		this.maxAnnualizedRate = maxAnnualizedRate;
	}

	public BigDecimal getIncrementAnnualizedRate() {
		return incrementAnnualizedRate;
	}

	public void setIncrementAnnualizedRate(BigDecimal incrementAnnualizedRate) {
		this.incrementAnnualizedRate = incrementAnnualizedRate;
	}

	public Integer getInterestFrom() {
		return interestFrom;
	}

	public void setInterestFrom(Integer interestFrom) {
		this.interestFrom = interestFrom;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public Integer getEarningsDays() {
		if(this.earningsDays != null && this.earningsDays > 0){
			return this.earningsDays;
		}
		int day = DateUtils.getIntervalDays(getStartDate(), getEndDate())+1 - getInterestFrom();
		return day > 0 ? day : 0;
	}

	public void setEarningsDays(Integer earningsDays) {
		this.earningsDays = earningsDays;
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
	
}
