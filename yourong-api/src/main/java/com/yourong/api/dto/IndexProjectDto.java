package com.yourong.api.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.common.constant.Config;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.StringUtil;

public class IndexProjectDto {
	/**项目id**/
	@JSONField(name="pid")
    private Long id;
	
	 /**项目名称**/
    private String name;
    
    /**最小收益**/
    private BigDecimal minAnnualizedRate;

    /**最大收益**/
    private BigDecimal maxAnnualizedRate;
    
    @JSONField(serialize = false)
    /**起息日，T+1则存1**/
    private Integer interestFrom;
    
    /**状态码(1:存盘,10:待审核,20:待发布,30:投资中,40:已暂停,50:已满额,60:已截止,70:已还款)**/
    private Integer status;
    
    @JSONField(serialize = false)
    /**项目开始日期(年月日)**/
    private Date startDate;
    
    @JSONField(serialize = false)    
    /**还款时间(年月日)**/
    private Date endDate;
    
    /**上线时间**/
    private Date onlineTime;
    
    @JSONField(serialize = false)    
    /**销售截止日期**/
    private Date saleEndTime;
    
    /**收益天数**/
    private Integer earningsDays;
    
    /**余额**/
    private BigDecimal availableBalance;
    
    /**投资总金额**/
    private BigDecimal totalAmount;
    
    /**项目缩略图**/
    private String thumbnail;
    
    /**投资进度**/
    private String investmentProgress;
    
    /** 就否新手项目 **/
    @JSONField(serialize = false)
	private Integer isNovice;

	/** 是否参与租赁分红 **/
    @JSONField(serialize = false)
	private Integer joinLease;
    
    private Date currentDate;
    
    private Integer countMember;
    
    private String totalInterest;
    
    @JSONField(serialize = false)
    private Integer appRecommend;

    /**特殊活动标识*/
    private Integer activitySign;
    
    /**特殊业务标识*/
    private Integer extraType;
    
    /**加息数值*/
    private String addRate;
    
    /**借款周期**/
    private Integer borrowPeriod;

    /**借款周期类型（1-日；2-月；3-年）**/
    private Integer borrowPeriodType;
    
    /**投资类型（1-债权；2-直投）**/
    private Integer investType;
    
    /**起投金额**/
    private BigDecimal minInvestAmount;
    
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

	public Integer getEarningsDays() {
		return getEarningsDaysByStatus();
	}

	public void setEarningsDays(Integer earningsDays) {
		this.earningsDays = earningsDays;
	}

	public BigDecimal getAvailableBalance() {
		return availableBalance;
	}

	public void setAvailableBalance(BigDecimal availableBalance) {
		this.availableBalance = availableBalance;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getThumbnail() {
		if(thumbnail != null){
			if(getId() != null && getId() >= 989800156L){
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

	public Integer getIsNovice() {
		return isNovice;
	}

	public void setIsNovice(Integer isNovice) {
		this.isNovice = isNovice;
	}

	public Integer getJoinLease() {
		return joinLease;
	}

	public void setJoinLease(Integer joinLease) {
		this.joinLease = joinLease;
	}
	
	
	public Integer getCountMember() {
		return countMember;
	}

	public void setCountMember(Integer countMember) {
		this.countMember = countMember;
	}


	public String getTotalInterest() {
		return totalInterest;
	}

	public void setTotalInterest(String totalInterest) {
		this.totalInterest = totalInterest;
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
		if(getIsNotice()){//预告
			return getNoticeProjectEarningsTotalDays();
		}
		if(getStatus() == StatusEnum.PROJECT_STATUS_INVESTING.getStatus() || getStatus() == StatusEnum.PROJECT_STATUS_STOP.getStatus()){
			return getResidualIncomeDays();
		}
		return getEarningsTotalDays();
	}
	
	/**
	 * 是否是预告
	 * @return
	 */
	public boolean getIsNotice(){
		if(getStatus() >= StatusEnum.PROJECT_STATUS_INVESTING.getStatus()){
			return false;
		}
		return true;
	}
	
	public String getInvestmentProgress(){
		if(availableBalance != null){
			if(availableBalance.compareTo(BigDecimal.ZERO) <= 0){
				investmentProgress = "100";
			}else if(availableBalance.compareTo(totalAmount) == 0){
				investmentProgress = "0";
			}else{
				investmentProgress = new DecimalFormat("###.##").format((totalAmount.subtract(availableBalance)).divide(totalAmount,4,RoundingMode.HALF_UP).multiply(new BigDecimal("100")));
			}
		}
		return investmentProgress;
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
    
    public Date getCurrentDate() {
		return DateUtils.getCurrentDate();
	}

	public Integer getAppRecommend() {
		return appRecommend;
	}

	public void setAppRecommend(Integer appRecommend) {
		this.appRecommend = appRecommend;
	}
    
	@JSONField(serialize=false)
	public boolean getIsAppRecommend(){
		if(getAppRecommend()== null || getAppRecommend() <= 0){
			return false;
		}
		return true;
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

	public Integer getInvestType() {
		return investType;
	}

	public void setInvestType(Integer investType) {
		this.investType = investType;
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
	
	public boolean getIsDirectProject(){
        if (this.investType == ProjectEnum.PROJECT_TYPE_DIRECT.getType()){
            return true;
        }
        return false;
    }

	/**
	 * @return the minInvestAmount
	 */
	public BigDecimal getMinInvestAmount() {
		return minInvestAmount;
	}

	/**
	 * @param minInvestAmount the minInvestAmount to set
	 */
	public void setMinInvestAmount(BigDecimal minInvestAmount) {
		this.minInvestAmount = minInvestAmount;
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
	
}
