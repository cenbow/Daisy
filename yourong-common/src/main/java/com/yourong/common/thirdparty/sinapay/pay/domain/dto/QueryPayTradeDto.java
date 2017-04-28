package com.yourong.common.thirdparty.sinapay.pay.domain.dto;

import java.util.Map;

import com.yourong.common.thirdparty.sinapay.common.annotations.NotNull;

/**
 * <p>支付查询DTO</p>
 * @author Wallis Wang
 * @version $Id: QueryPayTradeDto.java, v 0.1 2014年6月4日 下午9:32:23 wangqiang Exp $
 */
public class QueryPayTradeDto extends RequestDto {

    /**
     * 
     */
    private static final long   serialVersionUID = 90590459457827900L;

    /**
     * 支付请求号
     * 必填 
     * <p>商户网站支付订单号，商户内部保证唯一</p>
     */
    @NotNull
    private String              outPayNo;

    /**
     * 扩展信息
     */
    private Map<String, String> extendParam;

    public String getOutPayNo() {
        return outPayNo;
    }

    public void setOutPayNo(String outPayNo) {
        this.outPayNo = outPayNo;
    }

    public Map<String, String> getExtendParam() {
        return extendParam;
    }

    public void setExtendParam(Map<String, String> extendParam) {
        this.extendParam = extendParam;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QueryPayTradeDto [outPayNo=");
		builder.append(outPayNo);
		builder.append(", extendParam=");
		builder.append(extendParam);
		builder.append("]");
		return builder.toString();
	}

}
