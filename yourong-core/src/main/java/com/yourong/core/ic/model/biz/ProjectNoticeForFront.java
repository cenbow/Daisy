package com.yourong.core.ic.model.biz;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.DateUtils;

public class ProjectNoticeForFront extends AbstractBaseObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	/**项目编号**/
	private Long projectId;
	/**抵押类型**/
	private String projectType;
	/**投资总金额**/
	private BigDecimal totalAmount;
	 /**起息日，T+1则存1**/
	private Integer interestFrom;
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    /**上线时间(年月日时分秒)**/
    private Date onlineTime;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    /**销售截止日期(年月日时分秒)**/
    private Date saleEndTime;
    
    /**项目开始日期**/
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private Date startDate;
    
    /**还款时间**/
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private Date endDate;
    
	 /**最小收益**/
    private BigDecimal minAnnualizedRate;
    /**最大收益**/
    private BigDecimal maxAnnualizedRate;
    /**项目名称**/
    private String name;
    /**项目缩略图**/
    private String thumbnail;
    /** 就否新手项目 **/
   	private Integer isNovice;

   	/** 是否参与租赁分红 **/
   	private Integer joinLease;
   	
   	/**收益周期*/
   	private Integer borrowPeriod;
   	/**收益周期类型*/
   	private Integer borrowPeriodType;
   	/**项目模式类型*/
   	private Integer investType;
    
   	/** 是否活动标记 **/
   	private Integer activitySign;
   	
   	private Integer transferFlag;
   	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public String getProjectType() {
		return projectType;
	}
	public void setProjectType(String projectType) {
		this.projectType = projectType;
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
	public Date getSaleEndTime() {
		return saleEndTime;
	}
	public void setSaleEndTime(Date saleEndTime) {
		this.saleEndTime = saleEndTime;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getThumbnail() {
		return thumbnail;
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
	/**
	 * 收益总天数
	 * @return
	 */
	public Integer getEarningsTotalDays() {
		int day = DateUtils.getIntervalDays(getStartDate(), getEndDate())+1 - getInterestFrom();
		return day > 0 ? day : 0;
	}
	
	/**
	 * 获得预告项目的收益总天数
	 * @return
	 */
	public Integer getNoticeProjectEarningsTotalDays() {
		int day = DateUtils.getIntervalDays(DateUtils.formatDate(getOnlineTime(), DateUtils.DATE_FMT_3), getEndDate())+1 - getInterestFrom();
		return day > 0 ? day : 0;
	}
	
	public String getFormatTotalAmount(){
		if(totalAmount == null){
			return "";
		}
		return new DecimalFormat("###,###.##").format(totalAmount);
	}
	
	public String getFormatMinAnnualizedRate() {
		if(minAnnualizedRate == null){
			return "";
		}
		return new DecimalFormat("###.##").format(minAnnualizedRate);
	}
	
	public String getFormatMaxAnnualizedRate() {
		if(maxAnnualizedRate == null){
			return "";
		}
		return new DecimalFormat("###.##").format(maxAnnualizedRate);
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
	
	public boolean isDirectProject(){
		if(investType ==null){
			return false;
		}
		if(ProjectEnum.PROJECT_TYPE_DIRECT.getType()==investType){
			return true;
		}else{
			return false;
		}
	}
	
	public String getProfitPeriod() {
		if (borrowPeriod != null && borrowPeriodType != null) {
//			return borrowPeriod.toString() + (borrowPeriodType ==TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_DAY.getType() ? "天" 
//					: (borrowPeriodType == TypeEnum.DIRECT_PROJECT_BORROW_PERIOD_TYPE_MONTH.getType() ? "个月" : "年"));
			
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
	
	public Integer getActivitySign() {
		return activitySign;
	}
	
	public void setActivitySign(Integer activitySign) {
		this.activitySign = activitySign;
	}
	public Integer getTransferFlag() {
		return transferFlag;
	}
	public void setTransferFlag(Integer transferFlag) {
		this.transferFlag = transferFlag;
	}
	
}
