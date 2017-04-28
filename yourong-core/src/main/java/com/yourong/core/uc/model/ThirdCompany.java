package com.yourong.core.uc.model;

import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;

public class ThirdCompany extends AbstractBaseObject{
	
	private static final long serialVersionUID = 5746379109990570174L;
    /****/
    private Long id;

    /**会员id**/
    private Long memberId;

    /**公司名称**/
    private String companyName;

    /** 备注**/
    private String remarks;

    /**更新时间**/
    private Date updateTime;

    /**创建时间**/
    private Date createTime;

    /**删除标记**/
    private Integer delFlag;
    
    /**标签名**/
    private String label;
    
    /**数据值**/
    private String value;

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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName == null ? null : companyName.trim();
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
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

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
    
}