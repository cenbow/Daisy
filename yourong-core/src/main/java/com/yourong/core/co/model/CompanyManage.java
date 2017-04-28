package com.yourong.core.co.model;

import com.yourong.common.domain.AbstractBaseObject;
import com.yourong.core.bsc.model.BscAttachment;

import java.util.Date;
import java.util.List;

/**
 * Created by alan.zheng on 2017/4/1.
 */
public class CompanyManage extends AbstractBaseObject {
    private Long id;
    /**
     * 姓名
     */
    private String manageName;
    /**
     * 职位
     */
    private String manageJob;
    /**
     * 头像链接
     */
    private String manageHref;
    /**
     * 简介内容
     */
    private String manageContent;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 删除标记
     */
    private Integer delFlag;
    /**
     * 头像附件
     */
    private List<BscAttachment> attachments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getManageName() {
        return manageName;
    }

    public void setManageName(String manageName) {
        this.manageName = manageName;
    }

    public String getManageJob() {
        return manageJob;
    }

    public void setManageJob(String manageJob) {
        this.manageJob = manageJob;
    }

    public String getManageHref() {
        return manageHref;
    }

    public void setManageHref(String manageHref) {
        this.manageHref = manageHref;
    }

    public String getManageContent() {
        return manageContent;
    }

    public void setManageContent(String manageContent) {
        this.manageContent = manageContent;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
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

    public List<BscAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<BscAttachment> attachments) {
        this.attachments = attachments;
    }
}
