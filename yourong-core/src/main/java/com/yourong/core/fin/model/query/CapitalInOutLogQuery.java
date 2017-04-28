package com.yourong.core.fin.model.query;

import com.yourong.common.domain.BaseQueryParam;


public class CapitalInOutLogQuery extends BaseQueryParam {


    /**第三方支付账户类型 1存钱罐账户 2 基本户账户 3--保证金账户**/
    private Integer payAccountType;


    /**1:充值  2:提现 3:充值手续费 4：提现手续费  5:用户投资 6：现金券支出 7：收益券支出 8:利息收支  9：本金收支 10-新浪存钱罐收益 11-满标放款**/
    private Integer type;
    
    /**来源id(充值流水id，提现流水id，支付id)**/
    private String sourceId;

	public Integer getPayAccountType() {
		return payAccountType;
	}

	public void setPayAccountType(Integer payAccountType) {
		this.payAccountType = payAccountType;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
    


}