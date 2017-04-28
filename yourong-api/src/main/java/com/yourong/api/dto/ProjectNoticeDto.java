package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.yourong.common.constant.Config;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.DateUtils;
import com.yourong.common.util.StringUtil;

public class ProjectNoticeDto {
	
	/**项目编号**/
	@JSONField(name="pid")
	private Long projectId;
	
	private String name;
	/**投资总金额**/
	private BigDecimal totalAmount;
	 /**起息日，T+1则存1**/
	private Integer interestFrom;
    /**上线时间(年月日时分秒)**/
    private Date onlineTime;
	 /**最小收益**/
    private BigDecimal minAnnualizedRate;
    /**最大收益**/
    private BigDecimal maxAnnualizedRate;
    /**服务器当前时间**/
    private Date currentDate;
    
    private Date endDate;
    
    private String thumbnail;
    
    private int earningsDays;
    
	/**项目模式类型*/
   	private Integer investType;
    
    /** 就否新手项目 **/
    @JSONField(serialize=false)
   	private Integer isNovice;

   	/** 是否参与租赁分红 **/
    @JSONField(serialize=false)
   	private Integer joinLease;
    
    /** 特殊活动标识*/
    private Integer activitySign;
    
    /**特殊业务标识*/
    private Integer extraType;
    
    /**加息数值*/
    private String addRate;
    
    /**借款周期**/
    private Integer borrowPeriod;

    /**借款周期类型（1-日；2-月；3-年）**/
    private Integer borrowPeriodType;
    
    /**借款周期**/
    private String ProfitPeriod ;
    
    private boolean isDirectProject;
    
    private Integer transferFlag;
    
    private Integer quickRewardFlag;
    
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public Integer getInterestFrom() {
		return interestFrom;
	}
	public void setInterestFrom(Integer interestFrom) {
		this.interestFrom = interestFrom;
	}
	public Date getOnlineTime() {
		return onlineTime;
	}
	public void setOnlineTime(Date onlineTime) {
		this.onlineTime = onlineTime;
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
	public Date getCurrentDate() {
		return DateUtils.getCurrentDate();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getThumbnail() {
		if(thumbnail != null){
			if(getProjectId() != null && getProjectId() >= 989800156L){
				return Config.ossPicUrl+StringUtil.getFilePath(thumbnail,"300");
			}else{
				return Config.ossPicUrl+thumbnail;
			}
		}
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public int getEarningsDays() {
		return  DateUtils.getIntervalDays(DateUtils.formatDate(getOnlineTime(), DateUtils.DATE_FMT_3), getEndDate())+1 - getInterestFrom();
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
    
	public String getProfitPeriod() {
		if(this.borrowPeriod!=null&&this.borrowPeriodType!=null){
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
		ProfitPeriod = profitPeriod;
	}
	public Integer getInvestType() {
		return investType;
	}
	public void setInvestType(Integer investType) {
		this.investType = investType;
	}
	
	public boolean getIsDirectProject() {
		if(getInvestType()==null){
			return false;
		}
		if(ProjectEnum.PROJECT_TYPE_DIRECT.getType()==getInvestType()){
			return true;
		}
		return false;
	}
	public void setDirectProject(boolean isDirectProject) {
		this.isDirectProject = isDirectProject;
	}
	public Integer getTransferFlag() {
		return transferFlag;
	}
	public void setTransferFlag(Integer transferFlag) {
		this.transferFlag = transferFlag;
	}
	public Integer getQuickRewardFlag() {
		return quickRewardFlag;
	}
	public void setQuickRewardFlag(Integer quickRewardFlag) {
		this.quickRewardFlag = quickRewardFlag;
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
	
//	/**
//	 * 预告的结束时间（秒）
//	 * @return
//	 * 
//	 */
//	public int getNoticeEndTime(){
//		return DateUtils.getTimeIntervalSencond(DateUtils.getCurrentDate(), getOnlineTime());
//	}
	
}
