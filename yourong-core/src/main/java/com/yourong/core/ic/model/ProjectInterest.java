package com.yourong.core.ic.model;

import java.math.BigDecimal;
import java.util.Date;

import com.yourong.common.domain.AbstractBaseObject;

public class ProjectInterest extends AbstractBaseObject	 {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7445580502404679053L;

	/****/
    private Long id;

    /**项目ID**/
    private Long projectId;

    /**投资下线**/
    private BigDecimal minInvest;

    /**投资上限**/
    private BigDecimal maxInvest;

    /**年化收益**/
    private BigDecimal annualizedRate;

    /****/
    private Date gmtCreated;

    /****/
    private Date gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public BigDecimal getMinInvest() {
        return minInvest;
    }

    public void setMinInvest(BigDecimal minInvest) {
        this.minInvest = minInvest;
    }

    public BigDecimal getMaxInvest() {
        return maxInvest;
    }

    public void setMaxInvest(BigDecimal maxInvest) {
        this.maxInvest = maxInvest;
    }

    public BigDecimal getAnnualizedRate() {
        return annualizedRate;
    }

    public void setAnnualizedRate(BigDecimal annualizedRate) {
        this.annualizedRate = annualizedRate;
    }

    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

}