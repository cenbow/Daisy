package com.yourong.api.dto;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.Past;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.domain.AbstractBaseObject;

public class AutoInvestSetDto extends AbstractBaseObject{
	
	private static final long serialVersionUID = 5746379109990570174L;
    /****/
    private Long id;

    /**会员id**/
    private Long memberId;

    /**项目类型**/
    private String projectType;

    /** 投标标识（1-开启；2-关闭）**/
    private Integer investFlag;
    
    /**投标金额**/
    private BigDecimal amount;
    
    /**有效期开始时间**/
   // @Past
    @DateTimeFormat( pattern = "yyyy-MM-dd")
    private Date startTime;
    
    /**有效期结束时间**/
   // @Past
    @DateTimeFormat( pattern = "yyyy-MM-dd")
    private Date endTime;
    
    /**项目周期开始**/
    private Integer periodMin;
    
    /**项目周期结束**/
    private Integer periodMax;
    
    /**投标频率**/
    private Integer investFrequency;
    
    /**优惠券使用类型：0-不使用优惠券，1-优先使用收益最高优惠券,2-优先使用有效期最短优惠券**/
    private Integer couponType;
    
    /**排名时间**/
    private Date sortTime;
    
    private String remarks;

    /**更新时间**/
    private Date updateTime;

    /**创建时间**/
    private Date createTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getProjectType() {
		return projectType;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	public Integer getInvestFlag() {
		return investFlag;
	}

	public void setInvestFlag(Integer investFlag) {
		this.investFlag = investFlag;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	

	public Integer getPeriodMin() {
		return periodMin;
	}

	public void setPeriodMin(Integer periodMin) {
		this.periodMin = periodMin;
	}

	public Integer getPeriodMax() {
		return periodMax;
	}

	public void setPeriodMax(Integer periodMax) {
		this.periodMax = periodMax;
	}

	public Integer getInvestFrequency() {
		return investFrequency;
	}

	public void setInvestFrequency(Integer investFrequency) {
		this.investFrequency = investFrequency;
	}

	public Date getSortTime() {
		return sortTime;
	}

	public void setSortTime(Date sortTime) {
		this.sortTime = sortTime;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getCouponType() {
		return couponType;
	}

	public void setCouponType(Integer couponType) {
		this.couponType = couponType;
	}
    
}