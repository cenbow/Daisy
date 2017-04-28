package com.yourong.core.msg.model;

import java.util.Date;

public class Feedback {
	/****/
    private Long id;

    /**用户编号**/
    private Long memberId;
    
    /**来源(1:android、2:IOS)**/
    private Integer source;
    
    /**消息内容**/
    private String content;
    
    /**回复内容**/
    private String reply;
    
    /**回复处理状态**/
    private Integer replyStatus;
    
    /**反馈时间**/
    private Date createTime;
    
    /**回复时间**/
    private Date replyTime;
    
    private Integer delFlag;
    
    private Integer type;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	/**
	 * @return the type
	 */
	public Integer getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 * @return the reply
	 */
	public String getReply() {
		return reply;
	}

	/**
	 * @param reply the reply to set
	 */
	public void setReply(String reply) {
		this.reply = reply;
	}

	/**
	 * @return the replyStatus
	 */
	public Integer getReplyStatus() {
		return replyStatus;
	}

	/**
	 * @param replyStatus the replyStatus to set
	 */
	public void setReplyStatus(Integer replyStatus) {
		this.replyStatus = replyStatus;
	}

	/**
	 * @return the replyTime
	 */
	public Date getReplyTime() {
		return replyTime;
	}

	/**
	 * @param replyTime the replyTime to set
	 */
	public void setReplyTime(Date replyTime) {
		this.replyTime = replyTime;
	}
	
}
