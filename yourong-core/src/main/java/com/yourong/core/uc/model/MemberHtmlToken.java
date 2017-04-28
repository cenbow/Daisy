package com.yourong.core.uc.model;

import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;

public class MemberHtmlToken extends AbstractBaseObject {

	 /****/
    private Long id;

    /**用户ID**/
    private Long memberId;

    /**令牌**/
    private String token;

    /**创建时间**/
    private Date createTime;
    
    /**更新时间**/
    private Date updateTime;

    /**删除标志**/
    private Integer delFlag;

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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}
	
}
