package com.yourong.core.os.biz;

import com.yourong.common.domain.AbstractBaseObject;

public class TransactionsForOpen extends AbstractBaseObject {

	private String subscribeUserName;
	private Double amount;
	private Double validAmount;
	private String addDate;
	private Integer status;
	private Integer type;
	private Integer sourceType;

	public String getSubscribeUserName() {
		return subscribeUserName;
	}

	public void setSubscribeUserName(String subscribeUserName) {
		this.subscribeUserName = subscribeUserName;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getValidAmount() {
		return validAmount;
	}

	public void setValidAmount(Double validAmount) {
		this.validAmount = validAmount;
	}

	public String getAddDate() {
		return addDate;
	}

	public void setAddDate(String addDate) {
		this.addDate = addDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getSourceType() {
		return sourceType;
	}

	public void setSourceType(Integer sourceType) {
		this.sourceType = sourceType;
	}

}
