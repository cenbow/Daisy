package com.yourong.core.sales;

import com.yourong.common.domain.AbstractBaseObject;

public class SPGift extends AbstractBaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8498764095852065272L;
	private Long templateId;
	private Integer number;
	private String type;
	private String remarks;
	private Integer value;
	/**
	 * 是否发送站内信
	 */
	private boolean sendMsgFlag;
	/**
	 * 站内信显示的奖品
	 */
	private String rewardName;
	
	public Long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public boolean isSendMsgFlag() {
		return sendMsgFlag;
	}
	public void setSendMsgFlag(boolean sendMsgFlag) {
		this.sendMsgFlag = sendMsgFlag;
	}
	public String getRewardName() {
		return rewardName;
	}
	public void setRewardName(String rewardName) {
		this.rewardName = rewardName;
	}
	
	
}
