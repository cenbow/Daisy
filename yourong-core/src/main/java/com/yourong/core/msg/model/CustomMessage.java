package com.yourong.core.msg.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.yourong.common.util.DateUtils;

public class CustomMessage {
	/****/
	private Long id;

	/** 消息类型：1:短信、2:邮件、3:站内信 **/
	private Integer msgType;

	/**
	 * 渠道商类型
	 */
	private Integer smsType;

	/** 状态： **/
	private Integer status;

	/** 发送时间 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date sendDate;

	/** 过期时间 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date invalidDate;

	/****/
	private Integer delFlag;

	/** 通知类型：0-其它, 1-系统公告，2-操作通知, 3-收益通知, 4-:活动通知 **/
	private Integer notifyType;

	/** 目标用户：1-所有用户、2-投资过的用户、3-项目用户、4-自定义用户 **/
	private Integer userType;

	/**
	 * 模板规则
	 */
	private String typeRule;

	/****/
	private String customAttr;

	/**
	 * 用户id文件地址
	 */
	private String customFileUrl;
	/** 信息名称 **/
	private String msgName;

	/** 创建者编号 **/
	private Long creatorId;

	/** 创建者名称 **/
	private String creatorName;
	
	/** 审核人编号 **/
	private Long auditId;

	/** 创建时间 **/
	private Date createTime;

	/** 更新时间 **/
	private Date updateTime;

	/** 审核时间 **/
	private Date auditTime;

	/** 消息内容 **/
	private String content;

	/*** 审核消息 **/
	private String auditMessage;
	
	private String  remark;
	
	/** 注册结束时间 **/
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date registerEndTime;
	
	/** 注册天数 **/
	private Integer registerDays;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMsgName() {
		return msgName;
	}

	public void setMsgName(String msgName) {
		this.msgName = msgName;
	}

	public Integer getMsgType() {
		return msgType;
	}

	public void setMsgType(Integer msgType) {
		this.msgType = msgType;
	}

	public Integer getSmsType() {
		return smsType;
	}

	public void setSmsType(Integer smsType) {
		this.smsType = smsType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public Date getInvalidDate() {
		return invalidDate;
	}

	public void setInvalidDate(Date invalidDate) {
		this.invalidDate = invalidDate;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public Integer getNotifyType() {
		return notifyType;
	}

	public void setNotifyType(Integer notifyType) {
		this.notifyType = notifyType;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public String getTypeRule() {
		return typeRule;
	}

	public void setTypeRule(String typeRule) {
		this.typeRule = typeRule;
	}

	public String getCustomAttr() {
		return customAttr;
	}

	public void setCustomAttr(String customAttr) {
		this.customAttr = customAttr == null ? null : customAttr.trim();
	}

	public String getCustomFileUrl() {
		return customFileUrl;
	}

	public void setCustomFileUrl(String customFileUrl) {
		this.customFileUrl = customFileUrl;
	}

	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	public Long getAuditId() {
		return auditId;
	}

	public void setAuditId(Long auditId) {
		this.auditId = auditId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getAuditTime() {
		return auditTime;
	}

	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content == null ? null : content.trim();
	}

	public String getAuditMessage() {
		return auditMessage;
	}

	public void setAuditMessage(String auditMessage) {
		this.auditMessage = auditMessage;
	}

	public String getSendDateStr() {
		if (this.sendDate != null) {
			return DateUtils.getStrFromDate(this.sendDate, "yyyy-MM-dd HH:mm");
		}
		return "";
	}

	public String getInvalidDateStr() {
		if (this.invalidDate != null) {
			return DateUtils.getStrFromDate(this.invalidDate,
					"yyyy-MM-dd HH:mm");
		}
		return "";
	}

	/**
	 * @return the creatorName
	 */
	public String getCreatorName() {
		return creatorName;
	}

	/**
	 * @param creatorName the creatorName to set
	 */
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getRegisterEndTime() {
		return registerEndTime;
	}

	public void setRegisterEndTime(Date registerEndTime) {
		this.registerEndTime = registerEndTime;
	}

	public Integer getRegisterDays() {
		return registerDays;
	}

	public void setRegisterDays(Integer registerDays) {
		this.registerDays = registerDays;
	}
	
	public String getRegisterEndTimeStr() {
		if (this.registerEndTime != null) {
			return DateUtils.getStrFromDate(this.registerEndTime, "yyyy-MM-dd HH:mm:ss");
		}
		return "";
	}
			
}