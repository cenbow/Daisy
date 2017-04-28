package com.yourong.core.tc.model;

import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;

public class HostingCollectTradeAuth extends AbstractBaseObject {

    /**编号**/
    private Long id;

    /**代收交易号**/
    private String tradeNo;

    /**单笔代收完成/撤销请求号**/
    private String tradeRequestNo;

    /**代收完成/撤销请求号**/
	private String batchRequestNo;

    /**操作类型：1 代收完成， 2 代收撤销**/
    private Integer authType;

    /**项目ID**/
    private Long projectId;

    /**创建时间**/
    private Date createTime;

    /**更新时间**/
    private Date updateTime;

    /**备注**/
    private String remark;
    
    
    private String userIp;

    /**删除标记**/
    private Integer delFlag;

    public Long getId() {
        return id;
    }

    public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	public void setId(Long id) {
        this.id = id;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo == null ? null : tradeNo.trim();
    }

    public String getTradeRequestNo() {
        return tradeRequestNo;
    }

    public void setTradeRequestNo(String tradeRequestNo) {
        this.tradeRequestNo = tradeRequestNo == null ? null : tradeRequestNo.trim();
    }

	public String getBatchRequestNo() {
		return batchRequestNo;
	}

	public void setBatchRequestNo(String batchRequestNo) {
		this.batchRequestNo = batchRequestNo;
	}

	public Integer getAuthType() {
        return authType;
    }

    public void setAuthType(Integer authType) {
        this.authType = authType;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }
}