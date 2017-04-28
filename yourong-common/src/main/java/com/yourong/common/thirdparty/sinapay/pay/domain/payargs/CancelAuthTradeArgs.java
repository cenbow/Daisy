package com.yourong.common.thirdparty.sinapay.pay.domain.payargs;

import java.util.Map;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;

/**
 * 
 * @desc 代收撤销 交易列表参数
 * @author wangyanji 2016年5月23日下午11:28:08
 */
public class CancelAuthTradeArgs {
    /**
	 * 代收完成单笔请求号 必填
	 */
    @NotNull
	private String requestNo;

    /**
	 * 代收冻结订单号 必填
	 */
    @NotNull
	private String tradeNo;

    /**
	 * 摘要 必填
	 */
    @NotNull
	private String summary;

    /**
     * 扩展信息
     */
    private Map<String, String> extendParams;

	public String getRequestNo() {
		return requestNo;
	}

	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getTradeNo() {
		return tradeNo;
    }

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
    }

    public Map<String, String> getExtendParams() {
        return extendParams;
    }

    public void setExtendParams(Map<String, String> extendParams) {
        this.extendParams = extendParams;
    }
}
