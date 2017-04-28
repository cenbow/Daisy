package com.yourong.core.ic.model.query;

import com.yourong.common.domain.BaseQueryParam;

/**
 * Created by XR on 2016/12/15.
 */
public class TransactionProjectDetailBizQuery extends BaseQueryParam {
    /**
     * 交易id
     */
    private Long transactionId;

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }
}
