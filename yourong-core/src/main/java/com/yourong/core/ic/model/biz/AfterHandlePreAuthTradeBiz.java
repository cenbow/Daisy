package com.yourong.core.ic.model.biz;

import com.yourong.common.domain.AbstractBaseObject;

import java.util.Date;

/**
 * Created by XR on 2016/12/9.
 */
public class AfterHandlePreAuthTradeBiz extends AbstractBaseObject {
    /**
     * 项目id
     */
    private Long projectId;
    /**
     * 审核时间
     */
    private Date auditTime;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }
}
