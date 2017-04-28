package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.util.Collections3;
import com.yourong.common.util.DateUtils;
import com.yourong.core.ic.model.DebtInterest;

public class PreliminaryOrderDto {
	@JSONField(name="pid")
	/**项目id**/
    private Long projectId;
	@JSONField(name="name")
    /**项目名称**/
    private String projectName;
    /**年化收益率**/
    private BigDecimal annualizedRate;
    /**投资额**/
    private BigDecimal investAmount;
    /**预期收益**/
    private BigDecimal expectAmount;
    /**使用收益券**/
    private String profitCouponNo;
    /**使用收益券增加的年化收益**/
    private BigDecimal extraAnnualizedRate;
    /**收益天数**/
    private int earningsDays;
    /** 优惠券列表**/
    private List<OrderCouponDto> coupons;
    /**债权dto*/
	private List<DebtInterest> debtInterests;
	/**起息日**/
	private Integer interestFrom;
	/**项目收益类型*/
	private String profitType;
	/**期数*/
	private Integer earningPeriod;
	
	/**投资类型（1-债权；2-直投）**/
    private Integer investType;
    
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
	public BigDecimal getInvestAmount() {
		return investAmount;
	}
	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}
	public BigDecimal getExpectAmount() {
		return expectAmount;
	}
	public void setExpectAmount(BigDecimal expectAmount) {
		this.expectAmount = expectAmount;
	}
	public String getProfitCouponNo() {
		return profitCouponNo;
	}
	public void setProfitCouponNo(String profitCouponNo) {
		this.profitCouponNo = profitCouponNo;
	}
	public BigDecimal getExtraAnnualizedRate() {
		return extraAnnualizedRate;
	}
	public void setExtraAnnualizedRate(BigDecimal extraAnnualizedRate) {
		this.extraAnnualizedRate = extraAnnualizedRate;
	}
	public int getEarningsDays() {
		return earningsDays;
	}
	public void setEarningsDays(int earningsDays) {
		this.earningsDays = earningsDays;
	}
	public List<OrderCouponDto> getCoupons() {
		return coupons;
	}
	public void setCoupons(List<OrderCouponDto> coupons) {
		this.coupons = coupons;
	}
    
	/**
	 * 获取第一期的收益天数
	 */

	public Integer getFirstRealEarningDays(){
		if( Collections3.isNotEmpty(debtInterests)){
			for (DebtInterest debtInterest : debtInterests) {
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
		if(Collections3.isNotEmpty(debtInterests)){
			for (DebtInterest debtInterest : debtInterests) {
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
		if(investType !=null&& investType == 2){
			return earningPeriod ;
		}
		Integer period = 0;
		if(Collections3.isNotEmpty(debtInterests)){
			for (DebtInterest debtInterest : debtInterests) {
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
	public void setDebtInterests(List<DebtInterest> debtInterests) {
		this.debtInterests = debtInterests;
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
	
	public String getContractPreviewName(){
		if(investType == null){
			return "债权收益权转让协议范本"; 
		}
		if (this.investType == ProjectEnum.PROJECT_TYPE_DIRECT.getType()){
            return "借款协议范本";
        }
        return "债权收益权转让协议范本";     
	}
    
}
