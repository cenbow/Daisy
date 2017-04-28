package com.yourong.core.mc.model.biz;

import java.math.BigDecimal;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.tc.model.biz.TransactionForFirstInvestAct;

/**
 * 领取福袋
 * @author chaisen
 *
 */
public class ActivityForFiveBillion extends AbstractBaseObject {
	
	private List<Integer> couponAmount;
	
	private List<Long> templateId;
	
	/**
	 * 收益券模板id集合
	 */
	private List<Long> incomeTemplateId;
	
	private Long templateId15;
	
	private int days;
	
	private int number;
	
	private BigDecimal firstRegisterAmount;
	
	private BigDecimal investAmount;
	
	//福禄双全概率
	private List<Double> probabilityLuckBoth;
	
	//福禄齐天概率
	private List<Double> probabilityLuckMonkey;
	
	
	private List<Long> luckBothTemplateId;
	
	
	private List<Long> luckMonkeyTemplateId;
	
	
	private List<String> luckBothRewardInfo;
	
	
	private List<String> luckMonkeyRewardInfo;
	
	
	

	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * @return the luckMonkeyTemplateId
	 */
	public List<Long> getLuckMonkeyTemplateId() {
		return luckMonkeyTemplateId;
	}

	/**
	 * @param luckMonkeyTemplateId the luckMonkeyTemplateId to set
	 */
	public void setLuckMonkeyTemplateId(List<Long> luckMonkeyTemplateId) {
		this.luckMonkeyTemplateId = luckMonkeyTemplateId;
	}

	/**
	 * @return the luckMonkeyRewardInfo
	 */
	public List<String> getLuckMonkeyRewardInfo() {
		return luckMonkeyRewardInfo;
	}

	/**
	 * @param luckMonkeyRewardInfo the luckMonkeyRewardInfo to set
	 */
	public void setLuckMonkeyRewardInfo(List<String> luckMonkeyRewardInfo) {
		this.luckMonkeyRewardInfo = luckMonkeyRewardInfo;
	}

	/**
	 * @return the luckBothRewardInfo
	 */
	public List<String> getLuckBothRewardInfo() {
		return luckBothRewardInfo;
	}

	/**
	 * @param luckBothRewardInfo the luckBothRewardInfo to set
	 */
	public void setLuckBothRewardInfo(List<String> luckBothRewardInfo) {
		this.luckBothRewardInfo = luckBothRewardInfo;
	}

	/**
	 * @return the luckBothTemplateId
	 */
	public List<Long> getLuckBothTemplateId() {
		return luckBothTemplateId;
	}

	/**
	 * @param luckBothTemplateId the luckBothTemplateId to set
	 */
	public void setLuckBothTemplateId(List<Long> luckBothTemplateId) {
		this.luckBothTemplateId = luckBothTemplateId;
	}

	/**
	 * @return the probabilityLuckBoth
	 */
	public List<Double> getProbabilityLuckBoth() {
		return probabilityLuckBoth;
	}

	/**
	 * @param probabilityLuckBoth the probabilityLuckBoth to set
	 */
	public void setProbabilityLuckBoth(List<Double> probabilityLuckBoth) {
		this.probabilityLuckBoth = probabilityLuckBoth;
	}

	/**
	 * @return the probabilityLuckMonkey
	 */
	public List<Double> getProbabilityLuckMonkey() {
		return probabilityLuckMonkey;
	}

	/**
	 * @param probabilityLuckMonkey the probabilityLuckMonkey to set
	 */
	public void setProbabilityLuckMonkey(List<Double> probabilityLuckMonkey) {
		this.probabilityLuckMonkey = probabilityLuckMonkey;
	}

	public BigDecimal getFirstRegisterAmount() {
		return firstRegisterAmount;
	}

	public void setFirstRegisterAmount(BigDecimal firstRegisterAmount) {
		this.firstRegisterAmount = firstRegisterAmount;
	}

	/**
	 * @return the investAmount
	 */
	public BigDecimal getInvestAmount() {
		return investAmount;
	}

	/**
	 * @param investAmount the investAmount to set
	 */
	public void setInvestAmount(BigDecimal investAmount) {
		this.investAmount = investAmount;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public Long getTemplateId15() {
		return templateId15;
	}

	public void setTemplateId15(Long templateId15) {
		this.templateId15 = templateId15;
	}

	public List<Integer> getCouponAmount() {
		return couponAmount;
	}

	public void setCouponAmount(List<Integer> couponAmount) {
		this.couponAmount = couponAmount;
	}

	
	public List<Long> getTemplateId() {
		return templateId;
	}

	
	public void setTemplateId(List<Long> templateId) {
		this.templateId = templateId;
	}

	public List<Long> getIncomeTemplateId() {
		return incomeTemplateId;
	}

	public void setIncomeTemplateId(List<Long> incomeTemplateId) {
		this.incomeTemplateId = incomeTemplateId;
	}
	
	
	
	
	
	
	
	
	
}
