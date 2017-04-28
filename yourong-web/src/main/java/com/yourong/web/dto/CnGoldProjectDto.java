package com.yourong.web.dto;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * 金投项目数据
 *
 */
public class CnGoldProjectDto {
	private Long productId;
	private String title;
	private String shortName;
	private Integer platformId;
	private BigDecimal amount;
	private String complete;
	private BigDecimal profit;
	private int deadline;
	private String deadlineUnit;
	private double reward;
	private String type;
	private int payOption;
	private List subscribes = Lists.newArrayList();
	private String province;
	private String city;
	private String userName;
	private String userAvatarUrl;
	private String amountUsedDesc;
	private String revenue;
	private String loanUrl;
	private String successTime;
	private String publishTime;
	
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getComplete() {
		return complete;
	}
	public void setComplete(String complete) {
		this.complete = complete;
	}
	public BigDecimal getProfit() {
		return profit;
	}
	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}
	public int getDeadline() {
		return deadline;
	}
	public void setDeadline(int deadline) {
		this.deadline = deadline;
	}
	public String getDeadlineUnit() {
		return deadlineUnit;
	}
	public void setDeadlineUnit(String deadlineUnit) {
		this.deadlineUnit = deadlineUnit;
	}
	public double getReward() {
		return reward;
	}
	public void setReward(double reward) {
		this.reward = reward;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getPayOption() {
		return payOption;
	}
	public void setPayOption(int payOption) {
		this.payOption = payOption;
	}
	public List getSubscribes() {
		return subscribes;
	}
	public void setSubscribes(List subscribes) {
		this.subscribes = subscribes;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserAvatarUrl() {
		return userAvatarUrl;
	}
	public void setUserAvatarUrl(String userAvatarUrl) {
		this.userAvatarUrl = userAvatarUrl;
	}
	public String getAmountUsedDesc() {
		return amountUsedDesc;
	}
	public void setAmountUsedDesc(String amountUsedDesc) {
		this.amountUsedDesc = amountUsedDesc;
	}
	public String getRevenue() {
		return revenue;
	}
	public void setRevenue(String revenue) {
		this.revenue = revenue;
	}
	public String getLoanUrl() {
		return loanUrl;
	}
	public void setLoanUrl(String loanUrl) {
		this.loanUrl = loanUrl;
	}
	public String getSuccessTime() {
		return successTime;
	}
	public void setSuccessTime(String successTime) {
		this.successTime = successTime;
	}
	public String getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}
	public Integer getPlatformId() {
		return platformId;
	}
	public void setPlatformId(Integer platformId) {
		this.platformId = platformId;
	}
	
	
}
