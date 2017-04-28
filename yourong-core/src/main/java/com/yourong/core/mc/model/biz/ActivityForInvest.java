package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.FormulaUtil;

/**
 * 投资类活动biz
 * @author wangyanji
 *
 */
public class ActivityForInvest extends AbstractBaseObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 775484823636723166L;
	
	/** 会员ID**/
	private Long memberId;
	
	/** 投资金额 **/
	private BigDecimal investAmount;

	/** 用户名**/
	private String username;
	
	/** 会员头像**/
	private String avatar;
	
	/** 开始时间**/
	private Date startTime;
	
	/** 结束时间**/
	private Date endTime;
	
	/** 查询数量**/
	private Integer queryLimit;

	/** 债券类型**/
	private String[] guarantyType;

	/** 已发放优惠券**/
	private Integer sentCoupon;
	
	/** 剩余优惠券**/
	private Integer surplusCoupon;
	
	/**我的排名**/
	private Integer rankIndex;
	
	private Integer activityStatus;
	
	public BigDecimal getInvestAmount() {
		return investAmount;
	}

	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
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

	public Integer getQueryLimit() {
		return queryLimit;
	}

	public void setQueryLimit(Integer queryLimit) {
		this.queryLimit = queryLimit;
	}
	
	public String[] getGuarantyType() {
		return guarantyType;
	}

	public void setGuarantyType(String[] guarantyType) {
		this.guarantyType = guarantyType;
	}

	public Integer getSentCoupon() {
		return sentCoupon;
	}

	public void setSentCoupon(Integer sentCoupon) {
		this.sentCoupon = sentCoupon;
	}

	public Integer getSurplusCoupon() {
		return surplusCoupon;
	}

	public void setSurplusCoupon(Integer surplusCoupon) {
		this.surplusCoupon = surplusCoupon;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Integer getRankIndex() {
		return rankIndex;
	}

	public void setRankIndex(Integer rankIndex) {
		this.rankIndex = rankIndex;
	}
	
	public String getInvestAmountInteger() {
		if(this.investAmount == null) {
			return "0";
		} else {
			return FormulaUtil.getInteger(this.investAmount);
		}
	}

	public Integer getActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(Integer antivityStatus) {
		this.activityStatus = antivityStatus;
	}
}
