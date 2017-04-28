package com.yourong.core.ic.model.query;

import com.yourong.common.domain.BaseQueryParam;

public class LeaseBonusDetailQuery extends BaseQueryParam {

	/** 租赁结算id **/
	private Long leaseDetailId;

	public Long getLeaseDetailId() {
		return leaseDetailId;
	}

	public void setLeaseDetailId(Long leaseDetailId) {
		this.leaseDetailId = leaseDetailId;
	}

}
