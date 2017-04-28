package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 
 * @desc 人气值红包规则类
 * @author wangyanji 2016年1月7日下午6:48:35
 */
public class RedBagRuleBiz extends AbstractBaseObject {
	/**
	 * 最低投资额
	 */
	private BigDecimal minInvestAmount;

	/**
	 * 不参加抢红包的特殊项目标记
	 */
	private int[] exceptActivitySign;

	private Date startTime;

	private Date endTime;

	public BigDecimal getMinInvestAmount() {
		return minInvestAmount;
	}

	public void setMinInvestAmount(BigDecimal minInvestAmount) {
		this.minInvestAmount = minInvestAmount;
	}

	public int[] getExceptActivitySign() {
		return exceptActivitySign;
	}

	public void setExceptActivitySign(int[] exceptActivitySign) {
		this.exceptActivitySign = exceptActivitySign;
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

}
