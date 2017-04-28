package com.yourong.core.uc.model.biz;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @Description 自动投标用户信息 
 * @author luwenshan
 * @time 2016年8月17日
 *
 */
public class AutoInvestMember{
	
	/**用户ID**/
    private Long memberId;

    /**身份证号码**/
    private String identityNumber;
    
    /**
	 * 设置支付密码标记（0-未设置（默认）；1-已设置）
	 */
	private Integer payPasswordFlag;
	
	/**
	 * 委托扣款授权（0否，1是）
	 */
	private Integer withholdAuthorityFlag;
	
	/**手机号**/    
    private Long mobile;
    
    /**会员注册IP**/
    private String registerIp;
    
    /**自动投标设置ID**/ 
    private Long autoInvestSetId;
	
	/** 投标标识（1-开启；2-关闭）**/
    private Integer investFlag;
    
    /**投标频率**/
    private Integer investFrequency;
    
    /**投标金额**/
    private BigDecimal amount;
    
    /**有效期开始时间**/
    private Date startTime;
    
    /**有效期结束时间**/
    private Date endTime;
    
    /**项目周期开始**/
    private Integer periodMin;
    
    /**项目周期结束**/
    private Integer periodMax;
    
    /**排名时间**/
    private Date sortTime;
    
    /**优惠券使用类型：1-优先使用收益最高优惠券,2-优先使用有效期最短优惠券**/
    private Integer couponType;
    
    /**项目类型**/
    private String projectType;

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getIdentityNumber() {
		return identityNumber;
	}

	public void setIdentityNumber(String identityNumber) {
		this.identityNumber = identityNumber;
	}

	public Integer getPayPasswordFlag() {
		return payPasswordFlag;
	}

	public void setPayPasswordFlag(Integer payPasswordFlag) {
		this.payPasswordFlag = payPasswordFlag;
	}

	public Integer getWithholdAuthorityFlag() {
		return withholdAuthorityFlag;
	}

	public void setWithholdAuthorityFlag(Integer withholdAuthorityFlag) {
		this.withholdAuthorityFlag = withholdAuthorityFlag;
	}

	public String getRegisterIp() {
		return registerIp;
	}

	public void setRegisterIp(String registerIp) {
		this.registerIp = registerIp;
	}

	public Long getMobile() {
		return mobile;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	public Long getAutoInvestSetId() {
		return autoInvestSetId;
	}

	public void setAutoInvestSetId(Long autoInvestSetId) {
		this.autoInvestSetId = autoInvestSetId;
	}

	public Integer getInvestFlag() {
		return investFlag;
	}

	public void setInvestFlag(Integer investFlag) {
		this.investFlag = investFlag;
	}

	public Integer getInvestFrequency() {
		return investFrequency;
	}

	public void setInvestFrequency(Integer investFrequency) {
		this.investFrequency = investFrequency;
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
	
	public int countPeriodMin(){
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

	public void setPeriodMin(Integer periodMin) {
		this.periodMin = periodMin;
	}

	public Integer getPeriodMax() {
		return periodMax;
	}
	
	public int countPeriodMax(){
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

	public String getProjectType() {
		return projectType;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}
    
}