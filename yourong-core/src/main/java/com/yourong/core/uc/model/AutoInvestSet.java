package com.yourong.core.uc.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.Past;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.domain.AbstractBaseObject;

public class AutoInvestSet extends AbstractBaseObject{
	
	private static final long serialVersionUID = 5746379109990570174L;
    /****/
    private Long id;

    /**会员id**/
    private Long memberId;

    /**项目类型**/
    private String projectType;

    /** 投标标识（1-开启；2-关闭）**/
    private Integer investFlag; 
    
    /**投标频率**/
    private Integer investFrequency;
    
    /**投标金额**/
    private BigDecimal amount;
    
    /**有效期开始时间**/
    @Past
    @DateTimeFormat( pattern = "yyyy-MM-dd")
    private Date startTime;
    
    /**有效期结束时间**/
    @Past
    @DateTimeFormat( pattern = "yyyy-MM-dd")
    private Date endTime;
    
    /**项目周期开始**/
    private Integer periodMin;
    
    /**项目周期结束**/
    private Integer periodMax;
    
    /**排名时间**/
    private Date sortTime;
    
    /**优惠券使用类型：0-不使用优惠券，1-优先使用收益最高优惠券,2-优先使用有效期最短优惠券**/
    private Integer couponType;
    
    /**备注**/
    private String remarks;

    /**更新时间**/
    private Date updateTime;

    /**创建时间**/
    private Date createTime;
    
    private Integer sort;
    
    private boolean  isOpen=false;
    
    
    private String startTimeStr;
    
    
    private String endTimeStr;
    

	public String getStartTimeStr() {
		return startTimeStr;
	}

	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}

	public String getEndTimeStr() {
		return endTimeStr;
	}

	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

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

	

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
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
	 public int  countPeriodMin(){
	    	int days =0;
	        if (periodMin == 1) {
	            days =  5;
	        }
	        if (periodMin == 2) {
	            days = 10;
	        }
	        if (periodMin == 3) {
	           days = 30;
	        }
	        if (periodMin == 4) {
		        days = 2   *  30 ;
		    }
	        if (periodMin == 5) {
		        days = 3   *  30 ;
		    }
	        if (periodMin == 6) {
		        days = 6   *  30 ;
		    }
	        if (periodMin == 7) {
		        days =   30 * 12;
		    }
	        if (periodMin == 8) {
		        days =   30 * 24;
		    }
	        return days;
	    }
	 
	 public int  countPeriodMax(){
	    	int days =0;
	        if (periodMax == 1) {
	            days =  5;
	        }
	        if (periodMax == 2) {
	            days = 10;
	        }
	        if (periodMax == 3) {
	           days = 30;
	        }
	        if (periodMax == 4) {
		        days = 2   *  30 ;
		    }
	        if (periodMax == 5) {
		        days = 3   *  30 ;
		    }
	        if (periodMax == 6) {
		        days = 6   *  30 ;
		    }
	        if (periodMax == 7) {
		        days =   30 * 12;
		    }
	        if (periodMax == 8) {
		        days =   30 * 24;
		    }
	        return days;
	    }
	public void setPeriodMax(Integer periodMax) {
		this.periodMax = periodMax;
	}

	public Date getSortTime() {
		return sortTime;
	}

	public void setSortTime(Date sortTime) {
		this.sortTime = sortTime;
	}

	public Integer getCouponType() {
		return couponType;
	}

	public void setCouponType(Integer couponType) {
		this.couponType = couponType;
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

	public Integer getInvestFrequency() {
		return investFrequency;
	}

	public void setInvestFrequency(Integer investFrequency) {
		this.investFrequency = investFrequency;
	}

    
   
    
}