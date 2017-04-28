package com.yourong.core.uc.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员签到对象
 * @author Leon Ray
 * 2015年3月18日-下午5:23:50
 */
public class MemberCheck {
    /****/
    private Long id;

    /**用户id**/
    private Long memberId;

    /**签到时间**/
    private Date checkDate;

    /**签到所或人气值**/
    private BigDecimal gainPopularity;

    /**登录来源 0-pc 1-android  2-ios 3-wap 5-其他**/
    private Integer checkSource;

    /****/
    private Date createTime;
    
    /**签到倍数**/
    private int popularityDouble = 1;
    
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
	
	
	public Date getCheckDate() {
		return checkDate;
	}
	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}
	public BigDecimal getGainPopularity() {
		return gainPopularity;
	}
	public void setGainPopularity(BigDecimal gainPopularity) {
		this.gainPopularity = gainPopularity;
	}
	public Integer getCheckSource() {
		return checkSource;
	}
	public void setCheckSource(Integer checkSource) {
		this.checkSource = checkSource;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public int getPopularityDouble() {
		return popularityDouble;
	}
	public void setPopularityDouble(int popularityDouble) {
		this.popularityDouble = popularityDouble;
	}

	
   
}