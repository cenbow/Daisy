package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.enums.TypeEnum;
import com.yourong.common.util.DateUtils;

/**
 * 
 * @desc 红包
 * @author wangyanji 2016年1月20日下午4:54:27
 */
public class ActivityForRedBag extends AbstractBaseObject {

	/**
	 * 是否已经领取
	 */
	private boolean hasReceive = false;

	/**
	 * 是否已经领完
	 */
	private boolean hasEmpty = false;

	/**
	 * 是否异常显示繁忙
	 */
	private boolean hasException = false;

	/**
	 * 本次领取点数
	 */
	private BigDecimal rewardValue;

	/**
	 * 手机号
	 */
	private Long mobile;

	/**
	 * 掩饰的手机号
	 */
	private String mobileStr;

	/**
	 * 领取列表
	 */
	private List receiveList;

	/**
	 * 剩余总额
	 */
	private Integer residualAmount;

	/**
	 * 剩余抽取次数
	 */
	private Integer residualNumber;

	/**
	 * 是否领取
	 */
	private Integer receiveFlag;
	
	
	private Date receiveTime;

	/**
	 * 活动中参与的角色
	 */
	private String activityRole;

	/**
	 * banner图片地址
	 */
	private String image;

	/**
	 * banner链接
	 */
	private String href;

	/**
	 * 领取手机号
	 */
	private Long receive;
	
	public String getReceiveTimeStr() {
		if(receiveTime!=null){
			return DateUtils.getLongStrFromDate(receiveTime);
		}
		return "";
	}

	public boolean isHasReceive() {
		return hasReceive;
	}

	public void setHasReceive(boolean hasReceive) {
		this.hasReceive = hasReceive;
	}

	public BigDecimal getRewardValue() {
		return rewardValue;
	}

	public void setRewardValue(BigDecimal rewardValue) {
		this.rewardValue = rewardValue;
	}

	public boolean isHasEmpty() {
		return hasEmpty;
	}

	public void setHasEmpty(boolean hasEmpty) {
		this.hasEmpty = hasEmpty;
	}

	public boolean isHasException() {
		return hasException;
	}

	public void setHasException(boolean hasException) {
		this.hasException = hasException;
	}

	public Long getMobile() {
		return mobile;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	public String getMobileStr() {
		return mobileStr;
	}

	public void setMobileStr(String mobileStr) {
		this.mobileStr = mobileStr;
	}

	public List getReceiveList() {
		return receiveList;
	}

	public void setReceiveList(List receiveList) {
		this.receiveList = receiveList;
	}

	public Integer getResidualAmount() {
		return residualAmount;
	}

	public void setResidualAmount(Integer residualAmount) {
		this.residualAmount = residualAmount;
	}

	public Integer getResidualNumber() {
		return residualNumber;
	}

	public void setResidualNumber(Integer residualNumber) {
		this.residualNumber = residualNumber;
	}

	public Integer getReceiveFlag() {
		return receiveFlag;
	}

	public void setReceiveFlag(Integer receiveFlag) {
		this.receiveFlag = receiveFlag;
	}

	public boolean isMyPackage() {
		if (this.mobile != null) {
			return true;
		}
		return false;
	}

	public String getActivityRole() {
		return activityRole;
	}

	public void setActivityRole(String activityRole) {
		this.activityRole = activityRole;
	}

	public Date getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public Long getReceive() {
		return receive;
	}

	public void setReceive(Long receive) {
		this.receive = receive;
	}
}
