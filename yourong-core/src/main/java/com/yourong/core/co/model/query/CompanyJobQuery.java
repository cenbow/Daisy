package com.yourong.core.co.model.query;

import com.yourong.common.domain.BaseQueryParam;

/**
 * Created by alan.zheng on 2017/3/30.
 */
public class CompanyJobQuery extends BaseQueryParam {
    private Integer categoryId;

    private Integer status;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
