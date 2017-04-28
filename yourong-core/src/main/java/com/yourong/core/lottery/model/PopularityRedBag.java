package com.yourong.core.lottery.model;

import java.math.BigDecimal;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 
 * @desc 人气值红包规则类
 * @author wangyanji 2016年1月7日下午6:48:35
 */
public class PopularityRedBag extends AbstractBaseObject {

	/**
	 * 红包算法
	 */
	private String arithmetic;

	/**
	 * 不参加抢红包的特殊项目标记
	 */
	private int[] exceptActivitySign;

	/**
	 * 红包总额公式： 如：%5代表投资总额的5%
	 */
	private String totalAmount;

	/**
	 * 分裂数
	 */
	private int[] splitNum;

	/**
	 * 红包源
	 */
	private Long sourceId;

	/**
	 * 项目编号
	 */
	private Long projectId;

	/**
	 * 红包总额
	 */
	private BigDecimal redBagAmount;

	/**
	 * 最低投资额
	 */
	private BigDecimal minInvestAmount;

	/**
	 * 最终生成的红包个数
	 */
	private Long finalSplitNum;

	/**
	 * 红包备注
	 */
	private String redBagRemark;

	/**
	 * 分享好友文案
	 */
	private String wechatShareFriends;

	/**
	 * 分享朋友圈文案
	 */
	private String wechatShareCircle;

	public String getArithmetic() {
		return arithmetic;
	}

	public void setArithmetic(String arithmetic) {
		this.arithmetic = arithmetic;
	}

	public int[] getExceptActivitySign() {
		return exceptActivitySign;
	}

	public void setExceptActivitySign(int[] exceptActivitySign) {
		this.exceptActivitySign = exceptActivitySign;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public int[] getSplitNum() {
		return splitNum;
	}

	public void setSplitNum(int[] splitNum) {
		this.splitNum = splitNum;
	}

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public BigDecimal getRedBagAmount() {
		return redBagAmount;
	}

	public void setRedBagAmount(BigDecimal redBagAmount) {
		this.redBagAmount = redBagAmount;
	}

	public BigDecimal getMinInvestAmount() {
		return minInvestAmount;
	}

	public void setMinInvestAmount(BigDecimal minInvestAmount) {
		this.minInvestAmount = minInvestAmount;
	}

	public Long getFinalSplitNum() {
		return finalSplitNum;
	}

	public void setFinalSplitNum(Long finalSplitNum) {
		this.finalSplitNum = finalSplitNum;
	}

	public String getRedBagRemark() {
		return redBagRemark;
	}

	public void setRedBagRemark(String redBagRemark) {
		this.redBagRemark = redBagRemark;
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

}
