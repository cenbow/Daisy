package com.yourong.core.mc.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 
 * @desc 人气值红包规则类
 * @author wangyanji 2016年1月7日下午6:48:35
 */
public class PopularityRedBagBiz extends AbstractBaseObject {
	/**
	 * 红包分享码
	 */
	private String redBagCode;

	/**
	 * 分享好友文案
	 */
	private String wechatShareFriends;

	/**
	 * 分享朋友圈文案
	 */
	private String wechatShareCircle;

	/**
	 * 是否活动
	 */
	private boolean isActivity = false;

	/**
	 * 订单状态
	 */
	private Integer orderStatus;

	public String getRedBagCode() {
		return redBagCode;
	}

	public void setRedBagCode(String redBagCode) {
		this.redBagCode = redBagCode;
	}

	public String getWechatShareFriends() {
		return wechatShareFriends;
	}

	public void setWechatShareFriends(String wechatShareFriends) {
		this.wechatShareFriends = wechatShareFriends;
	}

	public String getWechatShareCircle() {
		return wechatShareCircle;
	}

	public void setWechatShareCircle(String wechatShareCircle) {
		this.wechatShareCircle = wechatShareCircle;
	}

	public boolean isActivity() {
		return isActivity;
	}

	public void setActivity(boolean isActivity) {
		this.isActivity = isActivity;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

}
