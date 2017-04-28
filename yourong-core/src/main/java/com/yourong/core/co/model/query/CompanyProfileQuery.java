package com.yourong.core.co.model.query;

import com.yourong.common.domain.BaseQueryParam;

/**
 * Created by alan.zheng on 2017/3/30.
 */
public class CompanyProfileQuery extends BaseQueryParam {
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
