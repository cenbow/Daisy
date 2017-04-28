package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.api.utils.SysServiceUtils;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.DateUtils;

public class ProjectInfoDto {
	
	/**项目id**/
	@JSONField(name="pid")
    private Long id;

    /**项目名称**/
    private String name;

    /**收益类型**/
    private String profitType;

    /**投资总金额**/
    private BigDecimal totalAmount;

    /**最小收益**/
    private BigDecimal minAnnualizedRate;

    /**最大收益**/
    private BigDecimal maxAnnualizedRate;

    /**项目开始日期(年月日)**/
    private Date startDate;

    /**还款时间(年月日)**/
    private Date endDate;

    /**上线时间(年月日时分秒)**/
    private Date onlineTime;

    /**销售截止日期(年月日时分秒)**/
    private Date saleEndTime;
    
    /**起息日，T+1则存1**/
    private Integer interestFrom;
    
    /**状态码(1:存盘,10:待审核,20:待发布,30:投资中,40:已暂停,50:已满额,60:已截止,70:已还款)**/
    private Integer status;

    /**就否新手项目**/
    @JSONField(serialize=false)
    private Integer isNovice;
    
    /**收益天数**/
    private Integer earningsDays;
    
    /**余额**/
    private BigDecimal availableBalance;
    
    /**投资进度**/
    private String investmentProgress;
    
    /**销售总数**/
    private Integer totalInvestment = 0;
    
    /**服务器当前时间**/
    private Date currentDate;

    /** 是否参与租赁分红 **/
    @JSONField(serialize=false)
   	private Integer joinLease;
    
    /** 投资会员数 **/
    private Integer transactionMemberCount;
    
    /** 项目总收益 **/
    private BigDecimal totalTransactionInterest;
    
    /**借款周期**/
    private Integer borrowPeriod;

    /**借款周期类型（1-日；2-月；3-年）**/
    private Integer borrowPeriodType;
    
    private String profitPeriod;
    
    /**投资类型（1-债权；2-直投）**/
    private Integer investType;
    
    /**还款计划标志位**/
    private Integer repaymentPlanFlag;
    
    /**支付中的订单数**/
    private Integer orders;
    
    /**项目支付中的订单金额**/
    private BigDecimal orderAmount;
    
    /*是否可以转让*/
    private Integer transferFlag;
    
	/**起息日后X个自然日可以转让**/
	private Integer transferAfterInterest;
	
	 /**折价**/
    private BigDecimal discount;
    
    /*项目类型*/
    private Integer projectCategory;
    
    private Long transferId;
    
    private List<DynamicMenuDto> inforMations;
    
    //0：不是快投有奖 1：是快投有奖
    private Integer quickRewardFlag;
    
    /**特殊业务标识*/
    private Integer extraType;
    
    /**加息数值*/
    private String addRate;
	
    /**快投奖励截止时间**/
    private Date quickRewardDate;
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProfitType() {
		return profitType;
	}

	public void setProfitType(String profitType) {
		this.profitType = profitType;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
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

	public Date getOnlineTime() {
		return onlineTime;
	}

	public void setOnlineTime(Date onlineTime) {
		this.onlineTime = onlineTime;
	}

	public Date getSaleEndTime() {
		return saleEndTime;
	}

	public void setSaleEndTime(Date saleEndTime) {
		this.saleEndTime = saleEndTime;
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


	public Integer getIsNovice() {
		return isNovice;
	}

	public void setIsNovice(Integer isNovice) {
		this.isNovice = isNovice;
	}

	public void setEarningsDays(Integer earningsDays) {
		this.earningsDays = earningsDays;
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	/**
	 * 收益天数
	 * @return
	 */
	public Integer getEarningsDays() {
		return getEarningsDaysByStatus();
	}
	
	/**
	 * 收益总天数
	 * @return
	 */
	@JSONField(serialize = false)
	public Integer getEarningsTotalDays() {
		int day = DateUtils.getIntervalDays(getStartDate(), getEndDate())+1 - getInterestFrom();
		return day > 0 ? day : 0;
	}
	
	/**
	 * 获得预告项目的收益总天数
	 * @return
	 */
	@JSONField(serialize = false)
	public Integer getNoticeProjectEarningsTotalDays() {
		int day = DateUtils.getIntervalDays(DateUtils.formatDate(getOnlineTime(), DateUtils.DATE_FMT_3), getEndDate())+1 - getInterestFrom();
		return day > 0 ? day : 0;
	}
	
	
	/**
	 * 剩余收益天数
	 * @return
	 */
	@JSONField(serialize = false)
	public Integer getResidualIncomeDays() {
		int day = DateUtils.getIntervalDays(DateUtils.formatDate(DateUtils.getCurrentDate(), DateUtils.DATE_FMT_3), getEndDate())+1 - getInterestFrom();
		return day > 0 ? day : 0;
	}
	
	/**
	 * 根据状态显示剩余收益天数或收益总天数
	 * @return
	 */
	@JSONField(serialize = false)
	public Integer getEarningsDaysByStatus(){
		if(isNotice()){//预告
			return getNoticeProjectEarningsTotalDays();
		}
		if(status == StatusEnum.PROJECT_STATUS_INVESTING.getStatus() || status == StatusEnum.PROJECT_STATUS_STOP.getStatus()){
			return getResidualIncomeDays();
		}
		return getEarningsTotalDays();
	}
	
	/**
	 * 是否是预告
	 * @return
	 */
	public boolean isNotice(){
		if(status >= StatusEnum.PROJECT_STATUS_INVESTING.getStatus()){
			return false;
		}
		return true;
	}

	public BigDecimal getAvailableBalance() {
		return availableBalance;
	}

	public void setAvailableBalance(BigDecimal availableBalance) {
		this.availableBalance = availableBalance;
	}

	public String getInvestmentProgress() {
		return investmentProgress;
	}

	public void setInvestmentProgress(String investmentProgress) {
		this.investmentProgress = investmentProgress;
	}

	public Integer getTotalInvestment() {
		return totalInvestment;
	}

	public void setTotalInvestment(Integer totalInvestment) {
		this.totalInvestment = totalInvestment;
	}

	public Integer getJoinLease() {
		return joinLease;
	}

	public void setJoinLease(Integer joinLease) {
		this.joinLease = joinLease;
	}
	
	public Integer getTransactionMemberCount() {
		return transactionMemberCount;
	}

	public void setTransactionMemberCount(Integer transactionMemberCount) {
		this.transactionMemberCount = transactionMemberCount;
	}

	public BigDecimal getTotalTransactionInterest() {
		return totalTransactionInterest;
	}

	public void setTotalTransactionInterest(BigDecimal totalTransactionInterest) {
		this.totalTransactionInterest = totalTransactionInterest;
	}

	/**
     * 是否新手项目
     * @return
     */
    public boolean getIsNoviceProject(){
    	if(isNovice == null){
    		return false;
    	}
    	if(isNovice == 0){
    		return true;
    	}
    	return false;
    }
    
    /**
     * 是否参与租赁分红
     * @return
     */
    public boolean getIsJoinLease(){
    	if(joinLease == null){
    		return false;
    	}
    	if(joinLease == 1){
    		return true;
    	}
    	return false;
    }
    
    /**
     * 还款方式
     * @return
     */
    public String getRepaymentText(){
    	return SysServiceUtils.getDictLabelByValue(getProfitType(), "return_type", "按日计息，按月付息，到期还本");
    }
    
    
    public Integer getBorrowPeriod() {
		return borrowPeriod;
	}

	public void setBorrowPeriod(Integer borrowPeriod) {
		this.borrowPeriod = borrowPeriod;
	}

	public Integer getBorrowPeriodType() {
		return borrowPeriodType;
	}

	public void setBorrowPeriodType(Integer borrowPeriodType) {
		this.borrowPeriodType = borrowPeriodType;
	}

	public String getProfitPeriod() {
		if(this.borrowPeriod!=null && this.borrowPeriodType!=null){
//			return this.borrowPeriod.toString()+(this.borrowPeriodType==TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY.getType()?"天":
//				(this.borrowPeriodType==TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_MONTH.getType()?"个月":"年"));
			
			String borrowPeriodResult = this.borrowPeriod.toString();
			if(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY.getType()==borrowPeriodType){
				return borrowPeriodResult + "天";
			}
			if(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_MONTH.getType()==borrowPeriodType){
				return borrowPeriodResult + "个月";
			}
			if(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_YEAR.getType()==borrowPeriodType){
				return borrowPeriodResult + "年";
			}
			if(TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_WEEK.getType()==borrowPeriodType){
				return borrowPeriodResult + "周";
			}
		}
		return "";
	}
	
	public void setProfitPeriod(String profitPeriod) {
		this.profitPeriod = profitPeriod;
	}

	public Integer getInvestType() {
		return investType;
	}

	public void setInvestType(Integer investType) {
		this.investType = investType;
	}

	public Integer getRepaymentPlanFlag() {
		if(investType == ProjectEnum.PROJECT_TYPE_DEBT.getType()){
			return 0;
		}
		if(status == 52 ||status == 70 || status == 51 || status==81){//履约中和已还款显示还款计划tab
			return 1 ;
		}
		return 0;
	}

	public void setRepaymentPlanFlag(Integer repaymentPlanFlag) {
		this.repaymentPlanFlag = repaymentPlanFlag;
	}

	public Integer getOrders() {
		return orders;
	}

	public void setOrders(Integer orders) {
		this.orders = orders;
	}

	public BigDecimal getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(BigDecimal orderAmount) {
		this.orderAmount = orderAmount;
	}

	/**
	 * @return the transferAfterInterest
	 */
	public Integer getTransferAfterInterest() {
		return transferAfterInterest;
	}

	/**
	 * @param transferAfterInterest the transferAfterInterest to set
	 */
	public void setTransferAfterInterest(Integer transferAfterInterest) {
		this.transferAfterInterest = transferAfterInterest;
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

	public Integer getQuickRewardFlag() {
		return quickRewardFlag;
	}

	public void setQuickRewardFlag(Integer quickRewardFlag) {
		this.quickRewardFlag = quickRewardFlag;
	}

	/**
	 * @return the inforMations
	 */
	public List<DynamicMenuDto> getInforMations() {
		return inforMations;
	}

	/**
	 * @param inforMations the inforMations to set
	 */
	public void setInforMations(List<DynamicMenuDto> inforMations) {
		this.inforMations = inforMations;
	}

	public Integer getExtraType() {
		return extraType;
	}

	public void setExtraType(Integer extraType) {
		this.extraType = extraType;
	}

	public String getAddRate() {
		return addRate;
	}

	public void setAddRate(String addRate) {
		this.addRate = addRate;
	}

	public Date getQuickRewardDate() {
		return quickRewardDate;
	}

	public void setQuickRewardDate(Date quickRewardDate) {
		this.quickRewardDate = quickRewardDate;
	}
	
}
