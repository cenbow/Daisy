package com.yourong.core.mc.model.biz;

import java.io.Serializable;

import com.yourong.common.util.StringUtil;


/**
 * 红色星期五活动专题
 * @author Leon Ray
 * 2014年12月12日-下午2:35:02
 */
public class ActivityForRedFriday implements Serializable {

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
	 * 优惠券id
	 */
	private Long couponId;
	

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

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	public Long getCouponId() {
		return couponId;
	}

	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}

	
	
}
