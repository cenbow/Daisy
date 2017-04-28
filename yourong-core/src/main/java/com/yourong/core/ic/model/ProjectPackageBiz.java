package com.yourong.core.ic.model;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.domain.AbstractBaseObject;

public class ProjectPackageBiz extends AbstractBaseObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    /**
     * 项目包项目关联表ID
     */
	private Long id;
	/**项目包ID**/
    private Long projectPackageId;

    /**项目ID***/
    private Long  projectId;
    /**项目类型编码**/
    private String originalProjectNumber;

    /**项目名称**/
    private String name;

    /**收益类型-起息方式**/
    private String profitType;

    /**借款金额**/
    private BigDecimal totalAmount;
    
    /**借款人姓名**/
    private String borrowerName;
    
    /**起息方式(1-阶梯收益， 2-利率随递增额递增)**/
    private Integer annualizedRateType;
    /**年化收益***/
    private BigDecimal annualizedRate;
    /***赠送收益***/
    private BigDecimal annualizedTotalRate; 
    /***借款周期*****/
    private Integer borrowPeriod;
    
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    /**上线时间(年月日时分秒)**/
    private Date onlineTime;
    
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    /**销售截止时间**/
    private Date saleEndTime;
    
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    /**项目销售完成时间*/
    private Date saleComplatedTime;
    
    /***项目剩余金额*****/
    private BigDecimal surplusAmount;
    /**
     * 状态
     */
    private Integer status;
    /**项目进度**/
    private String progress;
    /**募集周期 **/
    private String salePeriod;
    
	/** 借款周期类型（1-日；2-月；3-年） **/
	private Integer borrowPeriodType;
	/**起息方式**/
	private Integer interestFrom;
	/**资产包名称**/
	private String projectPackageName;
	/**资产包描述**/
	private String description;
	/**最小收益范围**/
	private BigDecimal minAnnualizedArea;
	/**最大收益范围**/
	private BigDecimal maxAnnualizedArea;
	
	/**最大借款周期***/
	private Integer maxBorrowPeriod;
	/**最大借款周期类型***/
	private Integer maxBorrowPeriodType;
	/**最小借款周期范围***/
	private Integer minBorrowPeriod;
	/**最大借款周期范围***/
	private Integer minBorrowPeriodType;
	/***上线截止日期**/
	private int endTime;
	

	public int getEndTime() {
		return endTime;
	}
	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}
	public Integer getMaxBorrowPeriod() {
		return maxBorrowPeriod;
	}
	public void setMaxBorrowPeriod(Integer maxBorrowPeriod) {
		this.maxBorrowPeriod = maxBorrowPeriod;
	}
	public Integer getMaxBorrowPeriodType() {
		return maxBorrowPeriodType;
	}
	public void setMaxBorrowPeriodType(Integer maxBorrowPeriodType) {
		this.maxBorrowPeriodType = maxBorrowPeriodType;
	}
	public Integer getMinBorrowPeriod() {
		return minBorrowPeriod;
	}
	public void setMinBorrowPeriod(Integer minBorrowPeriod) {
		this.minBorrowPeriod = minBorrowPeriod;
	}
	public Integer getMinBorrowPeriodType() {
		return minBorrowPeriodType;
	}
	public void setMinBorrowPeriodType(Integer minBorrowPeriodType) {
		this.minBorrowPeriodType = minBorrowPeriodType;
	}
	public BigDecimal getMinAnnualizedArea() {
		return minAnnualizedArea;
	}
	public void setMinAnnualizedArea(BigDecimal minAnnualizedArea) {
		this.minAnnualizedArea = minAnnualizedArea;
	}
	public BigDecimal getMaxAnnualizedArea() {
		return maxAnnualizedArea;
	}
	public void setMaxAnnualizedArea(BigDecimal maxAnnualizedArea) {
		this.maxAnnualizedArea = maxAnnualizedArea;
	}
	public BigDecimal getAnnualizedRate() {
		return annualizedRate;
	}
	public void setAnnualizedRate(BigDecimal annualizedRate) {
		this.annualizedRate = annualizedRate;
	}

	public BigDecimal getAnnualizedTotalRate() {
		return annualizedTotalRate;
	}
	public void setAnnualizedTotalRate(BigDecimal annualizedTotalRate) {
		this.annualizedTotalRate = annualizedTotalRate;
	}
	public String getProjectPackageName() {
		return projectPackageName;
	}
	public void setProjectPackageName(String projectPackageName) {
		this.projectPackageName = projectPackageName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getInterestFrom() {
		return interestFrom;
	}
	public void setInterestFrom(Integer interestFrom) {
		this.interestFrom = interestFrom;
	}
	public Integer getBorrowPeriodType() {
		return borrowPeriodType;
	}
	public void setBorrowPeriodType(Integer borrowPeriodType) {
		this.borrowPeriodType = borrowPeriodType;
	}
	public String getSalePeriod() {
		return salePeriod;
	}
	public void setSalePeriod(String salePeriod) {
		this.salePeriod = salePeriod;
	}
	public String getProgress() {
		return progress;
	}
	public void setProgress(String progress) {
		this.progress = progress;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Long getProjectPackageId() {
		return projectPackageId;
	}
	public void setProjectPackageId(Long projectPackageId) {
		this.projectPackageId = projectPackageId;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public Date getSaleComplatedTime() {
		return saleComplatedTime;
	}
	public void setSaleComplatedTime(Date saleComplatedTime) {
		this.saleComplatedTime = saleComplatedTime;
	}
	
	public Date getSaleEndTime() {
		return saleEndTime;
	}
	public void setSaleEndTime(Date saleEndTime) {
		this.saleEndTime = saleEndTime;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getOriginalProjectNumber() {
		return originalProjectNumber;
	}
	public void setOriginalProjectNumber(String originalProjectNumber) {
		this.originalProjectNumber = originalProjectNumber;
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
	public String getBorrowerName() {
		return borrowerName;
	}
	public void setBorrowerName(String borrowerName) {
		this.borrowerName = borrowerName;
	}

	public Integer getAnnualizedRateType() {
		return annualizedRateType;
	}
	public void setAnnualizedRateType(Integer annualizedRateType) {
		this.annualizedRateType = annualizedRateType;
	}

	
	
	public Integer getBorrowPeriod() {
		return borrowPeriod;
	}
	public void setBorrowPeriod(Integer borrowPeriod) {
		this.borrowPeriod = borrowPeriod;
	}
	public Date getOnlineTime() {
		return onlineTime;
	}
	public void setOnlineTime(Date onlineTime) {
		this.onlineTime = onlineTime;
	}
	public BigDecimal getSurplusAmount() {
		return surplusAmount;
	}
	public void setSurplusAmount(BigDecimal surplusAmount) {
		this.surplusAmount = surplusAmount;
	}
    
}
