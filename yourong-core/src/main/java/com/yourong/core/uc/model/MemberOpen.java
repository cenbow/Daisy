package com.yourong.core.uc.model;

import java.io.Serializable;
import java.util.Date;

import com.yourong.common.util.StringUtil;

public class MemberOpen implements Serializable {
    /**
	 * 
	 */
  private static final long serialVersionUID = 4770786020681978665L;

	/****/
    private Long id;

    /**用户id**/
    private Long memberId;

    private String openId;

    private String openPlatformKey;


    private Integer status;


    /****/
    private Date createTime;

    /****/
    private Date updateTime;

    /**是否删除**/
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

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}


	public String getOpenPlatformKey() {
		return openPlatformKey;
	}

	public void setOpenPlatformKey(String openPlatformKey) {
		this.openPlatformKey = openPlatformKey;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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