package com.yourong.core.fin.model.query;

import com.yourong.common.domain.BaseQueryParam;


public class BalanceQuery extends BaseQueryParam {


    /**余额类型：1、第三方账户余额；2、红包余额；4、项目资金余额；5、免提现费额度；6、每天提现限定次数**/
    private Integer balanceType;


    /**来源id(用户id、项目id)**/
    private Long sourceId;


	public Integer getBalanceType() {
		return balanceType;
	}


	public void setBalanceType(Integer balanceType) {
		this.balanceType = balanceType;
	}


	public Long getSourceId() {
		return sourceId;
	}


	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

}