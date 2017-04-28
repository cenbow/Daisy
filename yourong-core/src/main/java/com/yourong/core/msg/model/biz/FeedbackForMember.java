package com.yourong.core.msg.model.biz;

import java.util.Date;

import org.apache.ibatis.type.Alias;

@Alias(value = "FeedbackForMember")
public class FeedbackForMember {
	/****/
    private Long id;

    /**用户编号**/
    private Long memberId;
    
    /**来源(1:android、2:IOS)**/
    private Integer source;
    
    /**消息内容**/
    private String content;
    
    /**反馈时间**/
    private Date createTime;
    
    /**回复内容**/
    private String reply;
    
    /**回复处理状态**/
    private Integer replyStatus;
    
    private Integer type;
    
    /**用户姓名**/
    private String trueName;
    
    private Integer sex;
    
    private Long mobile;

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

	public String getTrueName() {
		return trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Long getMobile() {
		return mobile;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
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
	
}
