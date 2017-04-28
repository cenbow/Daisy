/**
 * 
 */
package com.yourong.core.ic.model.query;

import com.yourong.common.domain.BaseQueryParam;

/**
 * @desc 转让记录查询类
 * @author zhanghao
 * 2016年9月10日下午4:42:00
 */
public class TransferRecordQuery extends BaseQueryParam {
	
	private Long transactionId;

	/**
	 * @return the transactionId
	 */
	public Long getTransactionId() {
		return transactionId;
	}

	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}
	
	

}
