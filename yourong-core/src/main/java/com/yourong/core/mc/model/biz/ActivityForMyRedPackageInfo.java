package com.yourong.core.mc.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 
 * @desc 红包适配页model
 * @author wangyanji 2016年1月20日下午4:54:27
 */
public class ActivityForMyRedPackageInfo extends AbstractBaseObject {

	private Integer receiveTotalPrize;

	private Integer sendTotalPrize;

	private Integer totalTop;

	private Integer platformTotalSend;

	private Integer platformTotalReceive;
	
	
	private String avatarImg;

	public Integer getReceiveTotalPrize() {
		return receiveTotalPrize;
	}

	public void setReceiveTotalPrize(Integer receiveTotalPrize) {
		this.receiveTotalPrize = receiveTotalPrize;
	}

	public Integer getSendTotalPrize() {
		return sendTotalPrize;
	}

	public void setSendTotalPrize(Integer sendTotalPrize) {
		this.sendTotalPrize = sendTotalPrize;
	}

	public Integer getTotalTop() {
		return totalTop;
	}

	public void setTotalTop(Integer totalTop) {
		this.totalTop = totalTop;
	}

	public Integer getPlatformTotalSend() {
		return platformTotalSend;
	}

	public void setPlatformTotalSend(Integer platformTotalSend) {
		this.platformTotalSend = platformTotalSend;
	}

	public Integer getPlatformTotalReceive() {
		return platformTotalReceive;
	}

	public void setPlatformTotalReceive(Integer platformTotalReceive) {
		this.platformTotalReceive = platformTotalReceive;
	}

	public String getAvatarImg() {
		return avatarImg;
	}

	public void setAvatarImg(String avatarImg) {
		this.avatarImg = avatarImg;
	}

}
