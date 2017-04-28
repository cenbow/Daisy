package com.yourong.core.lottery.model;

import java.math.BigDecimal;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;

@SuppressWarnings("serial")
public class RuleForRedPackage extends AbstractBaseObject {

	/**
	 * 算法
	 */
	private Integer arithmeticType;

	/**
	 * 最小红包个数
	 */
	private Integer minNumber;

	/**
	 * 最大红包个数
	 */
	private Integer maxNumber;

	/**
	 * 允许的投资类型: 1-债权; 2-直投
	 */
	private List<Integer> allowInvestType;

	/**
	 * 是否允许新客
	 */
	private boolean allowNovise = true;

	/**
	 * 允许的活动项目标识
	 */
	private List<Integer> notAllowActivitySign;

	/**
	 * 允许的项目类型
	 */
	private List<String> allowProjectType;

	/**
	 * 订单来源：0:pc,1:andorid, 2:ios, 3:wap
	 */
	private List<Integer> allowOrderSource;

	/**
	 * 总额
	 */
	private String totalAmount;

	/**
	 * 起投金额(人气值红包对应单个红包的总额)
	 */
	private BigDecimal minInvestAmount;

	/**
	 * 最小人气值单位（人气值红包需要定义最小值）
	 */
	private Integer minPopularityUnit;

	/**
	 * 红包从创建以后多少分钟过期
	 */
	private Integer timeOutFromCreate;

	/**
	 * 微信分享朋友文案
	 */
	private String wechatShareFriends;

	/**
	 * 微信分享朋友圈文案
	 */
	private String wechatShareCircle;

	public Integer getArithmeticType() {
		return arithmeticType;
	}

	public void setArithmeticType(Integer arithmeticType) {
		this.arithmeticType = arithmeticType;
	}

	public Integer getMinNumber() {
		return minNumber;
	}

	public void setMinNumber(Integer minNumber) {
		this.minNumber = minNumber;
	}

	public Integer getMaxNumber() {
		return maxNumber;
	}

	public void setMaxNumber(Integer maxNumber) {
		this.maxNumber = maxNumber;
	}

	public List<Integer> getAllowInvestType() {
		return allowInvestType;
	}

	public void setAllowInvestType(List<Integer> allowInvestType) {
		this.allowInvestType = allowInvestType;
	}

	public boolean isAllowNovise() {
		return allowNovise;
	}

	public void setAllowNovise(boolean allowNovise) {
		this.allowNovise = allowNovise;
	}

	public List<Integer> getNotAllowActivitySign() {
		return notAllowActivitySign;
	}

	public void setNotAllowActivitySign(List<Integer> notAllowActivitySign) {
		this.notAllowActivitySign = notAllowActivitySign;
	}

	public List<String> getAllowProjectType() {
		return allowProjectType;
	}

	public void setAllowProjectType(List<String> allowProjectType) {
		this.allowProjectType = allowProjectType;
	}

	public List<Integer> getAllowOrderSource() {
		return allowOrderSource;
	}

	public void setAllowOrderSource(List<Integer> allowOrderSource) {
		this.allowOrderSource = allowOrderSource;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getMinInvestAmount() {
		return minInvestAmount;
	}

	public void setMinInvestAmount(BigDecimal minInvestAmount) {
		this.minInvestAmount = minInvestAmount;
	}

	public Integer getMinPopularityUnit() {
		return minPopularityUnit;
	}

	public void setMinPopularityUnit(Integer minPopularityUnit) {
		this.minPopularityUnit = minPopularityUnit;
	}

	public Integer getTimeOutFromCreate() {
		return timeOutFromCreate;
	}

	public void setTimeOutFromCreate(Integer timeOutFromCreate) {
		this.timeOutFromCreate = timeOutFromCreate;
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
