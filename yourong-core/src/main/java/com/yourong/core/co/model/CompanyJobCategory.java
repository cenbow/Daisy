package com.yourong.core.co.model;

import com.yourong.common.domain.AbstractBaseObject;

import java.util.Date;

/**
 * Created by alan.zheng on 2017/3/29.
 */
public class CompanyJobCategory extends AbstractBaseObject {
    private Long id;
    /**
     * 分类名称
     */
    private String categoryName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 招聘人数
     */
    private Integer hiringCount;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getHiringCount() {
        return hiringCount;
    }

    public void setHiringCount(Integer hiringCount) {
        this.hiringCount = hiringCount;
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
