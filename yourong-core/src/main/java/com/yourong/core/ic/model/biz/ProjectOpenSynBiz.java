package com.yourong.core.ic.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotNull;

/**
 * 机密回调实体类
 * Created by XR on 2016/12/2.
 */
public class ProjectOpenSynBiz extends AbstractBaseObject {
	
	/**
	 * 渠道商
	 */
	private String openPlatformKey;
    /**
     * id
     */
    private Long id;
    /**
     * 标的id
     */
    private String outBizNo;
    /**
     * 项目id
     */
    private Long projectId;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 商品sku
     */
    private String sku;
    /**
     * 状态， 1-处理中，2-处理失败，3-未创建项目，4-已创建项目,5-已审核,6-拒绝上线，7已放款
     */
    private Integer status;
    /**
     * 拒绝原因
     */
    private String refuseCause;
    /**
     * 放款状态
     */
    private Integer loanStatus;
    /**
     * 放款时间
     */
    private Date loanTime;
    
	public String getOpenPlatformKey() {
		return openPlatformKey;
	}

	public void setOpenPlatformKey(String openPlatformKey) {
		this.openPlatformKey = openPlatformKey;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOutBizNo() {
        return outBizNo;
    }

    public void setOutBizNo(String outBizNo) {
        this.outBizNo = outBizNo;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRefuseCause() {
        return refuseCause;
    }

    public void setRefuseCause(String refuseCause) {
        this.refuseCause = refuseCause;
    }

    public Integer getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(Integer loanStatus) {
        this.loanStatus = loanStatus;
    }

    public Date getLoanTime() {
        return loanTime;
    }

    public void setLoanTime(Date loanTime) {
        this.loanTime = loanTime;
    }
}
