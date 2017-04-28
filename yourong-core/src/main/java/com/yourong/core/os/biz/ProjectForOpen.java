package com.yourong.core.os.biz;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.common.util.StringUtil;

public class ProjectForOpen extends AbstractBaseObject {

	private String projectId;
	private String title;
	private Double amount;
	private String schedule;
	private String interestRate;
	private Integer deadline;
	private String deadlineUnit;
	private Double reward;
	private String type;
	private Integer repaymentType;
	private List<TransactionsForOpen> subscribes;
	private String userName;
	private String loanUrl;
	private String successTime;
	private String publishTime;
	private Integer minInvestAmount;

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		if (StringUtil.isNotBlank(schedule)) {
			this.schedule = new DecimalFormat("######.#").format(new BigDecimal(schedule)).toString();
		}
	}

	public String getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(String interestRate) {
		this.interestRate = interestRate;
	}

	public Integer getDeadline() {
		return deadline;
	}

	public void setDeadline(Integer deadline) {
		this.deadline = deadline;
	}

	public String getDeadlineUnit() {
		return deadlineUnit;
	}

	public void setDeadlineUnit(String deadlineUnit) {
		this.deadlineUnit = deadlineUnit;
	}

	public Double getReward() {
		return reward;
	}

	public void setReward(Double reward) {
		this.reward = reward;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getRepaymentType() {
		return repaymentType;
	}

	public void setRepaymentType(Integer repaymentType) {
		this.repaymentType = repaymentType;
	}

	public List<TransactionsForOpen> getSubscribes() {
		return subscribes;
	}

	public void setSubscribes(List<TransactionsForOpen> subscribes) {
		this.subscribes = subscribes;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public Integer getMinInvestAmount() {
		return minInvestAmount;
	}

	public void setMinInvestAmount(Integer minInvestAmount) {
		this.minInvestAmount = minInvestAmount;
	}

}
