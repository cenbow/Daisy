package com.yourong.core.tc.model.query;

import com.yourong.common.domain.BaseQueryParam;

import java.util.List;

/**
 * web转让项目查询
 * Created by XR on 2016/12/15.
 */
public class TransactionProjectBizQuery extends BaseQueryParam {
    /**
     * 状态
     */
    private Integer status;
    /**
     * 状态集合
     */
    private List<Integer> statusList;

    /**
     * 转让状态集合
     */
    private List<Integer> transferStatusList;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Integer> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Integer> statusList) {
        this.statusList = statusList;
    }

    public List<Integer> getTransferStatusList() {
        return transferStatusList;
    }

    public void setTransferStatusList(List<Integer> transferStatusList) {
        this.transferStatusList = transferStatusList;
    }
}
