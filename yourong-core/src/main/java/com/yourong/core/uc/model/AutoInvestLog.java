package com.yourong.core.uc.model;

import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;

/**
 * 
 * @Description 自动投标记录model
 * @author luwenshan
 * @time 2016年8月17日
 *
 */
public class AutoInvestLog extends AbstractBaseObject{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -3327549070884577360L;

	/****/
    private Long id;
    
    /**自动投标设置id**/
    private Long autoInvestId;
    
    /**会员id**/
    private Long memberId;
    
    /** 交易记录id **/
    private Long transactionId;
    
    /**项目id**/
    private Long projectId;
    
    /**订单ID**/
    private Long orderId;
    
    /**1:已投标 0:未投标**/
    private Integer status;
    
    /**自动投标设置信息json字符串**/
    private String autoInvestSetInfo;
    
    /**备注(原因)**/
    private String remarks;

    /**更新时间**/
    private Date updateTime;

    /**创建时间**/
    private Date createTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAutoInvestId() {
		return autoInvestId;
	}

	public void setAutoInvestId(Long autoInvestId) {
		this.autoInvestId = autoInvestId;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getAutoInvestSetInfo() {
		return autoInvestSetInfo;
	}

	public void setAutoInvestSetInfo(String autoInvestSetInfo) {
		this.autoInvestSetInfo = autoInvestSetInfo;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
    
}