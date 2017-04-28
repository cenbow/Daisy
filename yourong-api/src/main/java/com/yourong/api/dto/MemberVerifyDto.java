package com.yourong.api.dto;

import java.util.Date;

public class MemberVerifyDto {
	/**id**/
    private Integer id;

    /**会员号**/
    private Long memberId;

    /**认证类型：mobile identity email**/
    private String verifyType;

    /**认证提交的内容**/
    private String verifyContent;

    /**-1:取消 1：认证**/
    private Integer verifyOperate;

    /**备注**/
    private String remarks;

    /**创建时间**/
    private Date createTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getVerifyType() {
		return verifyType;
	}

	public void setVerifyType(String verifyType) {
		this.verifyType = verifyType;
	}

	public String getVerifyContent() {
		return verifyContent;
	}

	public void setVerifyContent(String verifyContent) {
		this.verifyContent = verifyContent;
	}

	public Integer getVerifyOperate() {
		return verifyOperate;
	}

	public void setVerifyOperate(Integer verifyOperate) {
		this.verifyOperate = verifyOperate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

    
    
}
