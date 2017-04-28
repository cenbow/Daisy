package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.StringUtil;


/**
 * 单笔大额投资专题
 * @author Leon Ray
 * 2015年4月20日-下午1:22:01
 */
public class ActivityForSingleInvest  extends AbstractBaseObject{

	/**
	 * 会员id
	 */
	private Long memberId;
	
	/**
	 * 用户名
	 */
	private String username;
	
	/**
	 * 手机号
	 */
	private Long mobile;
	
	/**
	 * 人气值数量
	 */
	private BigDecimal popularityNum;
	
	/**
	 * 项目id
	 */
	private BigDecimal investAmount;

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getUsername() {
		return StringUtil.maskUserNameOrMobile(username, mobile);
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getMobile() {
		return mobile;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	public BigDecimal getPopularityNum() {
		return popularityNum;
	}

	public void setPopularityNum(BigDecimal popularityNum) {
		this.popularityNum = popularityNum;
	}

	public BigDecimal getInvestAmount() {
		return investAmount;
	}

	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}
	
	
}
