package com.yourong.common.thirdparty.sinapay.pay.domain.dto;

import java.util.List;
import java.util.Map;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;
import com.yourong.common.thirdparty.sinapay.pay.domain.payargs.FinishAuthTradeArgs;

/**
 * 
 * @desc 代收完成/撤销
 * @author wangyanji 2016年5月23日下午11:31:01
 */
public class FinishAuthTradeDto extends RequestDto {

    /**
	 * 代收完成请求号 必填
	 */
    @NotNull
	private String outRequestNo;
    
    
    @NotNull
	private String userIp;

    /**
	 * 交易列表
	 */
	@NotNull
	private List<FinishAuthTradeArgs> tradeList;

    /**
     * 扩展信息
     */
    private Map<String, String> extendParam;

	public String getOutRequestNo() {
		return outRequestNo;
	}

	public void setOutRequestNo(String outRequestNo) {
		this.outRequestNo = outRequestNo;
	}

	public List<FinishAuthTradeArgs> getTradeList() {
		return tradeList;
	}

	public void setTradeList(List<FinishAuthTradeArgs> tradeList) {
		this.tradeList = tradeList;
	}

	public Map<String, String> getExtendParam() {
		return extendParam;
	}

	public void setExtendParam(Map<String, String> extendParam) {
		this.extendParam = extendParam;
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PreAuthTradeDto [outRequestNo=");
		builder.append(outRequestNo);
		builder.append(", tradeList=");
		builder.append(tradeList);
		builder.append(", userIp=");
		builder.append(userIp);
		builder.append("]");
		return builder.toString();
	}
}
