package com.yourong.core.ic.model;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.enums.ProjectEnum;
import com.yourong.common.enums.StatusEnum;
import com.yourong.common.enums.TypeEnum;


public class ProjectPackageLinkModel{
	/**项目id**/
	private Long id;
	/**项目名称**/
	private String name;
	/**投资总金额**/
	private BigDecimal totalAmount;
	/**年化收益***/
    private BigDecimal annualizedRate;
    /**额外项目收益**/
    private BigDecimal extraAmount;
    /***借款周期*****/
    private Integer borrowPeriod;
    /** 借款周期类型（1-日；2-月；3-年） **/
	private Integer borrowPeriodType;
    /**收益类型**/
    private String profitType;
    /**上线时间(年月日时分秒)**/
    private Date onlineTime;
    /**销售截止日期(年月日时分秒)**/
    private Date saleEndTime;
    /**项目销售完成时间*/
    private Date saleComplatedTime;
    /**项目状态**/
    private Integer status;
    /**项目进度**/
    private String progress;
    /**剩余可投金额**/
    private BigDecimal voteAmount;
    /***上线截止日期**/
	private int endTime;
	/**当前时间**/
	private Date currentDate;
	/**项目缩略图***/
	private String thumbnail;
	
	
	public Date getCurrentDate() {
		return currentDate;
	}
	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public int getEndTime() {
		return endTime;
	}
	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}
	public BigDecimal getVoteAmount() {
		return voteAmount;
	}
	public void setVoteAmount(BigDecimal voteAmount) {
		this.voteAmount = voteAmount;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getProgress() {
		return progress;
	}
	public void setProgress(String progress) {
		this.progress = progress;
	}
	public BigDecimal getExtraAmount() {
		return extraAmount;
	}
	public void setExtraAmount(BigDecimal extraAmount) {
		this.extraAmount = extraAmount;
	}
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
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public BigDecimal getAnnualizedRate() {
		return annualizedRate;
	}
	public void setAnnualizedRate(BigDecimal annualizedRate) {
		this.annualizedRate = annualizedRate;
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
	public String getProfitType() {
		return profitType;
	}
	public void setProfitType(String profitType) {
		this.profitType = profitType;
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
	public Date getSaleComplatedTime() {
		return saleComplatedTime;
	}
	public void setSaleComplatedTime(Date saleComplatedTime) {
		this.saleComplatedTime = saleComplatedTime;
	}
	/** 项目类型 **/
	private Integer projectCategory = TypeEnum.PROJECT_CATEGORY_NORMAL.getType();
	public Integer getProjectCategory() {
		return projectCategory;
	}
	public void setProjectCategory(Integer projectCategory) {
		this.projectCategory = projectCategory;
	}
	private boolean isDirectProject;
	
    /**投资类型（1-债权；2-直投）*/
    private Integer investType;
    
	public Integer getInvestType() {
		return investType;
	}
	public void setInvestType(Integer investType) {
		this.investType = investType;
	}
	public boolean isDirectProject() {
		if(ProjectEnum.PROJECT_TYPE_DIRECT.getType()==getInvestType()){
			return true;
		}
		return false;
	}

	public void setDirectProject(boolean isDirectProject) {
		this.isDirectProject = isDirectProject;
	}
	/**
	 * 根据状态获得按钮文本
	 * 
	 * @return
	 */
	public String getButtonText() {
		
		if (getProjectCategory() != null && getProjectCategory() == TypeEnum.PROJECT_CATEGORY_TRANSFER.getType()) {
			if (getStatus() == StatusEnum.TRANSFER_PROJECT_STATUS_INVESTING.getStatus()) {
				return "立即认购";
			} else if(getStatus() == StatusEnum.TRANSFER_PROJECT_STATUS_LOAN.getStatus()){
				return "履约中";
			}else if (getStatus() == StatusEnum.TRANSFER_PROJECT_STATUS_REPAYMENT.getStatus()) {
				return "已还款";
			} 
		}
		if (isDirectProject()) {
			if (getStatus() == StatusEnum.PROJECT_STATUS_INVESTING.getStatus()) {
				return "立即投资";
			} else if (getStatus() == StatusEnum.PROJECT_STATUS_FULL.getStatus()
					|| (getStatus() == StatusEnum.PROJECT_STATUS_LOSING.getStatus() && saleComplatedTime != null )
					||getStatus() == StatusEnum.PROJECT_STATUS_AUTH_PASS.getStatus()) {
				return "已投满";
			} else if (getStatus() == StatusEnum.PROJECT_STATUS_STOP.getStatus()) {
				return StatusEnum.PROJECT_STATUS_STOP.getDesc();
			} else if (getStatus() == StatusEnum.PROJECT_STATUS_HAD_LOAN.getStatus()
					||getStatus() == StatusEnum.PROJECT_STATUS_WAIT_LOAN.getStatus()
					) {
				return "履约中";
			} else if (getStatus() == StatusEnum.PROJECT_STATUS_END.getStatus() 
					||(getStatus() == StatusEnum.PROJECT_STATUS_LOSING.getStatus() && saleComplatedTime == null )) {
				return StatusEnum.PROJECT_STATUS_END.getDesc();
			} else if (getStatus() == StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()) {
				return StatusEnum.PROJECT_STATUS_REPAYMENT.getDesc();
			} else if (getStatus() == StatusEnum.PROJECT_STATUS_LOSE.getStatus()) {
				return StatusEnum.PROJECT_STATUS_LOSE.getDesc();
			}
		}else {
			if (status == StatusEnum.PROJECT_STATUS_INVESTING.getStatus()) {
				return "立即投资";
			} else if (status == StatusEnum.PROJECT_STATUS_STOP.getStatus()) {
				return StatusEnum.PROJECT_STATUS_STOP.getDesc();
			} else if (status == StatusEnum.PROJECT_STATUS_FULL.getStatus()
					|| status == StatusEnum.PROJECT_STATUS_END.getStatus()) {
				return "履约中";
			} else if (status == StatusEnum.PROJECT_STATUS_REPAYMENT.getStatus()) {
				return "已还款";
			}
		}
		return "";
	}
}